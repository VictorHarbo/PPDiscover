package com.ppdiscover.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ppdiscover.PPDocument;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;

import java.io.IOException;

public class SolrUtils {

    public static void indexPowerpointDTO(PPDocument document) throws SolrServerException, IOException {
        // Solr server URL
        String solrUrl = "http://localhost:8983/solr/ppdiscover"; // Replace with your core name
        SolrClient solrClient = new HttpSolrClient.Builder(solrUrl).build();

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


    public static String query(String query) throws SolrServerException, IOException {
        // Solr server URL
        String solrUrl = "http://localhost:8983/solr/ppdiscover"; // Replace with your core name
        SolrClient solrClient = new HttpSolrClient.Builder(solrUrl).build();

        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery(query);
        solrQuery.setRequestHandler("/select");
        solrQuery.set("wt", "json"); // Request JSON formats


        QueryResponse response =  solrClient.query(solrQuery);

        // Get the response as JSON
        String jsonResponse = response.getResponse().jsonStr();

        // If you need to format the JSON nicely, you can use Jackson ObjectMapper:
        ObjectMapper objectMapper = new ObjectMapper();
        String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectMapper.readTree(jsonResponse));

        return prettyJson;
    }
}
