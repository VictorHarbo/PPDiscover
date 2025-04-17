package com.ppdiscover;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Object to represent a Powerpoint document with content and associated psalm numbers and titles.
 */
public class PPDocument {
    final private String id;
    private String content = "";

    private List<Integer> salmeNumre = new ArrayList<>();
    private List<String> salmeTitler = new ArrayList<>();
    private List<String>salmeTekster = new ArrayList<>();

    private String fileName;
    private String textRow;

    /**
     * Constructor for PPDocument.
     * 
     * Generates a unique ID for the document.
     */
    public PPDocument() {
        id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<Integer> getSalmeNumre() {
        return salmeNumre;
    }

    public void setSalmeNumre(List<Integer> salmeNumre) {
        this.salmeNumre = salmeNumre;
    }

    public List<String> getSalmeTitler() {
        return salmeTitler;
    }

    public void setSalmeTitler(List<String> salmeTitler) {
        this.salmeTitler = salmeTitler;
    }

    public List<String> getSalmeTekster() {
        return salmeTekster;
    }

    public void setSalmeTekster(List<String> salmeTekster) {
        this.salmeTekster = salmeTekster;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Adds a new salme entry to the document.
     * 
     * @param salmeNummer The number of the psalm.
     * @param salmeTitel The title of the psalm.
     * @param salmeTekst The text of the psalm.
     */
    public void addSalmeEntry(Integer salmeNummer, String salmeTitel, String salmeTekst) {
        this.salmeNumre.add(salmeNummer);
        this.salmeTitler.add(salmeTitel);
        this.salmeTekster.add(salmeTekst);
    }

    public void addSalmeNummer(Integer salmeNummer) {
        this.salmeNumre.add(salmeNummer);
    }

    public void addSalmeTitel(String salmeTitel) {
        this.salmeTitler.add(salmeTitel);
    }

    public void addSalmeTekst(String salmeTekst) {
        this.salmeTekster.add(salmeTekst);
    }

    /**
     * Appends content to the content field of the java object.
     * 
     * @param content The content to append.
     */
    public void appendContent(String content){
        this.content += content +  " ";
    }

    public String getTextRow() {
        return textRow;
    }

    public void setTextRow(String textRow) {
        this.textRow = textRow;
    }

    @Override
    public String toString() {
        return "PPDocument{" +
                "id='" + id + '\'' +
                ", content='" + content + '\'' +
                ", salmeNumre=" + salmeNumre +
                ", salmeTitler=" + salmeTitler +
                ", salmeTekster=" + salmeTekster +
                ", fileLocation='" + fileName + '\'' +
                ", textRow='" + textRow + '\'' +
                '}';
    }
}
