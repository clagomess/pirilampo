package com.github.clagomess.pirilampo.core.parsers;

import com.github.clagomess.pirilampo.core.compilers.Compiler;
import com.github.clagomess.pirilampo.core.dto.ParametersDto;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.Base64;

@Slf4j
public class ImageParser extends Compiler {
    public void parse(PrintWriter out, ParametersDto parameters, File feature, String fileName){
        File file = getAbsolutePathFile(parameters, feature, fileName);

        if(file != null){
            parse(out, parameters, file);
        }else{
            out.print(fileName);
        }
    }

    protected void parse(PrintWriter out, ParametersDto parameters, File image){
        if(!parameters.isEmbedImages()){
            out.print("../");
            out.print(getFilePathWithoutAbsolute(parameters.getProjectSource().getParentFile(), image));
            return;
        }

        try(
                InputStream is = Files.newInputStream(image.toPath());
                BufferedInputStream bis = new BufferedInputStream(is);
        ){
            byte[] buffer = new byte[1024 * 4];
            int n;

            out.print("data:");
            out.print(URLConnection.guessContentTypeFromName(image.getName()));
            out.print(";base64,");

            while ((n = bis.read(buffer)) != -1) {
                byte[] result = new byte[n];
                System.arraycopy(buffer, 0, result, 0, n);

                String base64 = Base64.getEncoder().encodeToString(result);
                out.print(base64);
            }
        } catch (IOException e) {
            log.warn(e.getMessage() + " - " + image.getAbsolutePath());
            out.print(image.getName());
        }
    }
}
