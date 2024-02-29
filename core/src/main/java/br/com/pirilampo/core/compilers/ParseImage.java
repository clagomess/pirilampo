package br.com.pirilampo.core.compilers;


import br.com.pirilampo.core.dto.ParametroDto;
import br.com.pirilampo.core.enums.ArtefatoEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.util.Base64;

@Slf4j
class ParseImage {
    static String parse(ParametroDto parametro, File feature, String fileName){
        String toReturn = fileName;
        File file = Resource.absolute(parametro, feature, fileName);

        if(file != null){
            toReturn = parse(parametro, file);
        }

        return toReturn;
    }

    static String parse(ParametroDto parametro, File image){
        String toReturn = image.getName();

        if(parametro.getSitEmbedarImagens() || image.getAbsolutePath().equals(parametro.getTxtLogoSrc()) || parametro.getArtefato() == ArtefatoEnum.PDF) {
            try {
                byte[] base64 = Base64.getEncoder().encode(FileUtils.readFileToByteArray(image));

                if (base64.length > 0) {
                    String mimeType = URLConnection.guessContentTypeFromName(image.getName());

                    toReturn = "data:" + mimeType + ";base64," + new String(base64);
                }
            } catch (IOException e) {
                log.info(e.getMessage() + " - " + image.getAbsolutePath());
                log.warn(log.getName(), e);
            }
        } else {
            toReturn = image.getAbsolutePath().replace((new File(parametro.getTxtSrcFonte())).getParent(), "");
            toReturn = toReturn.replaceFirst("^[\\/|\\\\]", "");
            toReturn = toReturn.replaceAll("\\\\", "/");
            toReturn = "../" + toReturn;
        }

        return toReturn;
    }
}
