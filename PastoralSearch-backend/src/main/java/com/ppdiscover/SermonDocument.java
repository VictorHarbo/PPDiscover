package com.ppdiscover;

import java.util.UUID;

/**
 * Object to represent a sermon document with content and associated file name.
 */
public class SermonDocument {
    final private String id;

    private String fileName;
    private String content;

    public SermonDocument() {
        this.id = UUID.randomUUID().toString();
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }
}
