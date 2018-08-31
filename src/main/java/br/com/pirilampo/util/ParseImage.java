package br.com.pirilampo.util;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Slf4j
class ParseImage {
    static String parse(String fileName, String path){
        List<String> paths = new ArrayList<>();
        paths.add(path);
        return ParseImage.parse(fileName, paths);
    }

    static String parse(File file){
        return ParseImage.parse(file.getName(), file.getAbsolutePath().replace(file.getName(), ""));
    }

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
                    log.info(e.getMessage() + " - " + image.getAbsolutePath());
                    log.warn(ParseImage.class.getName(), e);
                }
            }
        }

        return toReturn;
    }
}
