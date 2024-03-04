package br.com.pirilampo.core.compilers;

import br.com.pirilampo.core.dto.ParametroDto;
import br.com.pirilampo.core.enums.PainelEnum;
import br.com.pirilampo.core.exception.FeatureException;
import gherkin.AstBuilder;
import gherkin.Parser;
import gherkin.TokenMatcher;
import gherkin.ast.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
class ParseDocument extends Compiler {
    private final ParseImage parseImage = new ParseImage();
    private final ParseToMarkdown parseToMarkdown = new ParseToMarkdown();
    private final ParametroDto parametro;
    private final File feature;

    @Getter
    private final List<File> paginaHtmlAnexo;

    @Getter
    private final Set<String> featureIndexValues = new LinkedHashSet<>();

    @Getter
    private String featureTitulo = null;

    private static final String HTML_TITULO = "<h2>%s</h2>\n";
    private static final String HTML_PARAGRAFO = "<p>%s</p>\n";
    private static final String HTML_STEP = "<p><span class=\"keyword\">%s</span> %s</p>\n";

    private static final String HTML_OPEN_CHILDREN = "<div class=\"panel panel-default\">\n" +
            "<div class=\"panel-heading\" style=\"cursor: pointer;\" data-toggle=\"collapse\" data-target=\"#scenario-%s\"><h3>%s</h3></div>";

    private static final String HTML_OPEN_CHILDREN_BODY = "<div id=\"scenario-%s\" class=\"panel-body collapse in\">";
    private static final String HTML_OPEN_CHILDREN_BODY_CLOSED = "<div id=\"scenario-%s\" class=\"panel-body collapse\">";

    private static final String HTML_CLOSE_CHILDREN = "</div>";
    private static final String HTML_CLOSE_CHILDREN_BODY = "</div>";


    private static final String HTML_OPEN_CHILDREN_TABLE = "<div class=\"table-responsive\">\n" +
            "<table class=\"table table-condensed table-bordered table-hover table-striped\">\n";

    private static final String HTML_CLOSE_CHILDREN_TABLE = "</table></div>";

    private static final String HTML_CHILDREN_TABLE_TH = "<th>%s</th>\n";
    private static final String HTML_CHILDREN_TABLE_TD = "<td>%s</td>\n";

    public ParseDocument(ParametroDto parametro, File feature){
        this.parametro = parametro;
        this.feature = feature;
        this.paginaHtmlAnexo = new ArrayList<>();
    }

    protected String putIndexValue(String value){
        if(StringUtils.isBlank(value) || value.length() < 3) return null;

        value = value.replaceAll("<.+?>", "");
        value = value.replace("&lt;", "");
        value = value.replace("&gt;", "");
        value = StringUtils.trimToNull(value);

        if(StringUtils.isNotBlank(value) && value.length() > 3) {
            featureIndexValues.add(value);
            return value;
        }else{
            return null;
        }
    }

    public void build(PrintWriter out) throws Exception {
        try (
                FileInputStream fis = new FileInputStream(feature);
                BOMInputStream bis = new BOMInputStream(fis);
                Reader in = new InputStreamReader(bis, StandardCharsets.UTF_8);
        ) {
            log.info("COMPILING: {}", feature.getAbsolutePath());

            GherkinDocument gd = new Parser<>(new AstBuilder()).parse(in, new TokenMatcher());
            if (gd != null){
                featureTitulo = gd.getFeature().getName();
                if(featureTitulo == null) throw new FeatureException("Feature without title", feature);
                build(gd, out);
            }

            log.info("OK: {}", feature.getAbsolutePath());
        } catch (Throwable e) {
            throw new FeatureException(e, feature);
        }
    }

    private void parseStepDataTable(DataTable dataTable, PrintWriter out){
        if(dataTable.getRows().isEmpty()) return;

        out.print(HTML_OPEN_CHILDREN_TABLE);

        out.print("<thead><tr>");

        for (TableCell tc : dataTable.getRows().get(0).getCells()) {
            out.print(String.format(HTML_CHILDREN_TABLE_TH, format(tc.getValue(), false)));
        }

        out.print("</tr></thead>");

        out.print("<tbody>");
        int i = 0;
        for (TableRow tr : dataTable.getRows()) {
            if(i++ == 0) continue;

            out.print("<tr>");
            for (TableCell tc : tr.getCells()) {
                out.print(String.format(HTML_CHILDREN_TABLE_TD, format(tc.getValue())));
            }
            out.print("</tr>");
        }

        out.print("</tbody>");
        out.print(HTML_CLOSE_CHILDREN_TABLE);
    }

    private void parseStepDocString(DocString docString, PrintWriter out){
        out.print("<pre>");
        out.print(format(docString.getContent(), false));
        out.print("</pre>");
    }

    private void parseScenarioOutlineExamples(Examples examples, PrintWriter out){
        out.print(String.format(HTML_STEP, examples.getKeyword(), ":"));
        out.print(HTML_OPEN_CHILDREN_TABLE);

        if(examples.getTableHeader() != null) {
            out.print("<thead><tr>");

            for (TableCell tc : examples.getTableHeader().getCells()) {
                out.print(String.format(HTML_CHILDREN_TABLE_TH, format(tc.getValue(), false)));
            }

            out.print("</tr></thead>");
        }

        if(examples.getTableBody() != null) {
            out.print("<tbody>");

            for (TableRow tr : examples.getTableBody()) {
                out.print("<tr>");

                for (TableCell tc : tr.getCells()) {
                    out.print(String.format(HTML_CHILDREN_TABLE_TD, format(tc.getValue())));
                }
                out.print("</tr>");
            }

            out.print("</tbody>");
        }

        out.print(HTML_CLOSE_CHILDREN_TABLE);
    }

    private void build(GherkinDocument gd, PrintWriter out){
        out.print(String.format(HTML_TITULO, format(gd.getFeature().getName(), false)));

        if(StringUtils.isNotBlank(gd.getFeature().getDescription())) {
            out.print(String.format(HTML_PARAGRAFO, format(gd.getFeature().getDescription())));
        }

        int scenarioIdx = 0;
        for (ScenarioDefinition sd : gd.getFeature().getChildren()){
            out.print(String.format(
                    HTML_OPEN_CHILDREN,
                    scenarioIdx,
                    StringEscapeUtils.escapeHtml(StringUtils.isBlank(sd.getName()) ? sd.getKeyword() : sd.getName())
            ));

            if (parametro.getTipPainel() == PainelEnum.FECHADO) {
                out.print(String.format(HTML_OPEN_CHILDREN_BODY_CLOSED, scenarioIdx));
            }else{
                out.print(String.format(HTML_OPEN_CHILDREN_BODY, scenarioIdx));
            }

            if(StringUtils.isNotBlank(sd.getDescription())){
                out.print(String.format(HTML_PARAGRAFO, format(sd.getDescription())));
            }

            for (Step step : sd.getSteps()){
                out.print(String.format(HTML_STEP, step.getKeyword(), format(step.getText())));
                if(step.getArgument() == null) continue;

                if(step.getArgument() instanceof DataTable) {
                    parseStepDataTable((DataTable) step.getArgument(), out);
                }

                if(step.getArgument() instanceof DocString) {
                    parseStepDocString((DocString) step.getArgument(), out);
                }
            }

            if(sd instanceof ScenarioOutline) {
                for (Examples examples : ((ScenarioOutline) sd).getExamples()){
                    parseScenarioOutlineExamples(examples, out);
                }
            }

            out.print(HTML_CLOSE_CHILDREN_BODY);
            out.print(HTML_CLOSE_CHILDREN);

            scenarioIdx++;
        }
    }

    protected String format(String txtRaw, boolean makdown){
        String txt = txtRaw.trim()
                .replace("<", "&lt;")
                .replace(">", "&gt;");

        if(txt.length() < 3) return txt;

        if(makdown) txt = parseToMarkdown.build(txt);

        final String img = "<br/><p><img src=\"$1\" $2/></p>";

        txt = txt.replaceAll("<img src=\"(.+?)\"(.*?)>", img);
        txt = txt.replaceAll("&lt;img src=&quot;(.+?)&quot;(.*?)&gt;", img);
        txt = txt.replaceAll("&lt;strike&gt;(.+?)&lt;/strike&gt;", "<strike>$1</strike>");
        txt = txt.replace("&quot;", "\"");
        txt = txt.replace("&lt;br&gt;", "<br/>");

        putIndexValue(txt);

        // pega endereço ou base64 da imagem
        Matcher mImgSrc = Pattern.compile("src=\"(.+?)\"").matcher(txt);
        while (mImgSrc.find()) {
            String imgSrc = parseImage.parse(parametro, feature, mImgSrc.group(1));
            txt = txt.replace(mImgSrc.group(), "src=\"" + imgSrc + "\"");
        }

        // verifica html embeded
        Matcher mHtmlHref = Pattern.compile("href=\"(.+?\\.html)\"").matcher(txt);
        while(mHtmlHref.find()) {
            String filename = mHtmlHref.group(1);
            File htmlEmbed = getAbsolutePathFeatureAsset(parametro, feature, filename);

            if (htmlEmbed != null && htmlEmbed.isFile()) {
                paginaHtmlAnexo.add(htmlEmbed);
                txt = txt.replace(mHtmlHref.group(), "href=\"#/html/" + filename + "\"");
            }
        }

        return txt;
    }

    private String format(String txt){
        return format(txt, true);
    }
}
