package com.ppdiscover.controller;

import com.ppdiscover.PPDocument;
import com.ppdiscover.utils.SolrUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;


@RestController
public class SolrController {

    @GetMapping("/solr/index")
    public String home() {
        try {
            PPDocument testDoc = new PPDocument();
            testDoc.setContent("Dette er en salme tekst");

            SolrUtils.indexPowerpointDTO(testDoc);
            return "Indexed document";
        } catch (SolrServerException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
