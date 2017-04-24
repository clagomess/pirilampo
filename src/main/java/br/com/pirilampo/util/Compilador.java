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
    static StringBuilder LOG;
    private List<File> arquivos = new ArrayList<>();
    private final String HTML_TEMPLATE = "<script type=\"text/ng-template\" id=\"%s\">%s</script>\n";
    private final String HTML_JAVASCRIPT = "<script type=\"text/javascript\">%s</script>\n";
    private final String HTML_CSS = "<style>%s</style>\n";
    private final String HTML_FEATURE_PDF = "<h1 class=\"page-header\">%s <small>%s <em>%s</em></small></h1>\n" +
            "%s\n<span style=\"page-break-after: always\"></span>";

    public static String COR_MENU = "#14171A";
    public static String NOME_MENU_RAIZ = "Features";
    public static File LOGO_PATH = null;
    static List<File> PAGINA_HTML_ANEXO;

    public Compilador(){
        Compilador.LOG = new StringBuilder();
        Compilador.PAGINA_HTML_ANEXO = new ArrayList<>();
    }

    public static void setConfig(String corMenu, String nomeMenuRaiz, File logoPath){
        COR_MENU = corMenu;
        NOME_MENU_RAIZ = nomeMenuRaiz;
        LOGO_PATH = logoPath;
    }

    //====== Metodos

    private void listarPasta(File curDir) throws Exception {
        File[] filesList = curDir.listFiles();

        if(filesList != null) {
            for (File f : filesList) {
                if (f.isDirectory()) {
                    listarPasta(f);
                }

                if (f.isFile() && f.getName().contains(".feature")) {
                    arquivos.add(f);
                }
            }
        }else{
            throw new Exception("Pasta não localizada!");
        }
    }

    private String getFeatureHtml(String pathFeature, List<String> pathList) throws IOException {
        Parser<GherkinDocument> parser = new Parser<>(new AstBuilder());
        TokenMatcher matcher = new TokenMatcher();
        String html = null;

        FileInputStream fis = null;
        BOMInputStream bis = null;

        try {
            // BOMInputStream para caso o arquivo possuir BOM
            fis = new FileInputStream(pathFeature);
            bis = new BOMInputStream(fis);

            Reader in = new InputStreamReader(bis, "UTF-8");

            GherkinDocument gherkinDocument = parser.parse(in, matcher);

            if (gherkinDocument != null) {
                ParseDocument pd = new ParseDocument(gherkinDocument, pathList);
                html = pd.getHtml();
            }

            Compilador.LOG.append("OK: ").append(pathFeature).append("\n");
            logger.info("OK: " + pathFeature);
        } catch (Exception e){
            Compilador.LOG.append("ERRRROU: ").append(pathFeature).append("\n");
            logger.warn("ERRRROU: " + pathFeature);
            throw e;
        } finally {
            if(fis != null){
                fis.close();
            }

            if(bis != null){
                bis.close();
            }
        }

        return html;
    }

    public void compilarPasta(String dir, String dirMaster, String projectName, String projecVersion, String outputDir) throws Exception {
        ParseMenu parseMenu = new ParseMenu();
        StringBuilder htmlTemplate = new StringBuilder();
        StringBuilder htmlJavascript = new StringBuilder();
        StringBuilder htmlCss = new StringBuilder();

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
                    String htmlFeatureRoot = f.getAbsolutePath().replace(curDir.getAbsolutePath(), "");
                    htmlFeatureRoot = htmlFeatureRoot.replace(f.getName(), "");
                    htmlFeatureRoot = htmlFeatureRoot.replace(File.separator, " ");
                    htmlFeatureRoot = htmlFeatureRoot.trim();

                    if(htmlFeatureRoot.equals("")){
                        htmlFeatureRoot = Compilador.NOME_MENU_RAIZ;
                    }

                    String htmlFeatureId = htmlFeatureRoot + "_" + f.getName().replace(".feature", ".html");

                    // Processa Master
                    if(dirMaster != null) {
                        boolean diferente = true;
                        File fmd = null;

                        if(arquivosMaster.size() > 0) {
                            for (File fm : arquivosMaster) {
                                String absoluteNFM = absoluteNameFeature(dirMaster, fm.getAbsolutePath());
                                String absoluteNFB = absoluteNameFeature(dir, f.getAbsolutePath());

                                if (absoluteNFM.equals(absoluteNFB)) {
                                    if(md5(loadFeature(fm.getAbsolutePath())).equals(md5(loadFeature(f.getAbsolutePath())))){
                                        diferente = false;
                                    }else{
                                        fmd = fm;
                                        // Debug
                                        logger.info(
                                                "Diff Master/Branch: {} - {} - {} - {}",
                                                md5(absoluteNFM),
                                                md5(absoluteNFB),
                                                absoluteNFM,
                                                absoluteNFB
                                        );
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
                            // PathListMaster
                            List<String> pathListMaster = new ArrayList<>();
                            pathListMaster.add(dirMaster);
                            pathListMaster.add(fmd.getAbsolutePath().replace(fmd.getName(), ""));

                            String featureHtml = getFeatureHtml(fmd.getAbsolutePath(), pathListMaster);

                            htmlTemplate.append(String.format(
                                    HTML_TEMPLATE,
                                    "master_" + htmlFeatureId,
                                    featureHtml
                            ));
                            htmlTemplate.append(String.format(
                                    HTML_TEMPLATE,
                                    "master_" + htmlFeatureId.replace(".html", ".feature"),
                                    loadFeature(fmd.getAbsolutePath())
                            ));
                        }
                    }

                    // Adiciona item de menu se deu tudo certo com a master
                    if(Compilador.NOME_MENU_RAIZ.equals(htmlFeatureRoot)){
                        parseMenu.addMenuItem(
                                htmlFeatureRoot +
                                File.separator +
                                f.getAbsolutePath().replace(curDir.getAbsolutePath(), "")
                        );
                    }else{
                        parseMenu.addMenuItem(f.getAbsolutePath().replace(curDir.getAbsolutePath(), ""));
                    }

                    // Gera a feture
                    List<String> pathList = new ArrayList<>();
                    pathList.add(dir);
                    pathList.add(f.getAbsolutePath().replace(f.getName(), ""));

                    String featureHtml = getFeatureHtml(f.getAbsolutePath(), pathList);

                    htmlTemplate.append(String.format(HTML_TEMPLATE, htmlFeatureId, featureHtml));

                    // Salva as feature para diff
                    if(dirMaster != null){
                        htmlTemplate.append(String.format(
                                HTML_TEMPLATE,
                                htmlFeatureId.replace(".html", ".feature"),
                                loadFeature(f.getAbsolutePath())
                        ));
                    }
                }
            }

            // adiciona html embed
            for (File htmlEmbed : Compilador.PAGINA_HTML_ANEXO){
                String loadedHtmlEmbed = loadFeature(htmlEmbed.getAbsolutePath());
                htmlTemplate.append(String.format(
                        "<template type=\"text/ng-template\" id=\"%s\">%s</template>%n",
                        htmlEmbed.getName(),
                        loadedHtmlEmbed
                ));
            }

            //------------------ BUILD -----------------
            String html = loadResource("htmlTemplate/html/template_feature_pasta.html");

            // adiciona resources
            htmlCss.append(String.format(HTML_CSS, loadResource("htmlTemplate/dist/feature-pasta.min.css")));
            htmlJavascript.append(String.format(HTML_JAVASCRIPT, loadResource("htmlTemplate/dist/feature-pasta.min.js")));
            htmlJavascript.append(String.format(HTML_JAVASCRIPT, loadResource("htmlTemplate/dist/feature-pasta-angular.min.js")));

            html = html.replace("#PROJECT_NAME#", projectName);
            html = html.replace("#PROJECT_VERSION#", projecVersion);
            html = html.replace("#HTML_MENU#", parseMenu.getHtml());
            html = html.replace("#HTML_CSS#", htmlCss);
            html = html.replace("#HTML_JAVASCRIPT#", htmlJavascript);
            html = html.replace("#HTML_TEMPLATE#", htmlTemplate);
            html = html.replace("#PROJECT_COLOR#", Compilador.COR_MENU);

            // monta cabeçalho menu
            if(Compilador.LOGO_PATH != null && Compilador.LOGO_PATH.isFile()){
                String logoString = ParseImage.parse(Compilador.LOGO_PATH);
                html = html.replace("#PROJECT_LOGO#", String.format("<img class=\"logo\" src=\"%s\">", logoString));
            }else{
                html = html.replace("#PROJECT_LOGO#", String.format(
                        "%s <small><em>%s</em></small>",
                        projectName,
                        projecVersion
                ));
            }

            // Grava
            // Cria Diretório se não existir */html/feature/
            String outDir = (outputDir != null ? outputDir : curDir.getParent() + "/html/");
            File outDirF = new File(outDir);

            if(!outDirF.exists()){
                outDirF.mkdir();
            }

            writeHtml(html, outDir + "index.html");
        }
    }

    public void compilarFeature(String featurePath, String projectName, String projecVersion, String outputDir) throws IOException {
        // Abre feature
        File feature = new File(featurePath);

        // compila
        List<String> pathList = new ArrayList<>();
        pathList.add(feature.getAbsolutePath().replace(feature.getName(), ""));
        String featureHtml = getFeatureHtml(feature.getAbsolutePath(), pathList);

        //------------------ BUILD -----------------
        String html = loadResource("htmlTemplate/html/template_feature.html");

        String htmlCss = String.format(HTML_CSS, loadResource("htmlTemplate/dist/feature.min.css"));

        html = html.replace("#PROJECT_NAME#", projectName);
        html = html.replace("#PROJECT_VERSION#", projecVersion);
        html = html.replace("#PROJECT_FEATURE#", feature.getName().replace(".feature", ""));
        html = html.replace("#HTML_CSS#", htmlCss);
        html = html.replace("#HTML_TEMPLATE#", featureHtml);

        // Grava
        String outDir = (outputDir != null ? outputDir : feature.getParent());
        outDir += String.format("/%s.html", feature.getName().replace(".feature", ""));

        writeHtml(html, outDir);
    }

    private void writeHtml(String html, String path) throws IOException {
        FileOutputStream fos = null;
        OutputStreamWriter osw = null;
        Writer out = null;

        try {
            fos = new FileOutputStream(path);
            osw = new OutputStreamWriter(fos, "UTF-8");
            out = new BufferedWriter(osw);
            out.write(html);
        }catch (Exception ea){
            throw ea;
        } finally {
            if(out != null){
                out.close();
            }

            if(osw != null){
                osw.close();
            }

            if(fos != null) {
                fos.close();
            }
        }
    }

    public void compilarFeaturePdf(String featurePath, String projectName, String projecVersion, String layout) throws Exception {
        // Abre feature
        File feature = new File(featurePath);

        //------------------ BUILD -----------------
        String htmlTemplate = loadResource("htmlTemplate/html/template_feature_pdf.html");
        String css = loadResource("htmlTemplate/dist/feature-pdf.min.css");

        List<String> pathList = new ArrayList<>();
        pathList.add(feature.getAbsolutePath().replace(feature.getName(), ""));
        String html = getFeatureHtml(feature.getAbsolutePath(), pathList);

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
        StringBuilder html = new StringBuilder();

        // Abre pasta root
        File curDir = new File(dir);

        // Popula com arquivos feature
        arquivos = new ArrayList<>();
        listarPasta(curDir);

        if(arquivos.size() > 0) {
            for (File f : arquivos) {
                if (f.getName().contains(".feature")) {
                    List<String> pathList = new ArrayList<>();
                    pathList.add(dir);
                    pathList.add(f.getAbsolutePath().replace(f.getName(), ""));

                    String rawHtml = getFeatureHtml(f.getAbsolutePath(), pathList);

                    html.append(String.format(
                            HTML_FEATURE_PDF,
                            projectName,
                            f.getName().replace(".feature", ""),
                            projecVersion,
                            rawHtml
                    ));
                }
            }

            //------------------ BUILD -----------------
            String htmlTemplate = loadResource("htmlTemplate/html/template_feature_pdf.html");
            String css = loadResource("htmlTemplate/dist/feature-pdf.min.css");

            html.append(htmlTemplate.replace("#HTML_TEMPLATE#", html));

            ParsePdf pp = new ParsePdf();

            String outDir = curDir.getParent() + "/html/";
            File outDirF = new File(outDir);

            if(!outDirF.exists()){
                outDirF.mkdir();
            }

            pp.buildHtml(outDir + "index.pdf", html.toString(), css, layout);
        }
    }

    private String loadResource(String src) throws IOException {
        StringBuilder buffer = new StringBuilder();
        String linha;
        BufferedReader br;

        URL url = Thread.currentThread().getContextClassLoader().getResource(Main.SYS_PATH + src);

        if(url != null) {
            try {
                br = new BufferedReader(new FileReader(url.getFile()), 200 * 1024);
            } catch (Exception ea) {
                logger.warn(Compilador.class.getName(), ea);
                br = new BufferedReader(new InputStreamReader(url.openStream()), 200 * 1024);
            }

            while ((linha = br.readLine()) != null) {
                buffer.append(linha).append("\n");
            }

            br.close();
        } else {
            logger.warn("Falha ao carregar Resource");
        }

        return buffer.toString();
    }

    private String loadFeature(String pathFeature){
        StringBuilder buffer = new StringBuilder();
        String toReturn = "";
        String linha;
        BufferedReader br;

        try {
            BOMInputStream bis = new BOMInputStream(new FileInputStream(pathFeature));

            br = new BufferedReader(new InputStreamReader(bis, "UTF-8"));

            while ((linha = br.readLine()) != null) {
                buffer.append(linha).append("\n");
            }

            toReturn = buffer.toString().replaceAll("\\t", "   ");
            toReturn = toReturn.trim();
        }catch (Exception e){
            logger.warn(Compilador.class.getName(), e);
        }

        return toReturn;
    }

    private String md5(String str){
        String md5 = "";

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md5 = String.format("%032x", new BigInteger(1, md.digest(str.getBytes())));
        } catch (NoSuchAlgorithmException e) {
            logger.warn(Compilador.class.getName(), e);
        }

        return md5;
    }

    public String absoluteNameFeature(String path, String absolutePath){
        path = path.replaceAll("\\\\", "");
        absolutePath = absolutePath.replaceAll("\\\\", "");

        path = path.replaceAll("\\/", "");
        absolutePath = absolutePath.replaceAll("\\/", "");

        return absolutePath.replace(path, "");
    }
}
