package com.ppdiscover.utils;

import com.ppdiscover.PPDocument;
import com.ppdiscover.SermonDocument;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.eclipse.jetty.http.HttpTester;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContentExtractor {

    // Define the regex pattern
    private static String salmeRegex = "^Salme (\\d+): (.+)$";

    // Compile the pattern
    private static Pattern salmePattern = Pattern.compile(salmeRegex);

    public static PPDocument extractToObject(String filePath) throws FileNotFoundException {
        FileInputStream inputStream = new FileInputStream(filePath);

        return getPPDocument(inputStream);
    }

    public static SermonDocument getSermonDocument(String filePath) throws FileNotFoundException {
        FileInputStream inputStream = new FileInputStream(filePath);

        return getSermonDocument(inputStream);
    }

    public static SermonDocument getSermonDocument(InputStream inputStream) {
        SermonDocument sermonDocument = new SermonDocument();

        try {
            XWPFDocument document = new XWPFDocument(inputStream);

            XWPFWordExtractor extractor = new XWPFWordExtractor(document);

            String text = extractor.getText();
            System.out.println("Text from .docx:\n" + text);


            sermonDocument.setContent(text);
            return sermonDocument;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


        public static PPDocument getPPDocument(InputStream inputStream) {
        PPDocument ppDocument = new PPDocument();

        try {
            // Load the PowerPoint file
            XMLSlideShow ppt = new XMLSlideShow(inputStream);

            // Iterate through each slide
            for (XSLFSlide slide : ppt.getSlides()) {
                //System.out.println("Slide " + (ppt.getSlides().indexOf(slide) + 1));

                // Iterate through shapes on the slide
                for (XSLFShape shape : slide.getShapes()) {
                    // Check if the shape is a text shape
                    if (shape instanceof XSLFTextShape) {
                        XSLFTextShape textShape = (XSLFTextShape) shape;
                        // Extract text from the text shape
                        String text = textShape.getText();
                        ppDocument.appendContent(text);
                        //System.out.println("Text: " + text);

                        Matcher salmeMatcher = salmePattern.matcher(text);

                        if (salmeMatcher.find()) {
                            ppDocument.addSalmeNummer(Integer.parseInt(salmeMatcher.group(1)));
                            ppDocument.addSalmeTitel(salmeMatcher.group(2));
                        }
                    }
                }
            }

            inputStream.close();

            return ppDocument;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void updateDocumentWithFileInformation(PPDocument document, String originalFilename) {
        document.setFileName(originalFilename);
    }

}
