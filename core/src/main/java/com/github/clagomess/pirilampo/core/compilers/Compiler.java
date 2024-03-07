package com.github.clagomess.pirilampo.core.compilers;

import com.github.clagomess.pirilampo.core.dto.FeatureMetadataDto;
import com.github.clagomess.pirilampo.core.dto.ParametersDto;
import org.apache.commons.io.input.BOMInputStream;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.clagomess.pirilampo.core.enums.CompilationArtifactEnum.HTML;
import static com.github.clagomess.pirilampo.core.enums.CompilationTypeEnum.FOLDER;
import static com.github.clagomess.pirilampo.core.enums.CompilationTypeEnum.FOLDER_DIFF;

public abstract class Compiler {
    public String getFeatureExtension(File f){
        Matcher matcher = Pattern.compile("\\.feature$", Pattern.CASE_INSENSITIVE)
                .matcher(f.getName());

        return matcher.find() ? matcher.group(0) : null;
    }

    protected Set<File> listFolder(File curDir) throws Exception {
        Set<File> buffer = new TreeSet<>();

        listFolder(buffer, curDir);

        return buffer;
    }

    private void listFolder(Set<File> buffer, File curDir) throws Exception {
        File[] filesList = curDir.listFiles();
        if(filesList == null) throw new Exception("*.features not found");

        for (File f : filesList) {
            if (f.isDirectory()) listFolder(buffer, f);

            if (f.isFile() && ".feature".equalsIgnoreCase(getFeatureExtension(f))) {
                buffer.add(f);
            }
        }
    }

    protected FeatureMetadataDto getFeatureMetadata(ParametersDto parameters, File feature){
        String htmlFeatureRoot = feature.getAbsolutePath()
                .replace(parameters.getProjectSource().getAbsolutePath(), "");

        if(parameters.getProjectMasterSource() != null){
            htmlFeatureRoot = htmlFeatureRoot.replace(parameters.getProjectMasterSource().getAbsolutePath(), "");
        }

        htmlFeatureRoot = htmlFeatureRoot.replace(feature.getName(), "")
                .replace(File.separator, " ")
                .trim();

        FeatureMetadataDto result = new FeatureMetadataDto();
        result.setName(feature.getName().replace(getFeatureExtension(feature), ""));
        result.setId(htmlFeatureRoot + "_" + result.getName());
        result.setIdHtml(result.getId() + ".html");
        result.setIdFeature(result.getId() + ".feature");

        return result;
    }

    protected File getOutArtifact(ParametersDto parameters){
        if(Arrays.asList(FOLDER, FOLDER_DIFF).contains(parameters.getCompilationType())){
            File targetDir = parameters.getProjectTarget() != null ?
                    new File(parameters.getProjectTarget(), "html") :
                    new File(parameters.getProjectSource().getParent(), "html");

            if(!targetDir.exists() && !targetDir.mkdir()){
                throw new RuntimeException(String.format("Failed to create dir: %s", targetDir.getAbsolutePath()));
            }

            return new File(targetDir, parameters.getCompilationArtifact() == HTML ? "index.html" : "index.pdf");
        }else{
            String filename = String.format(
                    "%s.%s",
                    getFeatureMetadata(parameters, parameters.getProjectSource()).getName(),
                    parameters.getCompilationArtifact() == HTML ? "html" : "pdf"
            );

            File targetDir = parameters.getProjectTarget() != null ?
                    parameters.getProjectTarget() :
                    new File(parameters.getProjectSource().getParent());

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

    public String getFeaturePathWithoutAbsolute(File base, File feature){
        String resultDiff = feature.getAbsolutePath().replace(base.getAbsolutePath() + File.separator, "");
        return resultDiff.replace(File.separator, "/");
    }

    public File getAbsolutePathFeatureAsset(ParametersDto parameters, File feature, String fileName){
        Set<File> basePaths = new LinkedHashSet<File>() {{
            add(feature.getParentFile());
            add(parameters.getProjectSource());
            if(parameters.getProjectMasterSource() != null) add(parameters.getProjectMasterSource());
        }};

        for (File basePath : basePaths) {
            File asset = new File(basePath, fileName);
            if(asset.isFile()) return asset;
        }

        return null;
    }
}
