package com.github.clagomess.gherkinfeaturegenerator;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

@Slf4j
public class GeneratorTest {
    private final Generator generator = new Generator();
    private final File path = new File("target");

    @Test
    public void build(){
        generator.build();
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
    public void downloadImage() {
        generator.downloadImage(path);
    }

    @Test
    public void genHTML(){
        generator.genHTML(path);
    }
}
