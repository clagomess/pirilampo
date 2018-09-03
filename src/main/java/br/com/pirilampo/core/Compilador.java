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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Compilador {
    public static StringBuilder LOG;
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

    private String getFeatureHtml(Parametro parametro, String pathFeature, List<String> pathList) throws IOException {
        Parser<GherkinDocument> parser = new Parser<>(new AstBuilder());
        TokenMatcher matcher = new TokenMatcher();
        String html = null;

        try (FileInputStream fis = new FileInputStream(pathFeature)) {
            // BOMInputStream para caso o arquivo possuir BOM
            BOMInputStream bis = new BOMInputStream(fis);

            Reader in = new InputStreamReader(bis, StandardCharsets.UTF_8);

            GherkinDocument gherkinDocument = parser.parse(in, matcher);

            if (gherkinDocument != null) {
                ParseDocument pd = new ParseDocument(parametro, gherkinDocument, pathList);
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
            arquivosMaster = ListarPasta.listarPasta(curDirMaster);
        }

        // -------- NORMAL
        // Abre pasta root
        File curDir = new File(parametro.getTxtSrcFonte());

        // Popula com arquivos feature
        final List<File> arquivos = ListarPasta.listarPasta(curDir);

        if(arquivos.size() > 0){
            for(File f : arquivos){
                // monta nome menu
                String htmlFeatureRoot = f.getAbsolutePath().replace(curDir.getAbsolutePath(), "");
                htmlFeatureRoot = htmlFeatureRoot.replace(f.getName(), "");
                htmlFeatureRoot = htmlFeatureRoot.replace(File.separator, " ");
                htmlFeatureRoot = htmlFeatureRoot.trim();
                htmlFeatureRoot = !StringUtils.isEmpty(parametro.getTxtNomeMenuRaiz()) ? parametro.getTxtNomeMenuRaiz() : htmlFeatureRoot;

                String htmlFeatureId = htmlFeatureRoot + "_" + f.getName().replace(Resource.getExtension(f), ".html");

                // Processa Master
                if(!StringUtils.isEmpty(parametro.getTxtSrcFonteMaster())) {
                    boolean diferente = true;
                    File fmd = null;

                    if(arquivosMaster != null && !arquivosMaster.isEmpty()) {
                        for (File fm : arquivosMaster) {
                            String absoluteNFM = Resource.absoluteNameFeature(parametro.getTxtSrcFonteMaster(), fm.getAbsolutePath());
                            String absoluteNFB = Resource.absoluteNameFeature(parametro.getTxtSrcFonte(), f.getAbsolutePath());
                            String absoluteNFMMd5 = Resource.md5(Resource.loadFeature(fm.getAbsolutePath()));
                            String absoluteNFBMd5 = Resource.md5(Resource.loadFeature(f.getAbsolutePath()));

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

                        String featureHtml = getFeatureHtml(parametro, fmd.getAbsolutePath(), pathListMaster);

                        htmlTemplate.append(String.format(
                                HTML_TEMPLATE,
                                "master_" + htmlFeatureId,
                                featureHtml
                        ));
                        htmlTemplate.append(String.format(
                                HTML_TEMPLATE,
                                "master_" + htmlFeatureId.replace(".html", ".feature"),
                                Resource.loadFeature(fmd.getAbsolutePath())
                        ));
                    }
                }

                // Adiciona item de menu se deu tudo certo com a master
                if(parametro.getTxtNomeMenuRaiz().equals(htmlFeatureRoot)){
                    parseMenu.addMenuItem(
                            htmlFeatureRoot +
                            File.separator +
                            f.getAbsolutePath().replace(curDir.getAbsolutePath(), "").replace(Resource.getExtension(f), ".feature")
                    );
                }else{
                    parseMenu.addMenuItem(f.getAbsolutePath().replace(curDir.getAbsolutePath().replace(Resource.getExtension(f), ".feature"), ""));
                }

                // Gera a feture
                List<String> pathList = new ArrayList<>();
                pathList.add(parametro.getTxtSrcFonte());
                pathList.add(f.getAbsolutePath().replace(f.getName(), ""));

                String featureHtml = getFeatureHtml(parametro, f.getAbsolutePath(), pathList);

                htmlTemplate.append(String.format(HTML_TEMPLATE, htmlFeatureId, featureHtml));

                // Salva as feature para diff
                if(!StringUtils.isEmpty(parametro.getTxtSrcFonteMaster())){
                    htmlTemplate.append(String.format(
                            HTML_TEMPLATE,
                            htmlFeatureId.replace(".html", ".feature"),
                            Resource.loadFeature(f.getAbsolutePath())
                    ));
                }
            }

            // adiciona html embed
            for (File htmlEmbed : Compilador.PAGINA_HTML_ANEXO){
                String loadedHtmlEmbed = Resource.loadFeature(htmlEmbed.getAbsolutePath());
                htmlTemplate.append(String.format(
                        "<template type=\"text/ng-template\" id=\"%s\">%s</template>%n",
                        htmlEmbed.getName(),
                        loadedHtmlEmbed
                ));
            }

            //------------------ BUILD -----------------
            String html = Resource.loadResource("htmlTemplate/html/template_feature_pasta.html");

            // adiciona resources
            htmlCss.append(String.format(HTML_CSS, Resource.loadResource("htmlTemplate/dist/feature-pasta.min.css")));
            htmlJavascript.append(String.format(HTML_JAVASCRIPT, Resource.loadResource("htmlTemplate/dist/feature-pasta.min.js")));
            htmlJavascript.append(String.format(HTML_JAVASCRIPT, Resource.loadResource("htmlTemplate/dist/feature-pasta-angular.min.js")));

            html = html.replace("#PROJECT_NAME#", parametro.getTxtNome());
            html = html.replace("#PROJECT_VERSION#", parametro.getTxtVersao());
            html = html.replace("#HTML_MENU#", parseMenu.getHtml());
            html = html.replace("#HTML_CSS#", htmlCss);
            html = html.replace("#HTML_JAVASCRIPT#", htmlJavascript);
            html = html.replace("#HTML_TEMPLATE#", htmlTemplate);
            html = html.replace("#MENU_COLOR#", parametro.getClrMenu());
            html = html.replace("#MENU_TEXT_COLOR#", parametro.getClrTextoMenu());

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

            Resource.writeHtml(html, outDir + "index.html");
        }
    }

    public void compilarFeature(Parametro parametro) throws IOException {
        // Abre feature
        File feature = new File(parametro.getTxtSrcFonte());

        // compila
        List<String> pathList = new ArrayList<>();
        pathList.add(feature.getAbsolutePath().replace(feature.getName(), ""));
        String featureHtml = getFeatureHtml(parametro, feature.getAbsolutePath(), pathList);

        //------------------ BUILD -----------------
        String html = Resource.loadResource("htmlTemplate/html/template_feature.html");

        String htmlCss = String.format(HTML_CSS, Resource.loadResource("htmlTemplate/dist/feature.min.css"));

        html = html.replace("#PROJECT_NAME#", parametro.getTxtNome());
        html = html.replace("#PROJECT_VERSION#", parametro.getTxtVersao());
        html = html.replace("#PROJECT_FEATURE#", feature.getName().replace(Resource.getExtension(feature), ""));
        html = html.replace("#HTML_CSS#", htmlCss);
        html = html.replace("#HTML_TEMPLATE#", featureHtml);

        // Grava
        String outDir = (parametro.getTxtOutputTarget() != null ? parametro.getTxtOutputTarget() : feature.getParent());
        outDir += String.format("/%s.html", feature.getName().replace(Resource.getExtension(feature), ""));

        Resource.writeHtml(html, outDir);
    }

    public void compilarFeaturePdf(Parametro parametro) throws Exception {
        // Abre feature
        File feature = new File(parametro.getTxtSrcFonte());

        //------------------ BUILD -----------------
        String htmlTemplate = Resource.loadResource("htmlTemplate/html/template_feature_pdf.html");
        String css = Resource.loadResource("htmlTemplate/dist/feature-pdf.min.css");

        List<String> pathList = new ArrayList<>();
        pathList.add(feature.getAbsolutePath().replace(feature.getName(), ""));
        String html = getFeatureHtml(parametro, feature.getAbsolutePath(), pathList);

        html = String.format(
                HTML_FEATURE_PDF,
                parametro.getTxtNome(),
                feature.getName().replace(Resource.getExtension(feature), ""),
                parametro.getTxtVersao(),
                html
        );

        html = htmlTemplate.replace("#HTML_TEMPLATE#", html);

        ParsePdf pp = new ParsePdf();

        String path = feature.getParent() + String.format("/%s.pdf", feature.getName().replace(Resource.getExtension(feature), ""));

        pp.buildHtml(path, html, css, parametro.getTipLayoutPdf().getValue());
    }

    public void compilarPastaPdf(Parametro parametro) throws Exception {
        StringBuilder html = new StringBuilder();

        // Abre pasta root
        File curDir = new File(parametro.getTxtSrcFonte());

        // Popula com arquivos feature
        List<File> arquivos = ListarPasta.listarPasta(curDir);

        if(!arquivos.isEmpty()) {
            for (File f : arquivos) {
                List<String> pathList = new ArrayList<>();
                pathList.add(parametro.getTxtSrcFonte());
                pathList.add(f.getAbsolutePath().replace(f.getName(), ""));

                String rawHtml = getFeatureHtml(parametro, f.getAbsolutePath(), pathList);

                html.append(String.format(
                        HTML_FEATURE_PDF,
                        parametro.getTxtNome(),
                        f.getName().replace(Resource.getExtension(f), ""),
                        parametro.getTxtVersao(),
                        rawHtml
                ));
            }

            //------------------ BUILD -----------------
            String htmlTemplate = Resource.loadResource("htmlTemplate/html/template_feature_pdf.html");
            String css = Resource.loadResource("htmlTemplate/dist/feature-pdf.min.css");

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
}
