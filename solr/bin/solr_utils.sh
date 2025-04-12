#!/bin/bash

# solrUtils.sh - A utility script for managing Solr configsets
# This script uploads configsets to a Solr cloud instance

# Default configuration
CONFIG_FILE="solr_config.conf"
DEFAULT_SOLR_URL="http://localhost:8983/solr"
DEFAULT_CONFIGSETS_DIR="."

# Function to display usage information
function show_usage {
    echo "Usage: $0 [options]"
    echo "Options:"
    echo "  -c, --config FILE    Specify configuration file (default: $CONFIG_FILE)"
    echo "  -u, --url URL        Specify Solr URL directly (overrides config file)"
    echo "  -d, --dir DIR        Specify configsets directory (default: $DEFAULT_CONFIGSETS_DIR)"
    echo "  -t, --test           Test connection to Solr without performing any other operations"
    echo "  -a, --auto           Automatically upload configset and create collection"
    echo "  -n, --name NAME      Collection name (required with --auto)"
    echo "  -s, --set SET        Configset name (required with --auto)"
    echo "  -h, --help           Display this help message"
    echo ""
    echo "Example:"
    echo "  $0 -c my_config.conf"
    echo "  $0 -u http://solr-server:8983/solr -d ./my_configsets"
    echo "  $0 -t -u http://solr-server:8983/solr"
    echo "  $0 -a -s my_configset -n my_collection"
}

# Function to read configuration from file
function read_config {
    if [ -f "$CONFIG_FILE" ]; then
        echo "Reading configuration from $CONFIG_FILE"
        # Read SOLR_URL from config file if it exists
        if grep -q "SOLR_URL=" "$CONFIG_FILE"; then
            SOLR_URL=$(grep "SOLR_URL=" "$CONFIG_FILE" | cut -d'=' -f2)
            echo "Found Solr URL in config: $SOLR_URL"
        fi
        
        # Read CONFIGSETS_DIR from config file if it exists
        if grep -q "CONFIGSETS_DIR=" "$CONFIG_FILE"; then
            CONFIGSETS_DIR=$(grep "CONFIGSETS_DIR=" "$CONFIG_FILE" | cut -d'=' -f2)
            echo "Found configsets directory in config: $CONFIGSETS_DIR"
        fi
    else
        echo "Configuration file $CONFIG_FILE not found. Using defaults."
    fi
}

# Function to check Solr connection
function check_solr_connection {
    local solr_url=$1
    local verbose=${2:-true}
    
    if [ "$verbose" = true ]; then
        echo "Checking Solr connection at $solr_url..."
    fi
    
    # Check if curl is available
    if ! command -v curl &> /dev/null; then
        echo "Error: curl is required but not installed."
        return 1
    fi
    
    # Try to connect to Solr
    local response=$(curl -s "$solr_url/admin/collections?action=LIST")
    
    # Check if the response contains a valid Solr response
    if echo "$response" | grep -q "responseHeader"; then
        if [ "$verbose" = true ]; then
            echo "Connected to Solr successfully."
        fi
        return 0
    else
        if [ "$verbose" = true ]; then
            echo "Error: Cannot connect to Solr at $solr_url"
            echo "Please check if Solr is running and the URL is correct."
        fi
        return 1
    fi
}

# Function to upload a configset and create a collection
function upload_and_create {
    local configset_name=$1
    local collection_name=$2
    local solr_url=$3
    local configsets_dir=$4
    
    echo "Uploading configset '$configset_name' and creating collection '$collection_name'..."
    
    # Check if configset directory exists
    local configset_path="$configsets_dir/$configset_name"
    if [ ! -d "$configset_path" ]; then
        echo "Error: Configset directory $configset_path does not exist."
        return 1
    fi
    
    # Create a temporary zip file of the configset
    local temp_zip=$(mktemp)
    (cd "$configset_path" && zip -r "$temp_zip" .)
    
    # Upload the configset
    echo "Uploading configset: $configset_name"
    local upload_response=$(curl -s -X POST "$solr_url/admin/configs?action=UPLOAD&name=$configset_name" \
        -H "Content-Type: application/octet-stream" \
        --data-binary @"$temp_zip")
    
    # Clean up the temporary zip file
    rm "$temp_zip"
    
    # Check if the upload was successful
    if ! echo "$upload_response" | grep -q "status=0"; then
        echo "Error uploading configset: $configset_name"
        echo "Response: $upload_response"
        return 1
    fi
    
    echo "Successfully uploaded configset: $configset_name"
    
    # Create collection using the configset
    echo "Creating collection: $collection_name using configset: $configset_name"
    local create_response=$(curl -s "$solr_url/admin/collections?action=CREATE&name=$collection_name&numShards=1&replicationFactor=1&collection.configName=$configset_name")
    
    # Check if the creation was successful
    if ! echo "$create_response" | grep -q "status=0"; then
        echo "Error creating collection: $collection_name"
        echo "Response: $create_response"
        return 1
    fi
    
    echo "Successfully created collection: $collection_name"
    return 0
}

# Parse command line arguments
SOLR_URL=$DEFAULT_SOLR_URL
CONFIGSETS_DIR=$DEFAULT_CONFIGSETS_DIR
TEST_CONNECTION_ONLY=false
AUTO_MODE=false
COLLECTION_NAME=""
CONFIGSET_NAME=""

while [[ $# -gt 0 ]]; do
    case "$1" in
        -c|--config)
            CONFIG_FILE="$2"
            shift 2
            ;;
        -u|--url)
            SOLR_URL="$2"
            shift 2
            ;;
        -d|--dir)
            CONFIGSETS_DIR="$2"
            shift 2
            ;;
        -t|--test)
            TEST_CONNECTION_ONLY=true
            shift
            ;;
        -a|--auto)
            AUTO_MODE=true
            shift
            ;;
        -n|--name)
            COLLECTION_NAME="$2"
            shift 2
            ;;
        -s|--set)
            CONFIGSET_NAME="$2"
            shift 2
            ;;
        -h|--help)
            show_usage
            exit 0
            ;;
        *)
            echo "Unknown option: $1"
            show_usage
            exit 1
            ;;
    esac
done

# Read configuration from file (will be overridden by command line arguments if provided)
read_config

# Check Solr connection
if ! check_solr_connection "$SOLR_URL"; then
    exit 1
fi

# If test connection only, exit here
if [ "$TEST_CONNECTION_ONLY" = true ]; then
    echo "Connection test completed successfully."
    exit 0
fi

# If auto mode is enabled, upload configset and create collection
if [ "$AUTO_MODE" = true ]; then
    # Check if required parameters are provided
    if [ -z "$CONFIGSET_NAME" ]; then
        echo "Error: Configset name is required in auto mode. Use --set option."
        exit 1
    fi
    
    if [ -z "$COLLECTION_NAME" ]; then
        echo "Error: Collection name is required in auto mode. Use --name option."
        exit 1
    fi
    
    # Upload configset and create collection
    if upload_and_create "$CONFIGSET_NAME" "$COLLECTION_NAME" "$SOLR_URL" "$CONFIGSETS_DIR"; then
        echo "Operation completed successfully."
        exit 0
    else
        echo "Operation failed."
        exit 1
    fi
fi

# Function to upload a configset
function upload_configset {
    local configset_name=$1
    local configset_path="$CONFIGSETS_DIR/$configset_name"
    
    if [ ! -d "$configset_path" ]; then
        echo "Error: Configset directory $configset_path does not exist."
        return 1
    fi
    
    echo "Uploading configset: $configset_name"
    
    # Create a temporary zip file of the configset
    local temp_zip=$(mktemp)
    (cd "$configset_path" && zip -r "$temp_zip" .)
    
    # Upload the configset
    local response=$(curl -s -X POST "$SOLR_URL/admin/configs?action=UPLOAD&name=$configset_name" \
        -H "Content-Type: application/octet-stream" \
        --data-binary @"$temp_zip")
    
    # Clean up the temporary zip file
    rm "$temp_zip"
    
    # Check if the upload was successful
    if echo "$response" | grep -q "status=0"; then
        echo "Successfully uploaded configset: $configset_name"
        return 0
    else
        echo "Error uploading configset: $configset_name"
        echo "Response: $response"
        return 1
    fi
}

# Function to create a collection using a configset
function create_collection {
    local collection_name=$1
    local configset_name=$2
    
    echo "Creating collection: $collection_name using configset: $configset_name"
    
    local response=$(curl -s "$SOLR_URL/admin/collections?action=CREATE&name=$collection_name&numShards=1&replicationFactor=1&collection.configName=$configset_name")
    
    # Check if the creation was successful
    if echo "$response" | grep -q "status=0"; then
        echo "Successfully created collection: $collection_name"
        return 0
    else
        echo "Error creating collection: $collection_name"
        echo "Response: $response"
        return 1
    fi
}

# Main execution
echo "Solr URL: $SOLR_URL"
echo "Configsets directory: $CONFIGSETS_DIR"

# Check if the configsets directory exists
if [ ! -d "$CONFIGSETS_DIR" ]; then
    echo "Error: Configsets directory $CONFIGSETS_DIR does not exist."
    exit 1
fi

# List available configsets
echo "Available configsets:"
configsets=($(ls -d "$CONFIGSETS_DIR"/*/ 2>/dev/null | xargs -n 1 basename))
if [ ${#configsets[@]} -eq 0 ]; then
    echo "No configsets found in $CONFIGSETS_DIR"
    exit 1
fi

for configset in "${configsets[@]}"; do
    echo "  - $configset"
done

# Ask user which configsets to upload
echo ""
echo "Enter the names of configsets to upload (comma-separated, or 'all' for all configsets):"
read -r selected_configsets

if [ "$selected_configsets" = "all" ]; then
    selected_configsets=${configsets[*]}
fi

# Upload selected configsets
for configset in $(echo "$selected_configsets" | tr ',' ' '); do
    configset=$(echo "$configset" | xargs)  # Trim whitespace
    if [ -n "$configset" ]; then
        upload_configset "$configset"
        
        # Ask if user want to create a collection with this configset
        echo "Do you want to create a collection using this configset? (y/n)"
        read -r create_collection_response
        
        if [[ "$create_collection_response" =~ ^[Yy]$ ]]; then
            echo "Enter collection name:"
            read -r collection_name
            create_collection "$collection_name" "$configset"
        fi
    fi
done

echo "Operation completed."
