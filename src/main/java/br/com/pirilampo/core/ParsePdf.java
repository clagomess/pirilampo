package br.com.pirilampo.core;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.codec.Base64;
import com.itextpdf.tool.xml.XMLWorker;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.tool.xml.css.CssFile;
import com.itextpdf.tool.xml.css.StyleAttrCSSResolver;
import com.itextpdf.tool.xml.html.Tags;
import com.itextpdf.tool.xml.parser.XMLParser;
import com.itextpdf.tool.xml.pipeline.css.CSSResolver;
import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline;
import com.itextpdf.tool.xml.pipeline.end.PdfWriterPipeline;
import com.itextpdf.tool.xml.pipeline.html.AbstractImageProvider;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

@Slf4j
public class ParsePdf {
    class Base64ImageProvider extends AbstractImageProvider {
        @Override
        public Image retrieve(String src) {
            int pos = src.indexOf("base64,");
            try {
                if (src.startsWith("data") && pos > 0) {
                    byte[] img = Base64.decode(src.substring(pos + 7));
                    return Image.getInstance(img);
                }
                else {
                    return Image.getInstance(src);
                }
            } catch (BadElementException | IOException ex) {
                log.warn(ParsePdf.class.getName(), ex);
                return null;
            }
        }

        @Override
        public String getImageRootPath() {
            return null;
        }
    }

    public void buildHtml(String path, String html, String css, String layout, String painelFechado) throws Exception {
        // Apply preferences and build metadata.
        Document document = new Document(layout.equals("R") ? PageSize.A4 : PageSize.A4.rotate());
        PdfWriter pw = PdfWriter.getInstance(document, new FileOutputStream(path));
        pw.setPageEvent(new Rodape());

        // Build PDF document.
        document.open();

        // CSS
        CSSResolver cssResolver = new StyleAttrCSSResolver();
        CssFile cssFile = XMLWorkerHelper.getCSS(new ByteArrayInputStream(css.getBytes()));
        cssResolver.addCss(cssFile);

        // HTML
        HtmlPipelineContext htmlContext = new HtmlPipelineContext(null);
        htmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory());
        htmlContext.setImageProvider(new Base64ImageProvider());

        // Pipelines
        PdfWriterPipeline pdf = new PdfWriterPipeline(document, pw);
        HtmlPipeline htmlP = new HtmlPipeline(htmlContext, pdf);
        CssResolverPipeline cssP = new CssResolverPipeline(cssResolver, htmlP);

        // XML Worker
        XMLWorker worker = new XMLWorker(cssP, true);
        XMLParser p = new XMLParser(worker);
        p.parse(new ByteArrayInputStream(html.getBytes()));

        // step 5
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
