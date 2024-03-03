package br.com.pirilampo.core.compilers;

import br.com.pirilampo.core.bean.Indice;
import br.com.pirilampo.core.dto.FeatureMetadataDto;
import br.com.pirilampo.core.dto.ParametroDto;
import br.com.pirilampo.core.enums.DiffEnum;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class FolderToHTMLCompiler extends Compiler {
    private final ParametroDto parametro;
    private final Map<String, Indice> indice = new HashMap<>();
    private List<File> arquivosMaster = null;

    public static final String HTML_OPEN_TEMPLATE = "<script type=\"text/ng-template\" id=\"%s\">";
    public static final String HTML_CLOSE_TEMPLATE = "</script>\n";

    protected DiffEnum diffMaster(FeatureMetadataDto featureMetadataDto, File featureBranch, PrintWriter out) throws Exception {
        if(parametro.getTxtSrcFonteMaster() == null) return DiffEnum.NAO_COMPARADO;
        if(arquivosMaster == null) this.arquivosMaster = listFolder(parametro.getTxtSrcFonteMaster());
        if(arquivosMaster.isEmpty()) return DiffEnum.NAO_COMPARADO;

        DiffEnum diff = DiffEnum.NOVO;
        File featureMasterCompared = null;

        for (File featureMaster : arquivosMaster) {
            // @TODO: reduce comparing
            String pathFeatureMaster = getFeaturePathWithoutAbsolute(
                    parametro.getTxtSrcFonteMaster(),
                    featureMaster
            );

            String pathFeatureBranch = getFeaturePathWithoutAbsolute(
                    parametro.getTxtSrcFonte(),
                    featureBranch
            );

            if (pathFeatureMaster.equals(pathFeatureBranch)) {
                if(FileUtils.contentEquals(featureBranch, featureMaster)){
                    diff = DiffEnum.IGUAL;
                }else{
                    diff = DiffEnum.DIFERENTE;
                    featureMasterCompared = featureMaster;
                }
                break;
            }
        }

        log.info("Diff Master/Branch: {} - {}", diff, featureBranch.getAbsolutePath());

        // pula para o proximo
        if(diff.equals(DiffEnum.IGUAL)) return diff;

        if(featureMasterCompared != null) {
            out.print(String.format(HTML_OPEN_TEMPLATE, "master_" + featureMetadataDto.getIdHtml()));
            new ParseDocument(parametro, featureMasterCompared).build(out);
            out.print(HTML_CLOSE_TEMPLATE);

            out.print(String.format(HTML_OPEN_TEMPLATE, "master_" + featureMetadataDto.getIdFeature()));
            writeFileToOut(featureMasterCompared, out);
            out.print(HTML_CLOSE_TEMPLATE);

            out.print(String.format(HTML_OPEN_TEMPLATE, featureMetadataDto.getIdFeature()));
            writeFileToOut(featureBranch, out);
            out.print(HTML_CLOSE_TEMPLATE);
        }

        return diff;
    }

    protected void buildMenu(PrintWriter out){
        out.print("<div id=\"sidebar-wrapper\">");
        out.print("<ul class=\"sidebar-nav\">");
        out.print("<li class=\"sidebar-brand\">");

        if(parametro.getTxtLogoSrc() != null){
            String logoString = ParseImage.parse(parametro, parametro.getTxtLogoSrc()); //@TODO: transformar em buffer
            out.print(String.format("<a href=\"#/\"><img class=\"logo\" src=\"%s\"></a>", logoString));
        }else{
            out.print(String.format(
                    "<a href=\"#/\">%s <small><em>%s</em></small></a>",
                    parametro.getTxtNome(),
                    parametro.getTxtVersao()
            ));
        }

        out.print("</li>");
        out.print("#HTML_MENU#"); //@TODO: vai ter que ser alimentado via javascript por causa da ordem exec
        out.print("</ul>");
        out.print("</div>");
    }

    protected void buildTemplateIndex(PrintWriter out){
        out.print(String.format(HTML_OPEN_TEMPLATE, "index.html"));
        out.print("<div style=\"text-align: center\">");
        out.print(String.format("<h1>%s</h1>", parametro.getTxtNome()));
        out.print(String.format("<small><em>%s</em></small>", parametro.getTxtVersao()));
        out.print("</div>");
        out.print(HTML_CLOSE_TEMPLATE);
    }

    protected void buildIndex(PrintWriter out) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(indice);
        out.println(String.format("var indice = %s;", json)); //@TODO: melhorar performance
    }

    public void build() throws Exception {
        ParseMenu parseMenu = new ParseMenu(parametro);

        // Popula com arquivos feature
        final List<File> arquivos = listFolder(parametro.getTxtSrcFonte());
        if(arquivos.isEmpty()) return;

        try (
                FileOutputStream fos = new FileOutputStream(getOutArtifact(parametro));
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8));
                PrintWriter out = new PrintWriter(bw);
        ){
            out.print("<!DOCTYPE html><html lang=\"en\" data-ng-app=\"pirilampoApp\"><head>");
            out.print("<meta charset=\"utf-8\">");
            out.print("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">");
            out.print("<meta name=\"viewport\" content=\"width=device-width, shrink-to-fit=no, initial-scale=1\">");
            out.print(String.format("<title>%s</title>", parametro.getTxtNome()));
            out.print("<style>");
            writeResourceToOut("htmlTemplate/dist/feature-pasta.min.css", out);
            out.print("\n\n");
            out.print(String.format(
                    "#sidebar-wrapper {background: %s;}",
                    parametro.getClrMenu()
            ));
            out.print(String.format(
                    "#menu-toggle {background: %s; color: %s;}",
                    parametro.getClrMenu(),
                    parametro.getClrTextoMenu()
            ));
            out.print(String.format(
                    ".sidebar-nav li a:hover, .sidebar-nav li a { color: %s;}",
                    parametro.getClrTextoMenu()
            ));
            out.print(String.format(
                    ".sidebar-nav > li {border-bottom: 1px solid %s;}",
                    parametro.getClrTextoMenu()
            ));
            out.print("</style>");
            out.print("</head><body>\n");
            out.print("<div id=\"wrapper\">");
            buildMenu(out);
            writeResourceToOut("htmlTemplate/html/template-feature-pasta-content-wrapper.html", out);
            out.print("</div>");

            int progressNum = 1;

            for(File f : arquivos){
                // progress
                //ProgressBind.setProgress(progressNum / (double) arquivos.size()); @TODO: check
                progressNum++;

                // monta nome menu
                FeatureMetadataDto featureMetadataDto = getFeatureMetadata(parametro, f);

                // Processa Diff Master
                DiffEnum diff = diffMaster(featureMetadataDto, f, out);
                if(diff == DiffEnum.IGUAL) continue;

                // Gera a feture
                ParseDocument pd = new ParseDocument(parametro, f);
                indice.putAll(pd.getIndice());
                parseMenu.addMenuItem(f, diff, pd.getFeatureTitulo());

                out.print(String.format(HTML_OPEN_TEMPLATE, featureMetadataDto.getIdHtml()));
                pd.build(out);
                out.print(HTML_CLOSE_TEMPLATE);

                // adiciona html embed
                for (File htmlEmbed : pd.getPaginaHtmlAnexo()){
                    out.print(String.format(HTML_OPEN_TEMPLATE, htmlEmbed.getName()));
                    writeFileToOut(htmlEmbed, out);
                    out.print(HTML_CLOSE_TEMPLATE);
                }

            }

            buildTemplateIndex(out);
            writeResourceToOut("htmlTemplate/html/template-feature-pasta-footer.html", out);

            out.print("<script type=\"text/javascript\">\n");
            buildIndex(out);
            // parseMenu.getHtml(); //@TODO: prever solução
            writeResourceToOut("htmlTemplate/dist/feature-pasta.min.js", out);
            writeResourceToOut("htmlTemplate/dist/feature-pasta-angular.min.js", out);
            out.print("</script>\n");
            out.print("</body></html>");
        }
    }
}
