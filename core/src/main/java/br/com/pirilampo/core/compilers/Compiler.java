package br.com.pirilampo.core.compilers;

import br.com.pirilampo.core.dto.FeatureMetadataDto;
import br.com.pirilampo.core.dto.ParametroDto;
import org.apache.commons.io.input.BOMInputStream;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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
        String htmlFeatureRoot = feature.getAbsolutePath()
                .replace(parametro.getTxtSrcFonte().getAbsolutePath(), "")
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

    protected void writeResourceToOut(String resource, PrintWriter out) throws IOException {
        URL url = Thread.currentThread().getContextClassLoader().getResource(resource);
        if(url == null){
            throw new FileNotFoundException(String.format(
                    "Resource %s not loaded",
                    resource
            ));
        }

        try(
                FileReader fr = new FileReader(url.getFile());
                BufferedReader br = new BufferedReader(fr)
        ){
            int value;
            while ((value = br.read()) != -1) {
                out.print((char) value);
            }
        }
    }

    protected void writeFileToOut(String filename, PrintWriter out) throws IOException {
        try (
                FileInputStream fis = new FileInputStream(filename);
                BOMInputStream bis = new BOMInputStream(fis);
                BufferedReader br = new BufferedReader(new InputStreamReader(bis, StandardCharsets.UTF_8));
        ){
            int value;
            while ((value = br.read()) != -1) {
                out.print((char) value);
            }
        }
    }
}
