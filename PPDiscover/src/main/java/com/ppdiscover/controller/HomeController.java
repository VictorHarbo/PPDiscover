package com.ppdiscover.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ppdiscover.PPDocument;
import com.ppdiscover.utils.PPExtractor;
import com.ppdiscover.utils.SolrUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;

@RestController
public class HomeController {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/")
    public String home() {
        return "Welcome to PPDiscover API";
    }

    @GetMapping("/search")
    public ResponseEntity<String> search(@RequestParam(value = "q", required = false) String query) {
        try {

            String solrResponse = SolrUtils.query(query);

            JsonNode jsonNode = objectMapper.readTree(solrResponse);

            // If it's valid JSON, return it as a JSON response
            return ResponseEntity.status(HttpStatus.OK).body(jsonNode.toString());

        } catch (SolrServerException | IOException e) {
            throw new RuntimeException(e);
        }
    }


    @PostMapping("/add")
    public ResponseEntity<String> add(@RequestParam("file") MultipartFile file, @RequestParam("collection") String collection) throws IOException {
        return addFileToIndex(file, collection);
    }

    private static ResponseEntity<String> addFileToIndex(MultipartFile file, String collection) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("No file uploaded");
        }

        // Only process .pptx files
        if (!file.getOriginalFilename().endsWith(".pptx")) {
            return ResponseEntity.badRequest().body("Please upload a valid PowerPoint file (.pptx)");
        }


        PPDocument document = PPExtractor.getPPDocument(file.getInputStream());

        PPExtractor.updateDocumentWithFileInformation(document, file.getOriginalFilename());

        switch (collection){
            case "firstRow":
                document.setTextRow("Første Række");
                break;
            case "secondRow":
                document.setTextRow("Anden Række");
                break;
            default:
                document.setTextRow("UNDEFINED");
                break;
        }

        try {
            SolrUtils.indexPowerpointDTO(document);
        } catch (SolrServerException e) {
            throw new RuntimeException(e);
        }


        return ResponseEntity.ok("File indexed successfully.");
    }

    // Endpoint for handling the file upload
    @PostMapping("/addMultiple")
    public ResponseEntity<String> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files, @RequestParam("collection") String collection) throws IOException {

        try {
            // Loop through the uploaded files
            for (MultipartFile file : files) {
                addFileToIndex(file, collection);
            }
            return ResponseEntity.ok("Files indexed successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to upload files: " + e.getMessage());
        }
    }
} 