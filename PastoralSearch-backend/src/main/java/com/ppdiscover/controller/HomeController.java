package com.ppdiscover.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ppdiscover.PPDocument;
import com.ppdiscover.SermonDocument;
import com.ppdiscover.utils.ContentExtractor;
import com.ppdiscover.utils.SolrUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class HomeController {

    private static final Logger log = LoggerFactory.getLogger(HomeController.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/")
    public String home() {
        return "Welcome to PPDiscover API";
    }

    @GetMapping("/search")
    public ResponseEntity<String> search(@RequestParam(value = "q", required = false) String query, @RequestParam(value = "type") String type) {
        try {
            String solrResponse;
            switch (type){
                case "salmer":
                    solrResponse = SolrUtils.queryHymns(query);
                    break;
                case "praedikener":
                    solrResponse = SolrUtils.querySermons(query);
                    break;
                default:
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            JsonNode jsonNode = objectMapper.readTree(solrResponse);

            // If it's valid JSON, return it as a JSON response
            return ResponseEntity.status(HttpStatus.OK).body(jsonNode.toString());

        } catch (SolrServerException | IOException e) {
            throw new RuntimeException(e);
        }
    }


    @PostMapping("/add")
    public ResponseEntity<String> add(@RequestParam("file") MultipartFile file, @RequestParam(value = "collection", required = false) String collection, @RequestParam("type") String type) throws IOException {
        return addFileToIndex(file, collection, type);
    }

    private static ResponseEntity<String> addFileToIndex(MultipartFile file, String collection, String type) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("No file uploaded");
        }

        return switch (type) {
            case "salmer" -> addPowerpointContentToIndex(file, collection);
            case "praedikener" -> addWordContentToIndex(file);
            default -> ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        };

    }

    private static ResponseEntity<String> addWordContentToIndex(MultipartFile file) throws IOException {
        // Only process .pptx files
        if (!file.getOriginalFilename().endsWith(".docx")) {
            return ResponseEntity.badRequest().body("Please upload a valid Word file (.docx)");
        }

        SermonDocument document = ContentExtractor.getSermonDocument(file.getInputStream());
        document.setFileName(file.getOriginalFilename());

        try {
            SolrUtils.indexWordDto(document);
        } catch (SolrServerException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.ok("File indexed successfully.");
    }

    private static ResponseEntity<String> addPowerpointContentToIndex(MultipartFile file, String collection) throws IOException {
        // Only process .pptx files
        if (!file.getOriginalFilename().endsWith(".pptx")) {
            return ResponseEntity.badRequest().body("Please upload a valid PowerPoint file (.pptx)");
        }

        if (collection == null || collection.isEmpty()) {
            return ResponseEntity.badRequest().body("Please specify a valid collection");
        }


        PPDocument document = ContentExtractor.getPPDocument(file.getInputStream());

        ContentExtractor.updateDocumentWithFileInformation(document, file.getOriginalFilename());

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
    public ResponseEntity<String> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files, @RequestParam("collection") String collection, @RequestParam("type") String type) throws IOException {

        try {
            // Loop through the uploaded files
            for (MultipartFile file : files) {
                addFileToIndex(file, collection, type);
            }
            return ResponseEntity.ok("Files indexed successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to upload files: " + e.getMessage());
        }
    }
} 