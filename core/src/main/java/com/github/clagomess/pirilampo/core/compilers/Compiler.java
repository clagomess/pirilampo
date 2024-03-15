package com.github.clagomess.pirilampo.core.compilers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.clagomess.pirilampo.core.dto.FeatureMetadataDto;
import com.github.clagomess.pirilampo.core.dto.ParametersDto;
import com.github.clagomess.pirilampo.core.fi.UIProgressFI;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.input.BOMInputStream;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.clagomess.pirilampo.core.enums.CompilationArtifactEnum.HTML;
import static com.github.clagomess.pirilampo.core.enums.CompilationTypeEnum.FOLDER;
import static com.github.clagomess.pirilampo.core.enums.CompilationTypeEnum.FOLDER_DIFF;

@Slf4j
public abstract class Compiler {
    @Setter
    protected UIProgressFI progress = (value) -> {};

    @Setter
    protected float progressCount = 0;

    protected final ObjectMapper mapper = new ObjectMapper() {{
        disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
    }};

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
        InputStream is = Compiler.class.getResourceAsStream(resource);
        if(is == null){
            throw new FileNotFoundException(String.format(
                    "Resource %s not loaded",
                    resource
            ));
        }

        try(
                InputStreamReader isr = new InputStreamReader(new BOMInputStream(is), StandardCharsets.UTF_8);
                BufferedReader br = new BufferedReader(isr);
        ){
            char[] buffer = new char[1024 * 4];
            int n;

            while ((n = br.read(buffer)) != -1) {
                out.write(buffer, 0, n);
            }
        }
    }

    protected void writeFileToOut(File file, PrintWriter out) throws IOException {
        try (
                FileInputStream fis = new FileInputStream(file);
                InputStreamReader isr = new InputStreamReader(new BOMInputStream(fis), StandardCharsets.UTF_8);
                BufferedReader br = new BufferedReader(isr);
        ){
            char[] buffer = new char[1024 * 4];
            int n;

            while ((n = br.read(buffer)) != -1) {
                out.write(buffer, 0, n);
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

    private long initTimer;
    protected void startTimer(){
        initTimer = Instant.now().toEpochMilli();
    }

    protected void stopTimer(){
        log.info("Compilation time: {}ms", Instant.now().toEpochMilli() - initTimer);
    }

    @Getter
    private final List<File> tempFiles = new LinkedList<>();
    protected File createTempFile() throws IOException {
        File bufferHtml = File.createTempFile("pirilampo-buffer-", ".tmp");
        tempFiles.add(bufferHtml);
        log.info("Created buffer file: {}", bufferHtml);
        return bufferHtml;
    }

    protected void deleteAllTempFiles(){
        tempFiles.forEach(File::delete);
    }
}
