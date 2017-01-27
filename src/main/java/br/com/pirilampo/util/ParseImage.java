package br.com.pirilampo.util;


import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.util.Base64;
import java.util.List;

class ParseImage {
    private static final Logger logger = LoggerFactory.getLogger(Compilador.class);

    static String parse(String fileName, List<String> paths){
        String toReturn = fileName;

        for (String path : paths) {
            File image = new File( path + File.separator + fileName);


            if(image.isFile()){
                try {
                    byte[] base64 = Base64.getEncoder().encode(FileUtils.readFileToByteArray(image));

                    if(base64.length > 0){
                        String mimeType = URLConnection.guessContentTypeFromName(image.getName());

                        toReturn = "data:" + mimeType + ";base64," + new String(base64);
                    }

                    break;
                } catch (IOException e) {
                    logger.info(e.getMessage() + " - " + image.getAbsolutePath());
                }
            }
        }

        return toReturn;
    }
}
