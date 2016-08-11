package br.com.pirilampo.util;

import br.com.pirilampo.main.Main;
import gherkin.AstBuilder;
import gherkin.Parser;
import gherkin.ParserException;
import gherkin.TokenMatcher;
import gherkin.ast.GherkinDocument;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Compilador {
    private List<File> arquivos = new ArrayList<>();
    private final String HTML_TEMPLATE = "<script type=\"text/ng-template\" id=\"%s\">%s</script>\n";
    private final String HTML_JAVASCRIPT = "<script type=\"text/javascript\">%s</script>\n";
    private final String HTML_CSS = "<style>%s</style>\n";

    private void listarPasta(File curDir){
        File[] filesList = curDir.listFiles();

        if(filesList != null) {
            for (File f : filesList) {
                if (f.isDirectory()) {
                    listarPasta(f);
                }

                if (f.isFile()) {
                    arquivos.add(f);
                }
            }
        }
    }

    private String getFeatureHtml(String pathFeature){
        Parser<GherkinDocument> parser = new Parser<>(new AstBuilder());
        TokenMatcher matcher = new TokenMatcher();
        Reader in =  null;
        String html = null;

        try {
            in = new InputStreamReader(new FileInputStream(pathFeature), "UTF-8");
        }catch (IOException e){
            e.printStackTrace();
        }

        try {
            if(in != null) {
                GherkinDocument gherkinDocument = parser.parse(in, matcher);

                if(gherkinDocument != null){
                    ParseDocument pd = new ParseDocument(gherkinDocument);
                    html = pd.getHtml();
                }
            }
        } catch (ParserException e) {
            e.printStackTrace();
        }

        return html;
    }

    private void compilarPastaItem(File pathPasta, File pathFeature){
        String html = getFeatureHtml(pathFeature.getAbsolutePath());

        try {
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
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void compilarPasta(String dir){
        Map<String, List<String>> menu = new HashMap<>();
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
            final String HTML_MENU_PAI = "<li><a href=\"#\">%s</a><ul>%s</ul></li>";
            final String HTML_MENU_FILHO = "<li><a href=\"#/feature/%s\">%s</a></li>";

            for (Map.Entry<String, List<String>> entry : menu.entrySet()) {
                String filhos = "";

                for(String item : entry.getValue()){
                    filhos += String.format(HTML_MENU_FILHO, item, item);
                }

                htmlMenu += String.format(HTML_MENU_PAI, entry.getKey(), filhos);
            }


            // adiciona libs
            htmlCss += String.format(HTML_CSS, loadResource("htmlTemplate/css/bootstrap.min.css"));
            htmlCss += String.format(HTML_CSS, loadResource("htmlTemplate/css/simple-sidebar.css"));

            htmlJavascript += String.format(HTML_JAVASCRIPT, loadResource("htmlTemplate/lib/jquery.js"));
            htmlJavascript += String.format(HTML_JAVASCRIPT, loadResource("htmlTemplate/lib/bootstrap.min.js"));
            htmlJavascript += String.format(HTML_JAVASCRIPT, loadResource("htmlTemplate/lib/angular.min.js"));
            htmlJavascript += String.format(HTML_JAVASCRIPT, loadResource("htmlTemplate/js/app.js"));
            htmlJavascript += String.format(HTML_JAVASCRIPT, loadResource("htmlTemplate/js/featureController.js"));
            htmlJavascript += String.format(HTML_JAVASCRIPT, loadResource("htmlTemplate/lib/angular-route.min.js"));
            htmlJavascript += String.format(HTML_JAVASCRIPT, loadResource("htmlTemplate/lib/angular-resource.min.js"));
            htmlJavascript += String.format(HTML_JAVASCRIPT, loadResource("htmlTemplate/lib/angular-ui-router.min.js"));

            html = html.replace("#HTML_MENU#", htmlMenu);
            html = html.replace("#HTML_CSS#", htmlCss);
            html = html.replace("#HTML_JAVASCRIPT#", htmlJavascript);
            html = html.replace("#HTML_TEMPLATE#", htmlTemplate);

            // Grava
            try {
                File feature = new File(curDir.getParent() + "/index.html");
                FileWriter fwrite = new FileWriter(feature);
                fwrite.write(html);
                fwrite.flush();
                fwrite.close();
            }catch (IOException e){
                e.printStackTrace();
            }

            System.out.println(menu);
        }
    }

    private String loadResource(String src){
        String buffer = "";
        String linha;

        URL url = Thread.currentThread().getContextClassLoader().getResource(Main.SYS_PATH + src);

        try{
            BufferedReader br = new BufferedReader(new FileReader(url.getFile()), 200 * 1024);

            while ((linha = br.readLine()) != null) {
                buffer += linha + "\n";
            }

            br.close();
        }catch(IOException e){
            e.printStackTrace();
        }

        return buffer;
    }
}
