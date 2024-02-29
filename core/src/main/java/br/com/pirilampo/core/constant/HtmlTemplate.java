package br.com.pirilampo.core.constant;

final public class HtmlTemplate {

    public static final String HTML_JAVASCRIPT = "<script type=\"text/javascript\">%s</script>\n";
    public static final String HTML_CSS = "<style>%s</style>\n";
    public static final String HTML_FEATURE_PDF = "<h1 class=\"page-header\">%s <small>%s <em>%s</em></small></h1>\n" +
            "%s\n<span style=\"page-break-after: always\"></span>";

    // MENU
    public static final String HTML_MENU_FILHO = "\t\t<li><a href=\"#/feature/%s\">%s%s</a></li>\n";
    public static final String HTML_MENU_PAI = "<li>\n" +
            "\t<a href=\"javascript:;\" data-toggle=\"collapse\" data-target=\"#menu-%s\">%s</a>\n" +
            "\t<ul id=\"menu-%s\" class=\"collapse\">\n%s\t</ul>\n" +
            "</li>\n";
    public static final String HTML_MENU_ICON_DIFF_NOVO = "<span class=\"icon-diff-novo\"></span> ";
    public static final String HTML_MENU_ICON_DIFF_DIFERENTE = "<span class=\"icon-diff-diferente\"></span> ";
}
