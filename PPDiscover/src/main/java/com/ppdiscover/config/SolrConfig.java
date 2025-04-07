package com.ppdiscover.config;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SolrConfig {

    @Value("${solr.url:http://localhost:8983/solr}")
    private String solrUrl;
    
    @Value("${solr.connection.timeout:5000}")
    private int connectionTimeout;
    
    @Value("${solr.socket.timeout:10000}")
    private int socketTimeout;

    @Bean
    public SolrClient solrClient() {
        return new HttpSolrClient.Builder(solrUrl)
                .withConnectionTimeout(connectionTimeout)
                .withSocketTimeout(socketTimeout)
                .build();
    }
} 