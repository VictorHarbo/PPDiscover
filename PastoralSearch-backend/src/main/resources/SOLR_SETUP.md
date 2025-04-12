# Solr Setup Guide for PPDiscover

This guide explains how to set up Solr 9 with the configuration files provided in this project.

## Prerequisites

- Solr 9.4.1 or compatible version installed
- Java 23 or higher

## Setting up Solr Core

1. Start Solr server:
   ```bash
   solr start
   ```

2. Create a new core named "ppdiscover":
   ```bash
   solr create -c ppdiscover
   ```

3. Stop Solr:
   ```bash
   solr stop
   ```

4. Copy the configuration files:
   - Copy `solr-schema.xml` to `[SOLR_HOME]/server/solr/ppdiscover/conf/schema.xml`
   - Copy `solrconfig.xml` to `[SOLR_HOME]/server/solr/ppdiscover/conf/solrconfig.xml`

5. Start Solr again:
   ```bash
   solr start
   ```

## Verifying the Setup

1. Access the Solr Admin UI at http://localhost:8983/solr/

2. Select the "ppdiscover" core from the dropdown menu

3. Go to the "Schema" tab to verify that the fields are correctly defined:
   - id (string)
   - title (text_general)
   - content (text_general)
   - author (string)
   - createdDate (date)
   - lastModifiedDate (date)
   - tags (strings)

4. Test the core by adding a sample document:
   - Go to the "Documents" tab
   - Select "Document Type" as "JSON"
   - Enter the following JSON:
     ```json
     {
       "id": "1",
       "title": "Sample Document",
       "content": "This is a sample document for testing Solr integration.",
       "author": "Test Author",
       "createdDate": "2023-01-01T00:00:00Z",
       "lastModifiedDate": "2023-01-01T00:00:00Z",
       "tags": ["test", "sample", "solr"]
     }
     ```
   - Click "Submit Document"

5. Test searching:
   - Go to the "Query" tab
   - Enter "sample" in the "q" field
   - Click "Execute Query"
   - You should see the document you just added in the results

## Troubleshooting

If you encounter issues:

1. Check Solr logs in `[SOLR_HOME]/server/logs/`

2. Verify that the schema.xml and solrconfig.xml files are correctly copied

3. Make sure the Lucene version in solrconfig.xml matches your Solr version

4. If you make changes to the configuration files, restart Solr:
   ```bash
   solr restart
   ```

## Solr 9 Specific Notes

- Solr 9 uses a new admin UI with improved features
- The default port is still 8983
- Solr 9 has improved performance and security features
- The schema API is now the preferred way to manage schemas, but the XML schema is still supported

## Additional Resources

- [Solr 9 Documentation](https://solr.apache.org/guide/solr/latest/)
- [SolrJ Documentation](https://solr.apache.org/guide/solr/latest/query-guide/solrj.html) 