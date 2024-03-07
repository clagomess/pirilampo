package com.github.clagomess.pirilampo.core.parsers;

import com.github.clagomess.pirilampo.core.dto.ParametersDto;
import com.github.clagomess.pirilampo.core.enums.LayoutPdfEnum;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.codec.Base64;
import com.itextpdf.tool.xml.XMLWorker;
import com.itextpdf.tool.xml.XMLWorkerFontProvider;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.tool.xml.css.StyleAttrCSSResolver;
import com.itextpdf.tool.xml.html.CssAppliersImpl;
import com.itextpdf.tool.xml.html.Tags;
import com.itextpdf.tool.xml.parser.XMLParser;
import com.itextpdf.tool.xml.pipeline.css.CSSResolver;
import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline;
import com.itextpdf.tool.xml.pipeline.end.PdfWriterPipeline;
import com.itextpdf.tool.xml.pipeline.html.AbstractImageProvider;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Slf4j
@RequiredArgsConstructor
public class PdfParser {
    private final ParametersDto parameters;
    private final InputStream css;

    private Document document;
    private XMLParser xmlParser;

    private static class Base64ImageProvider extends AbstractImageProvider {
        @Override
        public Image retrieve(String src) {
            log.info("- Processing image: {}", src);
            /* @TODO: improve performace
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
                log.warn(log.getName(), ex);
                return null;
            }
            */
            return null;
        }

        @Override
        public String getImageRootPath() {
            return null;
        }
    }

    public void initDocument(
            OutputStream file
    ) throws Exception {
        document = new Document(parameters.getLayoutPdf() == LayoutPdfEnum.PORTRAIT ?
                PageSize.A4 :
                PageSize.A4.rotate()
        );

        // document.addTitle(nomRelatorio); //@TODO: check this
        document.addCreationDate();
        document.addCreator("Pirilampo");

        PdfWriter pw = PdfWriter.getInstance(document, file);
        pw.setPageEvent(new Rodape());

        // Build PDF document.
        document.open();

        // CSS
        CSSResolver cssResolver = new StyleAttrCSSResolver();
        cssResolver.addCss(XMLWorkerHelper.getCSS(css));

        // HTML
        XMLWorkerFontProvider fontProvider = new XMLWorkerFontProvider(XMLWorkerFontProvider.DONTLOOKFORFONTS);
        HtmlPipelineContext htmlContext = new HtmlPipelineContext(new CssAppliersImpl(fontProvider));
        htmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory());
        htmlContext.setImageProvider(new Base64ImageProvider());

        // Pipelines
        PdfWriterPipeline pdf = new PdfWriterPipeline(document, pw);
        HtmlPipeline htmlP = new HtmlPipeline(htmlContext, pdf);
        CssResolverPipeline cssP = new CssResolverPipeline(cssResolver, htmlP);

        // XML Worker
        xmlParser = new XMLParser(new XMLWorker(cssP, true));
    }

    public void addFeatureHTML(InputStream html) throws IOException {
        xmlParser.parse(html);
    }

    public void closeDocument(){
        document.close();
    }

    private static class Rodape extends PdfPageEventHelper {
        PdfTemplate total;

        public void onOpenDocument(PdfWriter writer, Document document) {
            total = writer.getDirectContent().createTemplate(30, 16);
        }

        public void onEndPage(PdfWriter writer, Document document) {
            ColumnText.showTextAligned(
                    writer.getDirectContentUnder(),
                    Element.ALIGN_CENTER,
                    new Phrase("Page " + writer.getPageNumber()),
                    writer.getPageSize().getWidth() - 70, 20, 0
            );
        }
    }
}
