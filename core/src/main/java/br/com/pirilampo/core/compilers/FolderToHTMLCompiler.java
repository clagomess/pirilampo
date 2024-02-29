package br.com.pirilampo.core.compilers;

import br.com.pirilampo.core.bean.Indice;
import br.com.pirilampo.core.constant.HtmlTemplate;
import br.com.pirilampo.core.dto.FeatureMetadataDto;
import br.com.pirilampo.core.dto.ParametroDto;
import br.com.pirilampo.core.enums.DiffEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class FolderToHTMLCompiler extends Compiler {
    private final ParametroDto parametro;

    public void build() throws Exception {
        ParseMenu parseMenu = new ParseMenu(parametro);
        StringBuilder htmlTemplate = new StringBuilder();
        StringBuilder htmlJavascript = new StringBuilder();
        StringBuilder htmlCss = new StringBuilder();
        List<File> paginaHtmlAnexo = new ArrayList<>();
        Map<String, Indice> indice = new HashMap<>();

        // -------- MASTER
        List<File> arquivosMaster = null;
        if(!StringUtils.isEmpty(parametro.getTxtSrcFonteMaster())) {
            // Abre pasta root
            File curDirMaster = new File(parametro.getTxtSrcFonteMaster());

            // Popula com arquivos feature
            arquivosMaster = listFolder(curDirMaster);
        }

        // -------- NORMAL
        // Abre pasta root
        File curDir = new File(parametro.getTxtSrcFonte());

        // Popula com arquivos feature
        final List<File> arquivos = listFolder(curDir);

        if(arquivos.size() > 0){
            int progressNum = 1;

            for(File f : arquivos){
                // progress
                //ProgressBind.setProgress(progressNum / (double) arquivos.size()); @TODO: check
                progressNum++;

                // monta nome menu
                FeatureMetadataDto featureMetadataDto = getFeatureMetadata(parametro, f);
                DiffEnum diff = DiffEnum.NAO_COMPARADO;

                // Processa Master
                if(!StringUtils.isEmpty(parametro.getTxtSrcFonteMaster())) {
                    diff = DiffEnum.NOVO;
                    File fmd = null;

                    if(arquivosMaster != null && !arquivosMaster.isEmpty()) {
                        for (File fm : arquivosMaster) {
                            String absoluteNFM = Resource.absoluteNameFeature(parametro.getTxtSrcFonteMaster(), fm.getAbsolutePath());
                            String absoluteNFB = Resource.absoluteNameFeature(parametro.getTxtSrcFonte(), f.getAbsolutePath());
                            String featureM = Resource.loadFeature(fm.getAbsolutePath());
                            String featureB = Resource.loadFeature(f.getAbsolutePath());

                            if (absoluteNFM.equals(absoluteNFB)) {
                                if(featureM.equals(featureB)){
                                    diff = DiffEnum.IGUAL;
                                }else{
                                    diff = DiffEnum.DIFERENTE;
                                    fmd = fm;
                                }
                                break;
                            }
                        }
                    }

                    log.info("Diff Master/Branch: {} - {}", diff, f.getAbsolutePath());

                    // pula para o proximo
                    if(diff.equals(DiffEnum.IGUAL)){
                        continue;
                    }

                    if(fmd != null) {
                        final String featureHtml = null; //@TODO ParseDocument.getFeatureHtml(parametro, fmd);

                        htmlTemplate.append(String.format(HtmlTemplate.HTML_TEMPLATE, "master_" + featureMetadataDto.getIdHtml(), featureHtml));
                        htmlTemplate.append(String.format(HtmlTemplate.HTML_TEMPLATE, "master_" + featureMetadataDto.getIdFeature(), Resource.loadFeature(fmd.getAbsolutePath())));
                    }
                }

                // Gera a feture
                ParseDocument pd = new ParseDocument(parametro, f);
                String featureHtml = null; //@TODO pd.getFeatureHtml(parametro.getTipPainel().getValue());
                paginaHtmlAnexo.addAll(pd.getPaginaHtmlAnexo());
                indice.putAll(pd.getIndice());

                htmlTemplate.append(String.format(HtmlTemplate.HTML_TEMPLATE, featureMetadataDto.getIdHtml(), featureHtml));

                // Adiciona item de menu se deu tudo certo com a master
                parseMenu.addMenuItem(f, diff, pd.getFeatureTitulo());

                // Salva as feature para diff
                if(!StringUtils.isEmpty(parametro.getTxtSrcFonteMaster())){
                    htmlTemplate.append(String.format(HtmlTemplate.HTML_TEMPLATE, featureMetadataDto.getIdFeature(), Resource.loadFeature(f.getAbsolutePath())));
                }
            }

            // adiciona html embed
            for (File htmlEmbed : paginaHtmlAnexo){
                String loadedHtmlEmbed = Resource.loadFeature(htmlEmbed.getAbsolutePath());
                htmlTemplate.append(String.format(
                        "<template type=\"text/ng-template\" id=\"%s\">%s</template>%n",
                        htmlEmbed.getName(),
                        loadedHtmlEmbed
                ));
            }

            //------------------ BUILD -----------------
            String html = Resource.loadResource("htmlTemplate/html/template_feature_pasta.html");

            // monta indice
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(indice);
            htmlJavascript.append(String.format(HtmlTemplate.HTML_JAVASCRIPT, String.format("var indice = %s;", json)));

            // adiciona resources
            htmlCss.append(String.format(HtmlTemplate.HTML_CSS, Resource.loadResource("htmlTemplate/dist/feature-pasta.min.css")));
            htmlJavascript.append(String.format(HtmlTemplate.HTML_JAVASCRIPT, Resource.loadResource("htmlTemplate/dist/feature-pasta.min.js")));
            htmlJavascript.append(String.format(HtmlTemplate.HTML_JAVASCRIPT, Resource.loadResource("htmlTemplate/dist/feature-pasta-angular.min.js")));

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
                String logoString = ParseImage.parse(parametro, new File(parametro.getTxtLogoSrc()));
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
            String outDir = (StringUtils.isNotEmpty(parametro.getTxtOutputTarget()) ? parametro.getTxtOutputTarget() : curDir.getParent() + File.separator + "html");
            File outDirF = new File(outDir);

            if(!outDirF.exists()){
                outDirF.mkdir();
            }

            Resource.writeHtml(html, outDir + File.separator + "index.html");
        }
    }
}
