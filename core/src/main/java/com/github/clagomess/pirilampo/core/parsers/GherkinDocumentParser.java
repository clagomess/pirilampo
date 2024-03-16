package com.github.clagomess.pirilampo.core.parsers;

import com.github.clagomess.pirilampo.core.compilers.Compiler;
import com.github.clagomess.pirilampo.core.dto.ParametersDto;
import com.github.clagomess.pirilampo.core.enums.HtmlPanelToggleEnum;
import com.github.clagomess.pirilampo.core.exception.FeatureException;
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

@Slf4j
public class GherkinDocumentParser extends Compiler {
    private final ParametersDto parameters;
    private final File feature;

    @Getter
    private final TextParser textParser;

    @Getter
    private String featureTitulo = null;

    public GherkinDocumentParser(ParametersDto parameters, File feature){
        this(null, parameters, feature);
    }

    public GherkinDocumentParser(
            IndexParser indexParser,
            ParametersDto parameters,
            File feature
    ){
        this.parameters = parameters;
        this.feature = feature;
        this.textParser = new TextParser(indexParser, parameters, feature);
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

    private void parseHTMLTableHeadValue(PrintWriter out, String value){
        out.print("<th>");
        textParser.format(out, value, false);
        out.println("</th>");
    }

    private void parseHTMLTableBodyValue(PrintWriter out, String value){
        out.print("<td>");
        textParser.format(out, value, true);
        out.println("</td>");
    }

    private void parseStepDataTable(DataTable dataTable, PrintWriter out){
        if(dataTable.getRows().isEmpty()) return;

        out.println("<div class=\"table-responsive\">");
        out.println("<table class=\"table table-condensed table-bordered table-hover table-striped\">");
        out.print("<thead><tr>");

        for (TableCell tc : dataTable.getRows().get(0).getCells()) {
            parseHTMLTableHeadValue(out, tc.getValue());
        }

        out.print("</tr></thead>");

        out.print("<tbody>");
        int i = 0;
        for (TableRow tr : dataTable.getRows()) {
            if(i++ == 0) continue;

            out.print("<tr>");
            for (TableCell tc : tr.getCells()) {
                parseHTMLTableBodyValue(out, tc.getValue());
            }
            out.print("</tr>");
        }

        out.print("</tbody></table></div>");
    }

    private void parseStepDocString(DocString docString, PrintWriter out){
        out.print("<pre>");
        out.print(docString.getContent());
        out.print("</pre>");
    }

    private void parseHTMLStep(PrintWriter out, String step, String value){
        out.print("<p><span class=\"keyword\">");
        out.print(step);
        out.print("</span> ");
        textParser.format(out, value, true);
        out.println("</p>");
    }

    private void parseScenarioOutlineExamples(Examples examples, PrintWriter out){
        parseHTMLStep(out, examples.getKeyword(), ":");
        out.println("<div class=\"table-responsive\">");
        out.println("<table class=\"table table-condensed table-bordered table-hover table-striped\">");

        if(examples.getTableHeader() != null) {
            out.print("<thead><tr>");

            for (TableCell tc : examples.getTableHeader().getCells()) {
                parseHTMLTableHeadValue(out, tc.getValue());
            }

            out.print("</tr></thead>");
        }

        if(examples.getTableBody() != null) {
            out.print("<tbody>");

            for (TableRow tr : examples.getTableBody()) {
                out.print("<tr>");

                for (TableCell tc : tr.getCells()) {
                    parseHTMLTableBodyValue(out, tc.getValue());
                }
                out.print("</tr>");
            }

            out.print("</tbody>");
        }

        out.print("</table></div>");
    }

    private void parseHTMLScenarioPanelHead(PrintWriter out, int idx, String title) throws IOException {
        out.println("<div class=\"panel panel-default\">");
        out.print("<div class=\"panel-heading\" style=\"cursor: pointer;\" data-toggle=\"collapse\" data-target=\"#scenario-");
        out.print(idx);
        out.print("\"><h3>");
        StringEscapeUtils.escapeHtml(out, title);
        out.print("</h3></div>");
    }

    private void parseHTMLScenarioPanelBody(PrintWriter out, int idx){
        out.print("<div id=\"scenario-");
        out.print(idx);
        out.print("\" class=\"panel-body collapse ");
        if (parameters.getHtmlPanelToggle() == HtmlPanelToggleEnum.CLOSED) out.print("in");
        out.print("\">");
    }

    private void build(GherkinDocument gd, PrintWriter out) throws IOException {
        out.print("<h2>");
        textParser.format(out, gd.getFeature().getName(), false);
        out.println("</h2>");

        if(StringUtils.isNotBlank(gd.getFeature().getDescription())) {
            out.print("<p>");
            textParser.format(out, gd.getFeature().getDescription(), true);
            out.println("</p>");
        }

        int scenarioIdx = 0;
        for (ScenarioDefinition sd : gd.getFeature().getChildren()){
            parseHTMLScenarioPanelHead(
                    out,
                    scenarioIdx,
                    StringUtils.isBlank(sd.getName()) ? sd.getKeyword() : sd.getName()
            );

            parseHTMLScenarioPanelBody(out, scenarioIdx);

            if(StringUtils.isNotBlank(sd.getDescription())){
                out.print("<p>");
                textParser.format(out, sd.getDescription(), true);
                out.println("</p>");
            }

            for (Step step : sd.getSteps()){
                parseHTMLStep(out, step.getKeyword(), step.getText());

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

            out.print("</div></div>");

            scenarioIdx++;
        }
    }
}
