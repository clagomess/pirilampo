package br.com.pirilampo.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;

public class ParsePdf {
    public void buildHtml(String path, String html, String css, String layout) throws Exception {
        // Apply preferences and build metadata.
        Document document = new Document(layout.equals("R") ? PageSize.A4 : PageSize.A4.rotate());
        PdfWriter pw = PdfWriter.getInstance(document, new FileOutputStream(path));
        pw.setPageEvent(new Rodape());

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

    private class Rodape extends PdfPageEventHelper {
        PdfTemplate total;

        public void onOpenDocument(PdfWriter writer, Document document) {
            total = writer.getDirectContent().createTemplate(30, 16);
        }

        public void onEndPage(PdfWriter writer, Document document) {
            ColumnText.showTextAligned(
                    writer.getDirectContentUnder(),
                    Element.ALIGN_CENTER,
                    new Phrase("Página " + String.valueOf(writer.getPageNumber())),
                    writer.getPageSize().getWidth() - 70, 20, 0
            );
        }
    }
}
