package br.com.pirilampo.core;

import br.com.pirilampo.bean.Parametro;
import gherkin.AstBuilder;
import gherkin.Parser;
import gherkin.TokenMatcher;
import gherkin.ast.GherkinDocument;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.math.BigInteger;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class Compilador {
    public static StringBuilder LOG;
    private List<File> arquivos = new ArrayList<>();
    private final String HTML_TEMPLATE = "<script type=\"text/ng-template\" id=\"%s\">%s</script>\n";
    private final String HTML_JAVASCRIPT = "<script type=\"text/javascript\">%s</script>\n";
    private final String HTML_CSS = "<style>%s</style>\n";
    private final String HTML_FEATURE_PDF = "<h1 class=\"page-header\">%s <small>%s <em>%s</em></small></h1>\n" +
            "%s\n<span style=\"page-break-after: always\"></span>";
    static List<File> PAGINA_HTML_ANEXO;

    public Compilador(){
        Compilador.LOG = new StringBuilder();
        Compilador.PAGINA_HTML_ANEXO = new ArrayList<>();
    }

    //====== Metodos

    private void listarPasta(File curDir) throws Exception {
        File[] filesList = curDir.listFiles();

        if(filesList != null) {
            for (File f : filesList) {
                if (f.isDirectory()) {
                    listarPasta(f);
                }

                if (f.isFile() && ".feature".equalsIgnoreCase(getExtension(f))) {
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

        try (FileInputStream fis = new FileInputStream(pathFeature)) {
            // BOMInputStream para caso o arquivo possuir BOM
            BOMInputStream bis = new BOMInputStream(fis);

            Reader in = new InputStreamReader(bis, "UTF-8");

            GherkinDocument gherkinDocument = parser.parse(in, matcher);

            if (gherkinDocument != null) {
                ParseDocument pd = new ParseDocument(gherkinDocument, pathList);
                html = pd.getHtml();
            }

            Compilador.LOG.append("OK: ").append(pathFeature).append("\n");
            log.info("OK: {}", pathFeature);
        } catch (Exception e){
            Compilador.LOG.append("ERRRROU: ").append(pathFeature).append("\n");
            log.warn("ERRRROU: " + pathFeature);
            throw e;
        }

        return html;
    }

    public void compilarPasta(Parametro parametro) throws Exception {
        ParseMenu parseMenu = new ParseMenu();
        StringBuilder htmlTemplate = new StringBuilder();
        StringBuilder htmlJavascript = new StringBuilder();
        StringBuilder htmlCss = new StringBuilder();

        // -------- MASTER
        List<File> arquivosMaster = null;
        if(!StringUtils.isEmpty(parametro.getTxtSrcFonteMaster())) {
            // Abre pasta root
            File curDirMaster = new File(parametro.getTxtSrcFonteMaster());

            // Popula com arquivos feature
            arquivos = new ArrayList<>();
            listarPasta(curDirMaster);
            arquivosMaster = arquivos;
        }

        // -------- NORMAL
        // Abre pasta root
        File curDir = new File(parametro.getTxtSrcFonte());

        // Popula com arquivos feature
        arquivos = new ArrayList<>();
        listarPasta(curDir);

        if(arquivos.size() > 0){
            for(File f : arquivos){
                // monta nome menu
                String htmlFeatureRoot = f.getAbsolutePath().replace(curDir.getAbsolutePath(), "");
                htmlFeatureRoot = htmlFeatureRoot.replace(f.getName(), "");
                htmlFeatureRoot = htmlFeatureRoot.replace(File.separator, " ");
                htmlFeatureRoot = htmlFeatureRoot.trim();
                htmlFeatureRoot = !StringUtils.isEmpty(parametro.getTxtNomeMenuRaiz()) ? parametro.getTxtNomeMenuRaiz() : htmlFeatureRoot;

                String htmlFeatureId = htmlFeatureRoot + "_" + f.getName().replace(getExtension(f), ".html");

                // Processa Master
                if(!StringUtils.isEmpty(parametro.getTxtSrcFonteMaster())) {
                    boolean diferente = true;
                    File fmd = null;

                    if(arquivosMaster != null && !arquivosMaster.isEmpty()) {
                        for (File fm : arquivosMaster) {
                            String absoluteNFM = absoluteNameFeature(parametro.getTxtSrcFonteMaster(), fm.getAbsolutePath());
                            String absoluteNFB = absoluteNameFeature(parametro.getTxtSrcFonte(), f.getAbsolutePath());
                            String absoluteNFMMd5 = md5(loadFeature(fm.getAbsolutePath()));
                            String absoluteNFBMd5 = md5(loadFeature(f.getAbsolutePath()));

                            if (absoluteNFM.equals(absoluteNFB)) {
                                if(absoluteNFMMd5.equals(absoluteNFBMd5)){
                                    diferente = false;
                                }else{
                                    fmd = fm;
                                    // Debug
                                    log.info(
                                            "Diff Master/Branch: {} - {} - {} - {}",
                                            absoluteNFMMd5,
                                            absoluteNFBMd5,
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
                        pathListMaster.add(parametro.getTxtSrcFonteMaster());
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
                if(parametro.getTxtNomeMenuRaiz().equals(htmlFeatureRoot)){
                    parseMenu.addMenuItem(
                            htmlFeatureRoot +
                            File.separator +
                            f.getAbsolutePath().replace(curDir.getAbsolutePath(), "").replace(getExtension(f), ".feature")
                    );
                }else{
                    parseMenu.addMenuItem(f.getAbsolutePath().replace(curDir.getAbsolutePath().replace(getExtension(f), ".feature"), ""));
                }

                // Gera a feture
                List<String> pathList = new ArrayList<>();
                pathList.add(parametro.getTxtSrcFonte());
                pathList.add(f.getAbsolutePath().replace(f.getName(), ""));

                String featureHtml = getFeatureHtml(f.getAbsolutePath(), pathList);

                htmlTemplate.append(String.format(HTML_TEMPLATE, htmlFeatureId, featureHtml));

                // Salva as feature para diff
                if(parametro.getTxtSrcFonteMaster() != null){
                    htmlTemplate.append(String.format(
                            HTML_TEMPLATE,
                            htmlFeatureId.replace(".html", ".feature"),
                            loadFeature(f.getAbsolutePath())
                    ));
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

            html = html.replace("#PROJECT_NAME#", parametro.getTxtNome());
            html = html.replace("#PROJECT_VERSION#", parametro.getTxtVersao());
            html = html.replace("#HTML_MENU#", parseMenu.getHtml());
            html = html.replace("#HTML_CSS#", htmlCss);
            html = html.replace("#HTML_JAVASCRIPT#", htmlJavascript);
            html = html.replace("#HTML_TEMPLATE#", htmlTemplate);
            html = html.replace("#PROJECT_COLOR#", parametro.getClrMenu());

            // monta cabeçalho menu
            if(!StringUtils.isEmpty(parametro.getTxtLogoSrc())){
                String logoString = ParseImage.parse(new File(parametro.getTxtLogoSrc()));
                html = html.replace("#PROJECT_LOGO#", String.format("<img class=\"logo\" src=\"%s\">", logoString));
            }else{
                html = html.replace("#PROJECT_LOGO#", String.format(
                        "%s <small><em>%s</em></small>",
                        parametro.getTxtNome(),
                        parametro.getTxtVersao()
                ));
            }

            // Grava
            // Cria Diretório se não existir */html/feature/
            String outDir = (parametro.getTxtOutputTarget() != null ? parametro.getTxtOutputTarget() : curDir.getParent() + "/html/");
            File outDirF = new File(outDir);

            if(!outDirF.exists()){
                outDirF.mkdir();
            }

            writeHtml(html, outDir + "index.html");
        }
    }

    public void compilarFeature(Parametro parametro) throws IOException {
        // Abre feature
        File feature = new File(parametro.getTxtSrcFonte());

        // compila
        List<String> pathList = new ArrayList<>();
        pathList.add(feature.getAbsolutePath().replace(feature.getName(), ""));
        String featureHtml = getFeatureHtml(feature.getAbsolutePath(), pathList);

        //------------------ BUILD -----------------
        String html = loadResource("htmlTemplate/html/template_feature.html");

        String htmlCss = String.format(HTML_CSS, loadResource("htmlTemplate/dist/feature.min.css"));

        html = html.replace("#PROJECT_NAME#", parametro.getTxtNome());
        html = html.replace("#PROJECT_VERSION#", parametro.getTxtVersao());
        html = html.replace("#PROJECT_FEATURE#", feature.getName().replace(getExtension(feature), ""));
        html = html.replace("#HTML_CSS#", htmlCss);
        html = html.replace("#HTML_TEMPLATE#", featureHtml);

        // Grava
        String outDir = (parametro.getTxtOutputTarget() != null ? parametro.getTxtOutputTarget() : feature.getParent());
        outDir += String.format("/%s.html", feature.getName().replace(getExtension(feature), ""));

        writeHtml(html, outDir);
    }

    private void writeHtml(String html, String path) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(path)){
            OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
            Writer out = new BufferedWriter(osw);
            out.write(html);
            out.flush();
        }catch (Exception e){
            throw e;
        }
    }

    public void compilarFeaturePdf(Parametro parametro) throws Exception {
        // Abre feature
        File feature = new File(parametro.getTxtSrcFonte());

        //------------------ BUILD -----------------
        String htmlTemplate = loadResource("htmlTemplate/html/template_feature_pdf.html");
        String css = loadResource("htmlTemplate/dist/feature-pdf.min.css");

        List<String> pathList = new ArrayList<>();
        pathList.add(feature.getAbsolutePath().replace(feature.getName(), ""));
        String html = getFeatureHtml(feature.getAbsolutePath(), pathList);

        html = String.format(
                HTML_FEATURE_PDF,
                parametro.getTxtNome(),
                feature.getName().replace(getExtension(feature), ""),
                parametro.getTxtVersao(),
                html
        );

        html = htmlTemplate.replace("#HTML_TEMPLATE#", html);

        ParsePdf pp = new ParsePdf();

        String path = feature.getParent() + String.format("/%s.pdf", feature.getName().replace(getExtension(feature), ""));

        pp.buildHtml(path, html, css, parametro.getTipLayoutPdf().getValue());
    }

    public void compilarPastaPdf(Parametro parametro) throws Exception {
        StringBuilder html = new StringBuilder();

        // Abre pasta root
        File curDir = new File(parametro.getTxtSrcFonte());

        // Popula com arquivos feature
        arquivos = new ArrayList<>();
        listarPasta(curDir);

        if(!arquivos.isEmpty()) {
            for (File f : arquivos) {
                List<String> pathList = new ArrayList<>();
                pathList.add(parametro.getTxtSrcFonte());
                pathList.add(f.getAbsolutePath().replace(f.getName(), ""));

                String rawHtml = getFeatureHtml(f.getAbsolutePath(), pathList);

                html.append(String.format(
                        HTML_FEATURE_PDF,
                        parametro.getTxtNome(),
                        f.getName().replace(getExtension(f), ""),
                        parametro.getTxtVersao(),
                        rawHtml
                ));
            }

            //------------------ BUILD -----------------
            String htmlTemplate = loadResource("htmlTemplate/html/template_feature_pdf.html");
            String css = loadResource("htmlTemplate/dist/feature-pdf.min.css");

            html = new StringBuilder(htmlTemplate.replace("#HTML_TEMPLATE#", html));

            ParsePdf pp = new ParsePdf();

            String outDir = curDir.getParent() + "/html/";
            File outDirF = new File(outDir);

            if(!outDirF.exists()){
                outDirF.mkdir();
            }

            pp.buildHtml(outDir + "index.pdf", html.toString(), css, parametro.getTipLayoutPdf().getValue());
        }
    }

    private String loadResource(String src) throws IOException {
        StringBuilder buffer = new StringBuilder();
        String linha;

        URL url = Thread.currentThread().getContextClassLoader().getResource(src);

        if(url != null) {
            BufferedReader br;

            try (FileReader fr = new FileReader(url.getFile())) {
                br = new BufferedReader(fr, 200 * 1024);

                while ((linha = br.readLine()) != null) {
                    buffer.append(linha).append("\n");
                }
            } catch (Exception e) {
                try(InputStreamReader isr = new InputStreamReader(url.openStream())){
                    br = new BufferedReader(isr, 200 * 1024);

                    while ((linha = br.readLine()) != null) {
                        buffer.append(linha).append("\n");
                    }
                } catch(Exception ea){
                    log.warn(Compilador.class.getName(), e);
                    log.warn(Compilador.class.getName(), ea);
                }
            }
        } else {
            log.warn("Falha ao carregar Resource");
        }

        return buffer.toString();
    }

    private String loadFeature(String pathFeature){
        StringBuilder buffer = new StringBuilder();
        String toReturn = "";
        String linha;
        BufferedReader br;

        try (FileInputStream fis = new FileInputStream(pathFeature)){
            BOMInputStream bis = new BOMInputStream(fis);

            br = new BufferedReader(new InputStreamReader(bis, "UTF-8"));

            while ((linha = br.readLine()) != null) {
                buffer.append(linha).append("\n");
            }

            toReturn = buffer.toString().replaceAll("\\t", "   ");
            toReturn = toReturn.trim();
        }catch (Exception e){
            log.warn(Compilador.class.getName(), e);
        }

        return toReturn;
    }

    private String md5(String str){
        String md5 = "";

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md5 = String.format("%032x", new BigInteger(1, md.digest(str.getBytes())));
        } catch (NoSuchAlgorithmException e) {
            log.warn(Compilador.class.getName(), e);
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

    private String getExtension(File f){
        String ext = "";
        if(f != null && f.isFile()) {
            Pattern p = Pattern.compile("\\.[a-zA-Z]+$");
            Matcher m = p.matcher(f.getName());

            if (m.find()) {
                ext = m.group(0);
            }
        }

        return ext;
    }
}
