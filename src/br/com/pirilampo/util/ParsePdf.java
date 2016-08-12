package br.com.pirilampo.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;

public class ParsePdf {
    public void buildHtml(String path, String html, String css) throws Exception {
        // Apply preferences and build metadata.
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter pw = PdfWriter.getInstance(document, new FileOutputStream(path));

        // Build PDF document.
        document.open();

        // set template
        XMLWorkerHelper.getInstance().parseXHtml(
                pw,
                document,
                new ByteArrayInputStream(html.getBytes()),
                new ByteArrayInputStream(css.getBytes())
        );

        document.close();
    }
}
