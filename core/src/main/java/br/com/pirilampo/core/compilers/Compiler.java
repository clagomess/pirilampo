package br.com.pirilampo.core.compilers;

import br.com.pirilampo.core.dto.FeatureMetadataDto;
import br.com.pirilampo.core.dto.ParametroDto;
import br.com.pirilampo.core.enums.ArtefatoEnum;
import br.com.pirilampo.core.enums.CompilacaoEnum;
import org.apache.commons.io.input.BOMInputStream;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Compiler {
    public String getFeatureExtension(File f){
        Matcher matcher = Pattern.compile("\\.feature$", Pattern.CASE_INSENSITIVE)
                .matcher(f.getName());

        return matcher.find() ? matcher.group(0) : null;
    }

    protected List<File> listFolder(File curDir) throws Exception {
        List<File> buffer = new LinkedList<>();

        listFolder(buffer, curDir);

        return buffer;
    }

    private void listFolder(List<File> buffer, File curDir) throws Exception {
        File[] filesList = curDir.listFiles();
        if(filesList == null) throw new Exception("Pasta não localizada!");

        for (File f : filesList) {
            if (f.isDirectory()) listFolder(buffer, f);

            if (f.isFile() && ".feature".equalsIgnoreCase(getFeatureExtension(f))) {
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
        result.setName(feature.getName().replace(getFeatureExtension(feature), ""));
        result.setId(htmlFeatureRoot + "_" + result.getName());
        result.setIdHtml(result.getId() + ".html");
        result.setIdFeature(result.getId() + ".feature");

        return result;
    }

    protected File getOutArtifact(ParametroDto parametro){
        if(parametro.getTipCompilacao() == CompilacaoEnum.PASTA){
            File targetDir = parametro.getTxtOutputTarget() != null ?
                    new File(parametro.getTxtOutputTarget(), "html") :
                    new File(parametro.getTxtSrcFonte().getParent(), "html");

            if(!targetDir.exists() && !targetDir.mkdir()){
                throw new RuntimeException(String.format("Failed to create dir: %s", targetDir.getAbsolutePath()));
            }

            return new File(targetDir, parametro.getArtefato() == ArtefatoEnum.HTML ? "index.html" : "index.pdf");
        }else{
            String filename = String.format(
                    "%s.%s",
                    getFeatureMetadata(parametro, parametro.getTxtSrcFonte()).getName(),
                    parametro.getArtefato() == ArtefatoEnum.HTML ? "html" : "pdf"
            );

            File targetDir = parametro.getTxtOutputTarget() != null ?
                    parametro.getTxtOutputTarget() :
                    new File(parametro.getTxtSrcFonte().getParent());

            return new File(targetDir, filename);
        }
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

    protected void writeFileToOut(File file, PrintWriter out) throws IOException {
        try (
                FileInputStream fis = new FileInputStream(file);
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
