package br.com.pirilampo.core.compilers;

import br.com.pirilampo.core.dto.ParametroDto;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Resource {
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
