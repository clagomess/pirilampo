package br.com.pirilampo.constant;

final public class HtmlTemplate {
    public static final String HTML_TEMPLATE = "<script type=\"text/ng-template\" id=\"%s\">%s</script>\n";
    public static final String HTML_JAVASCRIPT = "<script type=\"text/javascript\">%s</script>\n";
    public static final String HTML_CSS = "<style>%s</style>\n";
    public static final String HTML_FEATURE_PDF = "<h1 class=\"page-header\">%s <small>%s <em>%s</em></small></h1>\n" +
            "%s\n<span style=\"page-break-after: always\"></span>";


    // FEATURE
    public static final String HTML_TITULO = "<h2>%s</h2>\n";
    public static final String HTML_PARAGRAFO = "<p>%s</p>\n";
    public static final String HTML_STEP = "<p><span class=\"keyword\">%s</span> %s</p>\n";
    public static final String HTML_CODE = "<pre>%s</pre>\n";

    public static final String HTML_CHILDREN = "<div class=\"panel panel-default\">\n" +
            "<div class=\"panel-heading\" style=\"cursor: pointer;\" data-toggle=\"collapse\" data-target=\"#scenario-%s\"><h3>%s</h3></div>\n%s\n</div>\n";
    public static final String HTML_CHILDREN_BODY = "<div id=\"scenario-%s\" class=\"panel-body collapse in\">%s</div>\n";
    public static final String HTML_CHILDREN_TABLE = "<div class=\"table-responsive\">\n" +
            "<table class=\"table table-condensed table-bordered table-hover table-striped\">\n" +
            "<thead>\n%s\n</thead>\n" +
            "<tbody>\n%s\n</tbody>\n" +
            "</table>\n</div>\n";
    public static final String HTML_CHILDREN_TABLE_TR = "<tr>%s</tr>\n";
    public static final String HTML_CHILDREN_TABLE_TH = "<th>%s</th>\n";
    public static final String HTML_CHILDREN_TABLE_TD = "<td>%s</td>\n";
}
