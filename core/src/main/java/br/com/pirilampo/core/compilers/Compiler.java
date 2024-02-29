package br.com.pirilampo.core.compilers;

import br.com.pirilampo.core.dto.FeatureMetadataDto;
import br.com.pirilampo.core.dto.ParametroDto;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public abstract class Compiler {
    protected List<File> listFolder(File curDir) throws Exception {
        List<File> buffer = new LinkedList<>();

        listFolder(buffer, curDir);

        return buffer;
    }

    protected void listFolder(List<File> buffer, File curDir) throws Exception {
        File[] filesList = curDir.listFiles();
        if(filesList == null) throw new Exception("Pasta não localizada!");

        for (File f : filesList) {
            if (f.isDirectory()) listFolder(buffer, f);

            if (f.isFile() && ".feature".equalsIgnoreCase(Resource.getExtension(f))) {
                buffer.add(f);
            }
        }
    }

    protected FeatureMetadataDto getFeatureMetadata(ParametroDto parametro, File feature){
        File curDir = new File(parametro.getTxtSrcFonte());

        String htmlFeatureRoot = feature.getAbsolutePath()
                .replace(curDir.getAbsolutePath(), "")
                .replace(feature.getName(), "")
                .replace(File.separator, " ")
                .trim();

        FeatureMetadataDto result = new FeatureMetadataDto();
        result.setName(feature.getName().replace(Resource.getExtension(feature), ""));
        result.setId(htmlFeatureRoot + "_" + result.getName());
        result.setIdHtml(result.getId() + ".html");
        result.setIdFeature(result.getId() + ".feature");

        return result;
    }
}
