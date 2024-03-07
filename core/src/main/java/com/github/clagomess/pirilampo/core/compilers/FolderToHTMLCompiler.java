package com.github.clagomess.pirilampo.core.compilers;

import com.github.clagomess.pirilampo.core.dto.FeatureIndexDto;
import com.github.clagomess.pirilampo.core.dto.FeatureMasterDto;
import com.github.clagomess.pirilampo.core.dto.FeatureMetadataDto;
import com.github.clagomess.pirilampo.core.dto.ParametersDto;
import com.github.clagomess.pirilampo.core.enums.DiffEnum;
import com.github.clagomess.pirilampo.core.parsers.GherkinDocumentParser;
import com.github.clagomess.pirilampo.core.parsers.ImageParser;
import com.github.clagomess.pirilampo.core.parsers.MenuParser;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class FolderToHTMLCompiler extends Compiler {
    private final ImageParser imageParser = new ImageParser();
    private final ParametersDto parameters;
    private final Map<String, FeatureIndexDto> indice = new HashMap<>();
    private List<FeatureMasterDto> masterFiles = null;

    public static final String HTML_OPEN_TEMPLATE = "<script type=\"text/ng-template\" id=\"%s\">";
    public static final String HTML_CLOSE_TEMPLATE = "</script>\n";

    protected DiffEnum diffMaster(FeatureMetadataDto featureMetadataDto, File featureBranch, PrintWriter out) throws Exception {
        if(parameters.getProjectMasterSource() == null) return DiffEnum.NOT_COMPARED;

        if(masterFiles == null){
            masterFiles = listFolder(parameters.getProjectMasterSource()).stream()
                    .map(item -> new FeatureMasterDto(
                            getFeaturePathWithoutAbsolute(parameters.getProjectMasterSource(), item),
                            item
                    )).collect(Collectors.toList());
        }

        if(masterFiles.isEmpty()) return DiffEnum.NOT_COMPARED;

        String pathFeatureBranch = getFeaturePathWithoutAbsolute(parameters.getProjectSource(), featureBranch);

        Optional<FeatureMasterDto> optFeatureMaster = masterFiles.stream()
                .filter(item -> item.getPath().equals(pathFeatureBranch))
                .findFirst();

        DiffEnum diff = DiffEnum.NEW;
        if(optFeatureMaster.isPresent()) {
            if (FileUtils.contentEquals(featureBranch, optFeatureMaster.get().getFeature())) {
                diff = DiffEnum.EQUAL;
            } else {
                diff = DiffEnum.DIFFERENT;
            }
        }

        log.info("Diff Master/Branch: {} - {}", diff, featureBranch.getAbsolutePath());

        // pula para o proximo
        if(diff.equals(DiffEnum.EQUAL)) return diff;

        if(optFeatureMaster.isPresent()) {
            out.print(String.format(HTML_OPEN_TEMPLATE, "master_" + featureMetadataDto.getIdHtml()));
            new GherkinDocumentParser(parameters, optFeatureMaster.get().getFeature()).build(out);
            out.print(HTML_CLOSE_TEMPLATE);

            out.print(String.format(HTML_OPEN_TEMPLATE, "master_" + featureMetadataDto.getIdFeature()));
            writeFileToOut(optFeatureMaster.get().getFeature(), out);
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

        if(parameters.getProjectLogo() != null){
            String logoString = imageParser.parse(parameters, parameters.getProjectLogo()); //@TODO: transformar em buffer
            out.print(String.format("<a href=\"#/\"><img class=\"logo\" src=\"%s\"></a>", logoString));
        }else{
            out.print(String.format(
                    "<a href=\"#/\">%s <small><em>%s</em></small></a>",
                    parameters.getProjectName(),
                    parameters.getProjectVersion()
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
        out.print(String.format("<h1>%s</h1>", parameters.getProjectName()));
        out.print(String.format("<small><em>%s</em></small>", parameters.getProjectVersion()));
        out.print("</div>");
        out.print(HTML_CLOSE_TEMPLATE);
    }

    protected void buildIndex(PrintWriter out) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(indice);
        out.println(String.format("var indice = %s;", json)); //@TODO: melhorar performance
    }

    public void build() throws Exception {
        MenuParser menuParser = new MenuParser(parameters);

        final List<File> arquivos = listFolder(parameters.getProjectSource());
        if(arquivos.isEmpty()) return;

        try (
                FileOutputStream fos = new FileOutputStream(getOutArtifact(parameters));
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8));
                PrintWriter out = new PrintWriter(bw);
        ){
            out.print("<!DOCTYPE html><html lang=\"en\" data-ng-app=\"pirilampoApp\"><head>");
            out.print("<meta charset=\"utf-8\">");
            out.print("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">");
            out.print("<meta name=\"viewport\" content=\"width=device-width, shrink-to-fit=no, initial-scale=1\">");
            out.print(String.format("<title>%s</title>", parameters.getProjectName()));
            out.print("<style>");
            writeResourceToOut("htmlTemplate/dist/feature-pasta.min.css", out);
            out.print("\n\n");
            out.print(String.format(
                    "#sidebar-wrapper {background: %s;}",
                    parameters.getMenuColor()
            ));
            out.print(String.format(
                    "#menu-toggle {background: %s; color: %s;}",
                    parameters.getMenuColor(),
                    parameters.getMenuTextColor()
            ));
            out.print(String.format(
                    ".sidebar-nav li a:hover, .sidebar-nav li a { color: %s;}",
                    parameters.getMenuTextColor()
            ));
            out.print(String.format(
                    ".sidebar-nav > li {border-bottom: 1px solid %s;}",
                    parameters.getMenuTextColor()
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
                FeatureMetadataDto featureMetadataDto = getFeatureMetadata(parameters, f);

                // Processa Diff Master
                DiffEnum diff = diffMaster(featureMetadataDto, f, out);
                if(diff == DiffEnum.EQUAL) continue;

                // Gera a feture
                GherkinDocumentParser gherkinDocumentParser = new GherkinDocumentParser(parameters, f);
                out.print(String.format(HTML_OPEN_TEMPLATE, featureMetadataDto.getIdHtml()));
                gherkinDocumentParser.build(out);
                out.print(HTML_CLOSE_TEMPLATE);

                indice.put(featureMetadataDto.getId(), new FeatureIndexDto(
                        gherkinDocumentParser.getFeatureTitulo(),
                        gherkinDocumentParser.getFeatureIndexValues()
                ));

                menuParser.addMenuItem(f, diff, gherkinDocumentParser.getFeatureTitulo());

                // adiciona html embed
                for (File htmlEmbed : gherkinDocumentParser.getPaginaHtmlAnexo()){
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

        // @TODO: add done and took
    }
}
