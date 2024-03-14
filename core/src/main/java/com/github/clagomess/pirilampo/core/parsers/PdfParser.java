package com.github.clagomess.pirilampo.core.parsers;

import com.github.clagomess.pirilampo.core.dto.ParametersDto;
import com.github.clagomess.pirilampo.core.enums.LayoutPdfEnum;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
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
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Slf4j
public class PdfParser {
    private final ParametersDto parameters;
    private final InputStream css;
    private final PdfImageProvider pdfImageProvider;

    private Document document;
    private XMLParser xmlParser;

    public PdfParser(ParametersDto parameters, InputStream css) {
        this.parameters = parameters;
        this.css = css;
        this.pdfImageProvider = new PdfImageProvider(parameters);
    }

    public void initDocument(
            OutputStream file
    ) throws Exception {
        document = new Document(parameters.getLayoutPdf() == LayoutPdfEnum.PORTRAIT ?
                PageSize.A4 :
                PageSize.A4.rotate()
        );

        document.addTitle(this.parameters.getProjectName());
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
        htmlContext.setImageProvider(pdfImageProvider);

        // Pipelines
        PdfWriterPipeline pdf = new PdfWriterPipeline(document, pw);
        HtmlPipeline htmlP = new HtmlPipeline(htmlContext, pdf);
        CssResolverPipeline cssP = new CssResolverPipeline(cssResolver, htmlP);

        // XML Worker
        xmlParser = new XMLParser(new XMLWorker(cssP, true));
    }

    public void addFeatureHTML(File currentFeature, InputStream html) throws IOException {
        pdfImageProvider.setCurrentFeature(currentFeature);
        xmlParser.parse(html);
    }

    public void addHTML(InputStream html) throws IOException {
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
