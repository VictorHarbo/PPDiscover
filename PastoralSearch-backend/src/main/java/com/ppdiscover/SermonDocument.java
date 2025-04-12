package com.ppdiscover;

import java.util.UUID;

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
