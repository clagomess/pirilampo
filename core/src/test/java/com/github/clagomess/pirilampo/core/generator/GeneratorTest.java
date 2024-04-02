package com.github.clagomess.pirilampo.core.generator;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class GeneratorTest {
    private final File path = new File("target/GeneratorTest");
    private final Generator generator = new Generator(path);

    public void build(){
        generator.build();
    }

    @BeforeEach
    public void setup(){
        if(!path.isDirectory()){
            assertTrue(path.mkdir());
        }else{
            Arrays.stream(path.listFiles()).forEach(File::delete);
        }
    }

    @Test
    public void getPath(){
        log.info("{}", generator.getPath());
    }

    @Test
    public void genWords(){
        log.info("{}", generator.genWords(45));
    }

    @Test
    public void genFeature(){
        StringWriter sw = new StringWriter();
        try (
                PrintWriter out = new PrintWriter(sw);
        ){
            generator.genFeature(out, path);
            log.info("{}", sw.toString());
        }
    }

    @Test
    public void genCenario(){
        StringWriter sw = new StringWriter();
        try (
                PrintWriter out = new PrintWriter(sw);
        ){
            generator.genCenario(out, path);
            log.info("{}", sw.toString());
        }
    }

    @Test
    public void genImageURL(){
        log.info("{}", generator.genImageURL());
    }

    @Test
    public void genImage() {
        generator.genImage(path);
    }

    @Test
    public void genHTML(){
        generator.genHTML(path);
    }
}
