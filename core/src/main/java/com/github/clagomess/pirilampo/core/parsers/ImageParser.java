package com.github.clagomess.pirilampo.core.parsers;

import com.github.clagomess.pirilampo.core.compilers.Compiler;
import com.github.clagomess.pirilampo.core.dto.ParametersDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.util.Base64;

@Slf4j
public class ImageParser extends Compiler {
    public String parse(ParametersDto parameters, File feature, String fileName){
        File file = getAbsolutePathFeatureAsset(parameters, feature, fileName);

        if(file != null){
            return parse(parameters, file);
        }else{
            return fileName;
        }
    }

    public String parse(ParametersDto parameters, File image){
        if(parameters.isEmbedImages() || image.equals(parameters.getProjectLogo())) {
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
            return "../" + getFeaturePathWithoutAbsolute(parameters.getProjectSource().getParentFile(), image);
        }

        return image.getName();
    }
}
