package com.ppdiscover.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ppdiscover.PPDocument;
import com.ppdiscover.SermonDocument;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;

import java.io.IOException;

/**
 * Utility class for interacting with Solr.
 */
public class SolrUtils {
    // Solr server URL
    //String solrUrl = "http://host.docker.internal:8983/solr/ppdiscover";
    private static final String solrUrlHymns = "http://10.0.0.32:8983/solr/hymns";
    private static final String solrUrlSermons = "http://10.0.0.32:8983/solr/sermons";


    /**
     * Index a PowerPoint document.
     * 
     * @param document The document to index.
     * @throws SolrServerException If there is an error with the Solr server.
     * @throws IOException If there is an error with the input stream.
     */
    public static void indexPowerpointDTO(PPDocument document) throws SolrServerException, IOException {
        SolrClient solrClient = new HttpSolrClient.Builder(solrUrlHymns).build();
        // Convert the Java object into SolrInputDocument
        SolrInputDocument solrDocument = new SolrInputDocument();

        solrDocument.addField("id", document.getId());
        solrDocument.addField("content", document.getContent());

        solrDocument.addField("fileName", document.getFileName());
        solrDocument.addField("salmeNumre", document.getSalmeNumre());
        solrDocument.addField("titler", document.getSalmeTitler());
        solrDocument.addField("tekster", document.getSalmeTekster());
        solrDocument.addField("textRow", document.getTextRow());


        // Add the document to Solr index
        UpdateResponse response = solrClient.add(solrDocument);
        System.out.println("Response: " + response.getStatus());

        // Commit the changes
        solrClient.commit();

        // Close the Solr client
        solrClient.close();
    }

    /**
     * Index a word document.
     * 
     * @param document The document to index.
     * @throws SolrServerException If there is an error with the Solr server.
     * @throws IOException If there is an error with the input stream.
     */
    public static void indexWordDto(SermonDocument document) throws SolrServerException, IOException {
        SolrClient solrClient = new HttpSolrClient.Builder(solrUrlSermons).build();
        // Convert the Java object into SolrInputDocument
        SolrInputDocument solrDocument = new SolrInputDocument();

        solrDocument.addField("id", document.getId());
        solrDocument.addField("content", document.getContent());

        solrDocument.addField("fileName", document.getFileName());

        // Add the document to Solr index
        UpdateResponse response = solrClient.add(solrDocument);
        System.out.println("Response: " + response.getStatus());

        // Commit the changes
        solrClient.commit();

        // Close the Solr client
        solrClient.close();
    }

    /**
     * Query the hymns collection.
     * 
     * @param query The query to search for.
     * @return A string representation of the query response.
     * @throws SolrServerException If there is an error with the Solr server.
     * @throws IOException If there is an error with the input stream.
     */
    public static String queryHymns(String query) throws SolrServerException, IOException {
        return getString(query, solrUrlHymns);

    }

    /**
     * Query the sermons collection.
     * 
     * @param query The query to search for.
     * @return A string representation of the query response.
     * @throws SolrServerException If there is an error with the Solr server.
     * @throws IOException If there is an error with the input stream.
     */
    public static String querySermons(String query) throws SolrServerException, IOException {
        return getString(query, solrUrlSermons);
    }

    /**
     * Get a JSON string representation of the query response.
     * 
     * @param query The query to search for.
     * @param solrUrlWithCollection The Solr URL with the collection.
     * @return A JSON string representation of the query response.
     */
    private static String getString(String query, String solrUrlWithCollection) throws IOException, SolrServerException {
        try (SolrClient solrClient = new HttpSolrClient.Builder(solrUrlWithCollection).build()) {

            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setQuery(query);
            solrQuery.setRequestHandler("/select");
            solrQuery.set("wt", "json"); // Request JSON format


            QueryResponse response = solrClient.query(solrQuery);
            String jsonResponse = response.getResponse().jsonStr();

            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectMapper.readTree(jsonResponse));
        }
    }
}
