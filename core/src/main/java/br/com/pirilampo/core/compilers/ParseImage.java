package br.com.pirilampo.core.compilers;


import br.com.pirilampo.core.dto.ParametroDto;
import br.com.pirilampo.core.enums.ArtefatoEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.util.Base64;

@Slf4j
class ParseImage extends Compiler {
    public String parse(ParametroDto parametro, File feature, String fileName){
        File file = getAbsolutePathFeatureAsset(parametro, feature, fileName);

        if(file != null){
            return parse(parametro, file);
        }else{
            return fileName;
        }
    }

    public String parse(ParametroDto parametro, File image){
        if(
                parametro.getSitEmbedarImagens() ||
                image.equals(parametro.getTxtLogoSrc()) ||
                parametro.getArtefato() == ArtefatoEnum.PDF
        ) {
            try {
                String base64 = Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(image));

                if (StringUtils.isNotEmpty(base64)) {
                    String mimeType = URLConnection.guessContentTypeFromName(image.getName());
                    return "data:" + mimeType + ";base64," + base64;
                }
            } catch (IOException e) {
                log.info(e.getMessage() + " - " + image.getAbsolutePath());
            }
        } else {
            return "../" + getFeaturePathWithoutAbsolute(parametro.getTxtSrcFonte().getParentFile(), image);
        }

        return image.getName();
    }
}
