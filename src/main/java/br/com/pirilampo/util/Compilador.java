package br.com.pirilampo.util;

import br.com.pirilampo.main.Main;
import gherkin.AstBuilder;
import gherkin.Parser;
import gherkin.TokenMatcher;
import gherkin.ast.GherkinDocument;
import org.apache.commons.io.input.BOMInputStream;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Compilador {
    public static String LOG;
    private List<File> arquivos = new ArrayList<>();
    private final String HTML_TEMPLATE = "<script type=\"text/ng-template\" id=\"%s\">%s</script>\n";
    private final String HTML_JAVASCRIPT = "<script type=\"text/javascript\">%s</script>\n";
    private final String HTML_CSS = "<style>%s</style>\n";
    private final String HTML_FEATURE_PDF = "<h1 class=\"page-header\">%s <small>%s <em>%s</em></small></h1>\n" +
            "%s\n<span style=\"page-break-after: always\"></span>";

    public Compilador(){
        Compilador.LOG = "";
    }

    private void listarPasta(File curDir) throws Exception {
        File[] filesList = curDir.listFiles();

        if(filesList != null) {
            for (File f : filesList) {
                if (f.isDirectory()) {
                    listarPasta(f);
                }

                if (f.isFile()) {
                    if(f.getName().contains(".feature")) {
                        arquivos.add(f);
                    }
                }
            }
        }else{
            throw new Exception("Pasta não localizada!");
        }
    }

    private String getFeatureHtml(String pathFeature) throws IOException {
        Parser<GherkinDocument> parser = new Parser<>(new AstBuilder());
        TokenMatcher matcher = new TokenMatcher();
        String html = null;

        try {
            // BOMInputStream para caso o arquivo possuir BOM
            BOMInputStream bis = new BOMInputStream(new FileInputStream(pathFeature));

            Reader in = new InputStreamReader(bis, "UTF-8");

            GherkinDocument gherkinDocument = parser.parse(in, matcher);

            if (gherkinDocument != null) {
                ParseDocument pd = new ParseDocument(gherkinDocument);
                html = pd.getHtml();
            }

            Compilador.LOG += "OK: " + pathFeature + "\n";
        }catch (Exception e){
            Compilador.LOG += "ERRRROU: " + pathFeature + "\n";
            throw e;
        }

        return html;
    }

    private void compilarPastaItem(File pathPasta, File pathFeature) throws IOException {
        String html = getFeatureHtml(pathFeature.getAbsolutePath());

        File hmtlDir;
        String outDir = pathPasta.getParent();

        // Cria Diretório se não existir */html/feature/
        outDir += "/html/";
        hmtlDir = new File(outDir);

        if(!hmtlDir.exists()){
            hmtlDir.mkdir();
        }

        outDir += "feature/";
        hmtlDir = new File(outDir);

        if(!hmtlDir.exists()) {
            hmtlDir.mkdir();
        }

        // Grava HTMLs
        outDir += pathFeature.getName().replace(".feature", ".html");

        File feature = new File(outDir);
        FileWriter fwrite = new FileWriter(feature);
        fwrite.write(html);
        fwrite.flush();
        fwrite.close();
    }

    public void compilarPasta(String dir, String projectName, String projecVersion) throws Exception {
        Map<String, List<String>> menu = new TreeMap<>();
        String htmlTemplate = "";
        String htmlJavascript = "";
        String htmlCss = "";

        // Abre pasta root
        File curDir = new File(dir);

        // Popula com arquivos feature
        arquivos = new ArrayList<>();
        listarPasta(curDir);

        if(arquivos.size() > 0){
            for(File f : arquivos){
                if(f.getName().contains(".feature")){
                    // monta menu
                    String menuPai = f.getAbsolutePath().replace(curDir.getAbsolutePath(), "");
                    menuPai = menuPai.replace(f.getName(), "");
                    menuPai = menuPai.replace("\\", "");

                    if(menuPai.trim().equals("")){
                        menuPai = "Features";
                    }

                    if(!menu.containsKey(menuPai)){
                        menu.put(menuPai, new ArrayList<>());
                    }

                    menu.get(menuPai).add(f.getName().replace(".feature", ""));

                    // Gera a feture
                    String featureHtml = getFeatureHtml(f.getAbsolutePath());

                    htmlTemplate += String.format(HTML_TEMPLATE, f.getName().replace(".feature", ".html"), featureHtml);
                }
            }

            //------------------ BUILD -----------------
            String html = loadResource("htmlTemplate/template_feature_pasta.html");


            // monta menu
            String htmlMenu = "";
            final String HTML_MENU_PAI = "<li><a href=\"javascript:;\" data-toggle=\"collapse\" data-target=\"#menu-%s\">" +
            "%s</a><ul id=\"menu-%s\" class=\"collapse\">%s</ul></li>";

            final String HTML_MENU_FILHO = "<li><a href=\"#/feature/%s\">%s</a></li>";

            int menuIdx = 0;
            for (Map.Entry<String, List<String>> entry : menu.entrySet()) {
                String filhos = "";

                for(String item : entry.getValue()){
                    filhos += String.format(HTML_MENU_FILHO, item, item);
                }

                htmlMenu += String.format(HTML_MENU_PAI, menuIdx, entry.getKey(), menuIdx, filhos);

                menuIdx++;
            }


            // adiciona libs
            htmlCss += String.format(HTML_CSS, loadResource("htmlTemplate/css/bootstrap.min.css"));
            htmlCss += String.format(HTML_CSS, loadResource("htmlTemplate/css/simple-sidebar.css"));
            htmlCss += String.format(HTML_CSS, loadResource("htmlTemplate/css/lightbox.min.css"));

            htmlJavascript += String.format(HTML_JAVASCRIPT, loadResource("htmlTemplate/lib/jquery.js"));
            htmlJavascript += String.format(HTML_JAVASCRIPT, loadResource("htmlTemplate/lib/bootstrap.min.js"));
            htmlJavascript += String.format(HTML_JAVASCRIPT, loadResource("htmlTemplate/lib/handlebars.min-latest.js"));
            htmlJavascript += String.format(HTML_JAVASCRIPT, loadResource("htmlTemplate/lib/typeahead.bundle.min.js"));
            htmlJavascript += String.format(HTML_JAVASCRIPT, loadResource("htmlTemplate/lib/angular.min.js"));
            htmlJavascript += String.format(HTML_JAVASCRIPT, loadResource("htmlTemplate/js/app.js"));
            htmlJavascript += String.format(HTML_JAVASCRIPT, loadResource("htmlTemplate/js/featureController.js"));
            htmlJavascript += String.format(HTML_JAVASCRIPT, loadResource("htmlTemplate/lib/angular-route.min.js"));
            htmlJavascript += String.format(HTML_JAVASCRIPT, loadResource("htmlTemplate/lib/angular-resource.min.js"));
            htmlJavascript += String.format(HTML_JAVASCRIPT, loadResource("htmlTemplate/lib/angular-ui-router.min.js"));
            htmlJavascript += String.format(HTML_JAVASCRIPT, loadResource("htmlTemplate/js/lightbox.min.js"));

            html = html.replace("#PROJECT_NAME#", projectName);
            html = html.replace("#PROJECT_VERSION#", projecVersion);
            html = html.replace("#HTML_MENU#", htmlMenu);
            html = html.replace("#HTML_CSS#", htmlCss);
            html = html.replace("#HTML_JAVASCRIPT#", htmlJavascript);
            html = html.replace("#HTML_TEMPLATE#", htmlTemplate);

            // Grava
            // Cria Diretório se não existir */html/feature/
            String outDir = curDir.getParent() + "/html/";
            File outDirF = new File(outDir);

            if(!outDirF.exists()){
                outDirF.mkdir();
            }

            Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outDir + "index.html"), "UTF-8"));
            out.write(html);
            out.close();
        }
    }

    public void compilarFeature(String featurePath, String projectName, String projecVersion) throws IOException {
        // Abre feature
        File feature = new File(featurePath);

        // compila
        String featureHtml = getFeatureHtml(feature.getAbsolutePath());

        //------------------ BUILD -----------------
        String html = loadResource("htmlTemplate/template_feature.html");

        String htmlCss = String.format(HTML_CSS, loadResource("htmlTemplate/css/bootstrap.min.css"));

        html = html.replace("#PROJECT_NAME#", projectName);
        html = html.replace("#PROJECT_VERSION#", projecVersion);
        html = html.replace("#PROJECT_FEATURE#", feature.getName().replace(".feature", ""));
        html = html.replace("#HTML_CSS#", htmlCss);
        html = html.replace("#HTML_TEMPLATE#", featureHtml);

        // Grava
        String outDir = feature.getParent() + String.format("/%s.html", feature.getName().replace(".feature", ""));

        Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outDir), "UTF-8"));
        out.write(html);
        out.close();
    }

    public void compilarFeaturePdf(String featurePath, String projectName, String projecVersion, String layout) throws Exception {
        // Abre feature
        File feature = new File(featurePath);

        //------------------ BUILD -----------------
        String htmlTemplate = loadResource("htmlTemplate/template_feature_pdf.html");
        String css = loadResource("htmlTemplate/css/bootstrap.min.css");

        String html = getFeatureHtml(feature.getAbsolutePath());
        html = String.format(
                HTML_FEATURE_PDF,
                projectName,
                feature.getName().replace(".feature", ""),
                projecVersion,
                html
        );
        html = htmlTemplate.replace("#HTML_TEMPLATE#", html);

        ParsePdf pp = new ParsePdf();

        String path = feature.getParent() + String.format("/%s.pdf", feature.getName().replace(".feature", ""));

        pp.buildHtml(path, html, css, layout);
    }

    public void compilarPastaPdf(String dir, String projectName, String projecVersion, String layout) throws Exception {
        String html = "";

        // Abre pasta root
        File curDir = new File(dir);

        // Popula com arquivos feature
        arquivos = new ArrayList<>();
        listarPasta(curDir);

        if(arquivos.size() > 0) {
            for (File f : arquivos) {
                if (f.getName().contains(".feature")) {
                    String rawHtml = getFeatureHtml(f.getAbsolutePath());

                    html += String.format(
                            HTML_FEATURE_PDF,
                            projectName,
                            f.getName().replace(".feature", ""),
                            projecVersion,
                            rawHtml
                    );
                }
            }

            //------------------ BUILD -----------------
            String htmlTemplate = loadResource("htmlTemplate/template_feature_pdf.html");
            String css = loadResource("htmlTemplate/css/bootstrap.min.css");

            html = htmlTemplate.replace("#HTML_TEMPLATE#", html);

            ParsePdf pp = new ParsePdf();

            String outDir = curDir.getParent() + "/html/";
            File outDirF = new File(outDir);

            if(!outDirF.exists()){
                outDirF.mkdir();
            }

            pp.buildHtml(outDir + "index.pdf", html, css, layout);
        }
    }

    private String loadResource(String src) throws IOException {
        String buffer = "";
        String linha;

        URL url = Thread.currentThread().getContextClassLoader().getResource(Main.SYS_PATH + src);

        BufferedReader br;

        try {
            br = new BufferedReader(new FileReader(url.getFile()), 200 * 1024);
        }catch (Exception ea){
            br = new BufferedReader(new InputStreamReader(url.openStream()), 200 * 1024);
        }

        while ((linha = br.readLine()) != null) {
            buffer += linha + "\n";
        }

        br.close();

        return buffer;
    }
}
