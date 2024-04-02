package com.github.clagomess.pirilampo.cli;

import com.ginsberg.junit.exit.ExpectSystemExitWithStatus;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class MainOptionsTest {
    private final MainOptions mainOptions = new MainOptions();

    @Test
    @ExpectSystemExitWithStatus(1)
    public void getArgs(){
        mainOptions.getArgs(new String[]{"-projectLogo", "aaa"});
    }

    @Test
    @ExpectSystemExitWithStatus(1)
    public void getArgs_trigger_validate(){
        mainOptions.getArgs(new String[]{"-projectSource", "aaa"});
    }
}
