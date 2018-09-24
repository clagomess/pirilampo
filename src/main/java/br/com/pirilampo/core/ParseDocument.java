package br.com.pirilampo.core;

import br.com.pirilampo.bean.Indice;
import br.com.pirilampo.bean.Parametro;
import br.com.pirilampo.constant.HtmlTemplate;
import br.com.pirilampo.exception.FeatureException;
import gherkin.AstBuilder;
import gherkin.Parser;
import gherkin.TokenMatcher;
import gherkin.ast.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.commonmark.renderer.html.HtmlRenderer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
class ParseDocument {
    private GherkinDocument gd;
    private Parametro parametro;
    private File feature;
    @Getter
    private List<File> paginaHtmlAnexo;
    @Getter
    private Map<String, Indice> indice;
    private String featureId;
    @Getter
    private String featureTitulo = null;

    public ParseDocument(Parametro parametro, File feature){
        this.parametro = parametro;
        this.feature = feature;
        this.paginaHtmlAnexo = new ArrayList<>();
        this.indice = new HashMap<>();
        this.featureId = Feature.id(parametro, feature);
    }

    private void setIndiceValue(String value){
        if(!indice.containsKey(featureId)){
            indice.put(featureId, new Indice());
        }

        if(StringUtils.isNotEmpty(value) && value.length() > 3) {
            value = value.replaceAll("<(|\\/)(.+?)>", "");
            value = value.replaceAll("&lt;", "");
            value = value.replaceAll("&gt;", "");
        }

        if(StringUtils.isNotEmpty(value) && value.length() > 3) {
            indice.get(featureId).getValues().add(value);
        }
    }

    private void setIndiceName(final String name){
        if(!indice.containsKey(featureId)){
            indice.put(featureId, new Indice());
        }

        indice.get(featureId).setName(name);
    }

    public static String getFeatureHtml(Parametro parametro, File feature) throws Exception {
        ParseDocument pd = new ParseDocument(parametro, feature);

        return pd.getFeatureHtml();
    }

    public String getFeatureHtml() throws Exception {
        Parser<GherkinDocument> parser = new Parser<>(new AstBuilder());
        TokenMatcher matcher = new TokenMatcher();
        String html = null;

        try (FileInputStream fis = new FileInputStream(feature)) {
            // BOMInputStream para caso o arquivo possuir BOM
            BOMInputStream bis = new BOMInputStream(fis);

            Reader in = new InputStreamReader(bis, StandardCharsets.UTF_8);

            this.gd = parser.parse(in, matcher);

            if (this.gd != null) {
                html = getHtml();
            }

            log.info("OK: {}", feature.getAbsolutePath());
        } catch (Exception e){
            throw new FeatureException(e, feature);
        }

        return html;
    }

    private String getHtml(){
        StringBuilder html = new StringBuilder();

        if(gd != null){
            featureTitulo = gd.getFeature().getName();

            html.append(String.format(HtmlTemplate.HTML_TITULO, format(gd.getFeature().getName(), false)));
            setIndiceName(gd.getFeature().getName());

            if(gd.getFeature().getDescription() != null) {
                html.append(String.format(HtmlTemplate.HTML_PARAGRAFO, format(gd.getFeature().getDescription())));
            }

            int scenarioIdx = 0;
            for (ScenarioDefinition sd : gd.getFeature().getChildren()){
                StringBuilder body = new StringBuilder();

                if(sd.getDescription() != null){
                    body.append(String.format(HtmlTemplate.HTML_PARAGRAFO, format(sd.getDescription())));
                }

                for (Step step : sd.getSteps()){
                    body.append(String.format(HtmlTemplate.HTML_STEP, step.getKeyword(), format(step.getText())));

                    if(step.getArgument() != null){
                        if(step.getArgument() instanceof DataTable) {
                            StringBuilder htmlTrH = new StringBuilder();
                            StringBuilder htmlTrD = new StringBuilder();
                            int i = 0;

                            for (TableRow tr : ((DataTable) step.getArgument()).getRows()) {
                                StringBuilder htmlTc = new StringBuilder();
                                for (TableCell tc : tr.getCells()) {
                                    if (i == 0) {
                                        htmlTc.append(String.format(HtmlTemplate.HTML_CHILDREN_TABLE_TH, format(tc.getValue(), false)));
                                    } else {
                                        htmlTc.append(String.format(HtmlTemplate.HTML_CHILDREN_TABLE_TD, format(tc.getValue())));
                                    }
                                }

                                if (i == 0) {
                                    htmlTrH.append(String.format(HtmlTemplate.HTML_CHILDREN_TABLE_TR, htmlTc));
                                } else {
                                    htmlTrD.append(String.format(HtmlTemplate.HTML_CHILDREN_TABLE_TR, htmlTc));
                                }

                                i++;
                            }

                            body.append(String.format(HtmlTemplate.HTML_CHILDREN_TABLE, htmlTrH, htmlTrD));
                        }

                        if(step.getArgument() instanceof DocString) {
                            body.append(String.format(HtmlTemplate.HTML_CODE, format(((DocString) step.getArgument()).getContent(), false)));
                        }
                    }
                }

                if(sd instanceof ScenarioOutline) {
                    for (Examples examples : ((ScenarioOutline) sd).getExamples()){
                        body.append(String.format(HtmlTemplate.HTML_STEP, examples.getKeyword(), ":"));

                        StringBuilder htmlTrH = new StringBuilder();
                        StringBuilder htmlTrD = new StringBuilder();
                        StringBuilder htmlTc = new StringBuilder();

                        if(examples.getTableHeader() != null) {
                            for (TableCell tc : examples.getTableHeader().getCells()) {
                                htmlTc.append(String.format(HtmlTemplate.HTML_CHILDREN_TABLE_TH, format(tc.getValue(), false)));
                            }

                            htmlTrH.append(String.format(HtmlTemplate.HTML_CHILDREN_TABLE_TR, htmlTc));
                        }

                        if(examples.getTableBody() != null) {
                            for (TableRow tr : examples.getTableBody()) {
                                htmlTc = new StringBuilder();

                                for (TableCell tc : tr.getCells()) {
                                    htmlTc.append(String.format(HtmlTemplate.HTML_CHILDREN_TABLE_TD, format(tc.getValue())));
                                }

                                htmlTrD.append(String.format(HtmlTemplate.HTML_CHILDREN_TABLE_TR, htmlTc));
                            }
                        }

                        if(!"".equals(htmlTrH.toString()) || !"".equals(htmlTrD.toString())) {
                            body.append(String.format(HtmlTemplate.HTML_CHILDREN_TABLE, htmlTrH, htmlTrD));
                        }
                    }
                }

                html.append(String.format(
                        HtmlTemplate.HTML_CHILDREN,
                        scenarioIdx,
                        StringEscapeUtils.escapeHtml("".equals(sd.getName()) ? sd.getKeyword() : sd.getName()),
                        String.format(HtmlTemplate.HTML_CHILDREN_BODY, scenarioIdx, body)
                ));

                scenarioIdx++;
            }
        }

        return html.toString();
    }

    /**
     * @param txtRaw raw texto
     * @param md ativar markedow?
     * @return html
     */
    private String format(String txtRaw, boolean md){
        String txt = txtRaw;
        txt = txt.trim();

        txt = txt.replaceAll("<", "&lt;");
        txt = txt.replaceAll(">", "&gt;");

        if(md && txt.length() >= 3) {
            try {
                txt = txt.replaceAll("( *)(\\n)( *)", "\n");

                org.commonmark.parser.Parser parser = org.commonmark.parser.Parser.builder().build();
                org.commonmark.node.Node document = parser.parse(txt);
                HtmlRenderer renderer = HtmlRenderer.builder().build();
                txt = renderer.render(document);

                txt = txt.replaceFirst("^<p>(.+)<\\/p>", "$1");
                txt = txt.trim();
            } catch (Exception e) {
                log.warn(ParseDocument.class.getName(), e);
            }
        }

        final String img = "<br/><p><img src=\"$1\" $2/></p>";

        txt = txt.replaceAll("<img src=\"(.+?)\"(.*?)>", img);
        txt = txt.replaceAll("&lt;img src=&quot;(.+?)&quot;(.*?)&gt;", img);
        txt = txt.replaceAll("&lt;strike&gt;(.+?)&lt;/strike&gt;", "<strike>$1</strike>");
        txt = txt.replaceAll("&lt;br&gt;", "<br/>");

        if(txt.contains("<img")){
            txt = txt.replaceAll("&quot;", "\"");
        }

        // pega endereço ou base64 da imagem
        Pattern p = Pattern.compile("src=\"(.+?)\"");
        Matcher m = p.matcher(txt);

        while (m.find()) {
            String imgSrc = ParseImage.parse(parametro, feature, m.group(1));
            txt = txt.replace("src=\"" + m.group(1) + "\"", "src=\"" + imgSrc + "\"");
        }

        // verifica html embeded
        p = Pattern.compile("href=\"(.+)\\.html\"");
        m = p.matcher(txt);

        while(m.find()) {
            File htmlEmbed = Resource.absolute(parametro, feature, m.group(1) + ".html");

            if (htmlEmbed.isFile()) {
                paginaHtmlAnexo.add(htmlEmbed);
                String urlHtmlEmbed = "#/html/" + m.group(1) + ".html";
                txt = txt.replace("href=\""+ m.group(1) +".html\"", "href=\"" + urlHtmlEmbed + "\"");
            }
        }

        setIndiceValue(txt);

        return txt;
    }

    private String format(String txt){
        return format(txt, true);
    }
}
