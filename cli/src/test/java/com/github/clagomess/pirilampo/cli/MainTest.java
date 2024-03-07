package com.github.clagomess.pirilampo.cli;

import com.ginsberg.junit.exit.ExpectSystemExitWithStatus;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.LinkedList;
import java.util.List;

@Slf4j
public class MainTest {
    @Test
    @ExpectSystemExitWithStatus(1)
    public void main_options_validate(){
        Main.main(new String[]{"-projectSource", "aaa"});
    }

    @ParameterizedTest
    @CsvSource(value = {
        "FEATURE,HTML,feature/xxx.Feature,",
        "FEATURE,PDF,feature/xxx.Feature,",
        "FOLDER,HTML,feature,",
        "FOLDER_DIFF,HTML,feature,master",
        "FOLDER,PDF,feature,",
    })
    @ExpectSystemExitWithStatus(0)
    public void main_ok(
            String compilationType,
            String compilationArtifact,
            String projectSource,
            String projectMasterSource
    ){
        List<String> argv = new LinkedList<>();
        argv.add("-projectSource");
        argv.add(getClass().getResource(projectSource).getFile());

        if(projectMasterSource != null) {
            argv.add("-projectMasterSource");
            argv.add(getClass().getResource(projectMasterSource).getFile());
        }

        argv.add("-compilationType");
        argv.add(compilationType);

        argv.add("-compilationArtifact");
        argv.add(compilationArtifact);

        Main.main(argv.toArray(new String[0]));
    }
}
