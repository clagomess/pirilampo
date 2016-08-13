package br.com.pirilampo.util;

import gherkin.ast.*;
import org.apache.commons.lang.StringEscapeUtils;
import org.commonmark.html.HtmlRenderer;

public class ParseDocument {
    private GherkinDocument gd;

    public ParseDocument(GherkinDocument gd){
        this.gd = gd;
    }

    private final String HTML_TITULO = "<h1>%s</h1>\n";
    private final String HTML_PARAGRAFO = "<p>%s</p>\n";
    private final String HTML_STEP = "<p><span class=\"keyword\">%s</span> %s</p>\n";
    private final String HTML_CODE = "<pre>%s</pre>\n";

    private final String HTML_CHILDREN = "<div class=\"panel panel-default\">\n" +
            "<div class=\"panel-heading\" style=\"cursor: pointer;\" data-toggle=\"collapse\" data-target=\"#scenario-%s\"><strong>%s</strong></div>\n%s\n</div>\n";
    private final String HTML_CHILDREN_BODY = "<div id=\"scenario-%s\" class=\"panel-body collapse in\">%s</div>\n";
    private final String HTML_CHILDREN_TABLE = "<div class=\"table-responsive\">\n" +
            "<table class=\"table table-condensed table-bordered table-hover table-striped\">\n" +
            "<thead>\n%s\n</thead>\n" +
            "<tbody>\n%s\n</tbody>\n" +
            "</table>\n</div>\n";
    private final String HTML_CHILDREN_TABLE_TR = "<tr>%s</tr>\n";
    private final String HTML_CHILDREN_TABLE_TH = "<th>%s</th>\n";
    private final String HTML_CHILDREN_TABLE_TD = "<td>%s</td>\n";

    public String getHtml(){
        String html = "";

        if(gd != null){
            html += String.format(HTML_TITULO, format(gd.getFeature().getName(), false));
            html += String.format(HTML_PARAGRAFO, format(gd.getFeature().getDescription()));

            int scenarioIdx = 0;
            for (ScenarioDefinition sd : gd.getFeature().getChildren()){
                String body  = "";

                if(sd.getDescription() != null){
                    body += String.format(HTML_PARAGRAFO, format(sd.getDescription()));
                }

                for (Step step : sd.getSteps()){
                    body += String.format(HTML_STEP, step.getKeyword(), format(step.getText()));

                    if(step.getArgument() != null){
                        if(step.getArgument() instanceof DataTable) {
                            String htmlTrH = "";
                            String htmlTrD = "";
                            int i = 0;

                            for (TableRow tr : ((DataTable) step.getArgument()).getRows()) {
                                String htmlTc = "";
                                for (TableCell tc : tr.getCells()) {
                                    if (i == 0) {
                                        htmlTc += String.format(HTML_CHILDREN_TABLE_TH, format(tc.getValue(), false));
                                    } else {
                                        htmlTc += String.format(HTML_CHILDREN_TABLE_TD, format(tc.getValue()));
                                    }
                                }

                                if (i == 0) {
                                    htmlTrH += String.format(HTML_CHILDREN_TABLE_TR, htmlTc);
                                } else {
                                    htmlTrD += String.format(HTML_CHILDREN_TABLE_TR, htmlTc);
                                }

                                i++;
                            }

                            body += String.format(HTML_CHILDREN_TABLE, htmlTrH, htmlTrD);
                        }

                        if(step.getArgument() instanceof DocString) {
                            body += String.format(HTML_CODE, format(((DocString) step.getArgument()).getContent(), false));
                        }
                    }
                }

                body = String.format(HTML_CHILDREN_BODY, scenarioIdx, body);
                html += String.format(HTML_CHILDREN, scenarioIdx, StringEscapeUtils.escapeHtml(sd.getName().equals("") ? sd.getKeyword() : sd.getName()), body);

                scenarioIdx++;
            }
        }

        return html;
    }

    /**
     * @param txt raw texto
     * @param md ativar markedow?
     * @return html
     */
    private String format(String txt, boolean md){
        txt = txt.trim();
        txt = StringEscapeUtils.escapeHtml(txt);

        if(md) {
            try {
                org.commonmark.parser.Parser parser = org.commonmark.parser.Parser.builder().build();
                org.commonmark.node.Node document = parser.parse(txt);
                HtmlRenderer renderer = HtmlRenderer.builder().build();
                txt = renderer.render(document);
                txt = txt.replace("<p>", "");
                txt = txt.replace("</p>", "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return txt;
    }

    private String format(String txt){
        return format(txt, true);
    }
}
