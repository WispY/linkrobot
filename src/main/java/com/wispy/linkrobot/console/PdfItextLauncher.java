package com.wispy.linkrobot.console;

import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfReader;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collection;
import java.util.Collections;

import static com.itextpdf.text.pdf.PdfAnnotation.PdfImportedLink;

/**
 * @author Leonid_Poliakov
 */
public class PdfItextLauncher {
    public static void main(String[] args) throws Exception {
        File pdfFile = new File("D:/sample.pdf");
        PdfReader reader = new PdfReader(new FileInputStream(pdfFile));
        int numberOfPages = reader.getNumberOfPages();
        for (int pageIndex = 1; pageIndex <= numberOfPages; pageIndex++) {
            for (PdfImportedLink link : notEmpty(reader.getLinks(pageIndex))) {
                PdfObject annotationParameter = link.getParameters().get(PdfName.A);
                if (annotationParameter instanceof PdfDictionary) {
                    PdfDictionary annotationDictionary = (PdfDictionary) annotationParameter;
                    System.out.println(annotationDictionary.getAsString(PdfName.URI));
                }
            }
        }
    }

    private static <T> Collection<T> notEmpty(Collection<T> input) {
        return input == null ? Collections.emptyList() : input;
    }
}