package br.com.pirilampo.util;

import br.com.pirilampo.main.Main;
import gherkin.AstBuilder;
import gherkin.Parser;
import gherkin.TokenMatcher;
import gherkin.ast.GherkinDocument;
import org.apache.commons.io.input.BOMInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigInteger;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Compilador {
    private static final Logger logger = LoggerFactory.getLogger(Compilador.class);
    static String LOG;
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
            logger.info("OK: " + pathFeature);
        }catch (Exception e){
            Compilador.LOG += "ERRRROU: " + pathFeature + "\n";
            logger.warn("ERRRROU: " + pathFeature);
            throw e;
        }

        return html;
    }

    public void compilarPasta(String dir, String dirMaster, String projectName, String projecVersion, String outputDir) throws Exception {
        ParseMenu parseMenu = new ParseMenu();
        String htmlTemplate = "";
        String htmlJavascript = "";
        String htmlCss = "";

        // -------- MASTER
        List<File> arquivosMaster = null;
        if(dirMaster != null) {
            // Abre pasta root
            File curDirMaster = new File(dirMaster);

            // Popula com arquivos feature
            arquivos = new ArrayList<>();
            listarPasta(curDirMaster);
            arquivosMaster = arquivos;
        }

        // -------- NORMAL
        // Abre pasta root
        File curDir = new File(dir);

        // Popula com arquivos feature
        arquivos = new ArrayList<>();
        listarPasta(curDir);

        if(arquivos.size() > 0){
            for(File f : arquivos){
                if(f.getName().contains(".feature")){
                    // monta nome menu
                    String htmlFeatureId = f.getAbsolutePath().replace(curDir.getAbsolutePath(), "");
                    htmlFeatureId = htmlFeatureId.replace(f.getName(), "");
                    htmlFeatureId = htmlFeatureId.replace(File.separator, " ");
                    htmlFeatureId = htmlFeatureId.trim();
                    htmlFeatureId = htmlFeatureId + "_" + f.getName().replace(".feature", ".html");

                    // Processa Master
                    if(dirMaster != null) {
                        boolean diferente = true;
                        File fmd = null;

                        if(arquivosMaster.size() > 0) {
                            for (File fm : arquivosMaster) {
                                if (absoluteNameFeature(dirMaster, fm.getAbsolutePath()).equals(absoluteNameFeature(dir, f.getAbsolutePath()))) {
                                    if(md5(loadFeature(fm.getAbsolutePath())).equals(md5(loadFeature(f.getAbsolutePath())))){
                                        diferente = false;
                                    }else{
                                        fmd = fm;
                                    }
                                    break;
                                }
                            }
                        }

                        // pula para o proximo
                        if(!diferente){
                            continue;
                        }

                        if(fmd != null) {
                            String featureHtml = getFeatureHtml(fmd.getAbsolutePath());
                            htmlTemplate += String.format(
                                    HTML_TEMPLATE,
                                    "master_" + htmlFeatureId,
                                    featureHtml
                            );
                            htmlTemplate += String.format(
                                    HTML_TEMPLATE,
                                    "master_" + htmlFeatureId.replace(".html", ".feature"),
                                    loadFeature(fmd.getAbsolutePath())
                            );
                        }
                    }

                    // Adiciona item de menu se deu tudo certo com a master
                    parseMenu.addMenuItem(f.getAbsolutePath().replace(curDir.getAbsolutePath(), ""));

                    // Gera a feture
                    String featureHtml = getFeatureHtml(f.getAbsolutePath());

                    htmlTemplate += String.format(HTML_TEMPLATE, htmlFeatureId, featureHtml);

                    // Salva as feature para diff
                    if(dirMaster != null){
                        htmlTemplate += String.format(
                                HTML_TEMPLATE,
                                htmlFeatureId.replace(".html", ".feature"),
                                loadFeature(f.getAbsolutePath())
                        );
                    }
                }
            }

            //------------------ BUILD -----------------
            String html = loadResource("htmlTemplate/template_feature_pasta.html");

            // adiciona libs
            htmlCss += String.format(HTML_CSS, loadResource("htmlTemplate/css/bootstrap.min.css"));
            htmlCss += String.format(HTML_CSS, loadResource("htmlTemplate/css/simple-sidebar.css"));
            htmlCss += String.format(HTML_CSS, loadResource("htmlTemplate/css/lightbox.min.css"));

            htmlJavascript += String.format(HTML_JAVASCRIPT, loadResource("htmlTemplate/lib/jquery.js"));
            htmlJavascript += String.format(HTML_JAVASCRIPT, loadResource("htmlTemplate/lib/bootstrap.min.js"));
            htmlJavascript += String.format(HTML_JAVASCRIPT, loadResource("htmlTemplate/lib/handlebars.min-latest.js"));
            htmlJavascript += String.format(HTML_JAVASCRIPT, loadResource("htmlTemplate/lib/typeahead.bundle.min.js"));
            htmlJavascript += String.format(HTML_JAVASCRIPT, loadResource("htmlTemplate/lib/diff_match_patch.js"));
            htmlJavascript += String.format(HTML_JAVASCRIPT, loadResource("htmlTemplate/lib/jquery.pretty-text-diff.min.js"));
            htmlJavascript += String.format(HTML_JAVASCRIPT, loadResource("htmlTemplate/lib/angular.min.js"));
            htmlJavascript += String.format(HTML_JAVASCRIPT, loadResource("htmlTemplate/js/app.js"));
            htmlJavascript += String.format(HTML_JAVASCRIPT, loadResource("htmlTemplate/js/featureController.js"));
            htmlJavascript += String.format(HTML_JAVASCRIPT, loadResource("htmlTemplate/lib/angular-route.min.js"));
            htmlJavascript += String.format(HTML_JAVASCRIPT, loadResource("htmlTemplate/lib/angular-resource.min.js"));
            htmlJavascript += String.format(HTML_JAVASCRIPT, loadResource("htmlTemplate/lib/angular-ui-router.min.js"));
            htmlJavascript += String.format(HTML_JAVASCRIPT, loadResource("htmlTemplate/js/lightbox.min.js"));

            html = html.replace("#PROJECT_NAME#", projectName);
            html = html.replace("#PROJECT_VERSION#", projecVersion);
            html = html.replace("#HTML_MENU#", parseMenu.getHtml());
            html = html.replace("#HTML_CSS#", htmlCss);
            html = html.replace("#HTML_JAVASCRIPT#", htmlJavascript);
            html = html.replace("#HTML_TEMPLATE#", htmlTemplate);

            // Grava
            // Cria Diretório se não existir */html/feature/
            String outDir = (outputDir != null ? outputDir : curDir.getParent() + "/html/");
            File outDirF = new File(outDir);

            if(!outDirF.exists()){
                outDirF.mkdir();
            }

            Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outDir + "index.html"), "UTF-8"));
            out.write(html);
            out.close();
        }
    }

    public void compilarFeature(String featurePath, String projectName, String projecVersion, String outputDir) throws IOException {
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
        String outDir = (outputDir != null ? outputDir : feature.getParent());
        outDir += String.format("/%s.html", feature.getName().replace(".feature", ""));

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
        BufferedReader br;

        URL url = Thread.currentThread().getContextClassLoader().getResource(Main.SYS_PATH + src);

        if(url != null) {
            try {
                br = new BufferedReader(new FileReader(url.getFile()), 200 * 1024);
            } catch (Exception ea) {
                br = new BufferedReader(new InputStreamReader(url.openStream()), 200 * 1024);
            }

            while ((linha = br.readLine()) != null) {
                buffer += linha + "\n";
            }

            br.close();
        } else {
            logger.warn("Falha ao carregar Resource");
        }

        return buffer;
    }

    private String loadFeature(String pathFeature){
        String buffer = "";
        String linha;
        BufferedReader br;

        try {
            BOMInputStream bis = new BOMInputStream(new FileInputStream(pathFeature));

            br = new BufferedReader(new InputStreamReader(bis, "UTF-8"));

            while ((linha = br.readLine()) != null) {
                buffer += linha + "\n";
            }
        }catch (Exception e){
            logger.warn(e.getMessage());
        }

        return buffer;
    }

    private String md5(String str){
        String md5 = null;

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md5 = String.format("%032x", new BigInteger(1, md.digest(str.getBytes())));
        } catch (NoSuchAlgorithmException e) {
            logger.warn(Compilador.class.getName(), e);
        }

        return md5;
    }

    private String absoluteNameFeature(String path, String absolutePath){
        absolutePath = absolutePath.replace(path, "");
        absolutePath = absolutePath.replaceFirst("^\\/", "");
        absolutePath = absolutePath.replaceFirst("^\\\\", "");

        return absolutePath;
    }
}
