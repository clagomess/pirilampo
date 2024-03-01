package br.com.pirilampo.core.compilers;

import br.com.pirilampo.core.dto.ParametroDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class Resource {
    /** @deprecated @TODO: to be removed **/
    public static void writeHtml(String html, String path) throws IOException {

    }

    /** @deprecated @TODO: to be removed **/
    public static String loadResource(String src) throws IOException {
        return null;
    }

    /** @deprecated @TODO: to be removed **/
    public static String loadFeature(String pathFeature){
        return null;
    }

    public static String absoluteNameFeature(String path, String absolutePath){
        path = path.replaceAll("\\\\", "");
        absolutePath = absolutePath.replaceAll("\\\\", "");

        path = path.replaceAll("\\/", "");
        absolutePath = absolutePath.replaceAll("\\/", "");

        return absolutePath.replace(path, "");
    }

    public static String getExtension(File f){
        String ext = "";
        if(f != null && f.isFile()) {
            Pattern p = Pattern.compile("\\.[a-zA-Z]+$");
            Matcher m = p.matcher(f.getName());

            if (m.find()) {
                ext = m.group(0);
            }
        }

        return ext;
    }

    public static File absolute(ParametroDto parametro, File feature, String fileName){
        File toReturn = null;
        List<String> paths = new ArrayList<>();
        paths.add(feature.getAbsolutePath().replace(feature.getName(), ""));
        paths.add(parametro.getTxtSrcFonte().getAbsolutePath());

        if(parametro.getTxtSrcFonteMaster() != null){
            paths.add(parametro.getTxtSrcFonteMaster().getAbsolutePath());
        }

        for (String path : paths) {
            File file = new File( path + File.separator + fileName);
            if(file.isFile()){
                toReturn = file;
                break;
            }
        }

        return toReturn;
    }
}
