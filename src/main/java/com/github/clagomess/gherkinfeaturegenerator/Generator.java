package com.github.clagomess.gherkinfeaturegenerator;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
public class Generator {
    public File root = new File("target/features");
    public List<File> paths = new LinkedList<>();
    public List<File> features = new LinkedList<>();
    public int qtdFeatures = 100;

    public void build(){
        IntStream.rangeClosed(0, 5).parallel().forEach(i -> {
            File featurePath = getPath();
            File feature = new File(featurePath, String.format("%s.feature", RandomStringUtils.randomAlphabetic(10)));

            try (
                    FileOutputStream fos = new FileOutputStream(feature);
                    PrintWriter out = new PrintWriter(fos);
            ) {
                log.info("GEN {}", feature);
                genFeature(out, featurePath);
            }catch (Throwable e){
                log.error(e.getMessage());
            }
        });
    }

    public boolean randomBoolean(){
        return Math.floor(Math.random() * 2) == 1.0;
    }

    public File getPath(){
        if(!root.exists()) root.mkdir();

        File path;
        boolean createANewOnePath = randomBoolean();
        if(paths.isEmpty() || createANewOnePath){
            path = new File(root, RandomStringUtils.randomAlphabetic(10));
        } else {
            int chosed = (int) Math.floor(Math.random() * paths.size());
            path = paths.get(chosed);
        }

        boolean createASubPath = randomBoolean();
        if(createASubPath){
            path = new File(path, RandomStringUtils.randomAlphabetic(10));
        }

        if(!path.exists()) path.mkdirs();

        paths.add(path);
        return path;
    }

    public String genWords(int qtd){
        List<String> list = new LinkedList<>();

        IntStream.rangeClosed(1, qtd).forEach(i -> {
            int qtdLeters = (int) Math.ceil(Math.random() * 10);
            if(qtdLeters == 0) qtdLeters = 1;

            String word = RandomStringUtils.randomAlphabetic(qtdLeters);
            list.add(word);
        });

        return String.join(" ", list);
    }

    public void genFeature(PrintWriter out, File path){
        out.println("# language: pt");
        out.println("# encoding: utf-8");
        out.println("Funcionalidade: " + genWords((int) Math.ceil(Math.random() * 5)));
        out.println("  " + genWords((int) Math.ceil(Math.random() * 45)));
        out.println("");
        out.println("  Contexto:");
        out.println("    Dado " + genWords((int) Math.ceil(Math.random() * 45)));
        out.println("");

        int qtdCenario = (int) Math.ceil(Math.random() * 15);
        IntStream.rangeClosed(1, qtdCenario).forEach(i -> genCenario(out, path));
    }

    public List<String> imageTemplate = Arrays.asList(
        "![Image](%s)",
        "<img src=\"%s\">",
        "<img src=\"%s\" width=\"100\">"
    );

    public void genCenario(PrintWriter out, File path){
        out.println("  Cenário: " + genWords((int) Math.ceil(Math.random() * 5)));
        out.println("    Dado " + genWords((int) Math.ceil(Math.random() * 45)));
        out.println("    Então " + genWords((int) Math.ceil(Math.random() * 45)));
        out.println("    E " + genWords((int) Math.ceil(Math.random() * 45)));

        if(randomBoolean()){
            String filename = downloadImage(path);
            if(filename != null) {
                int template = (int) Math.floor(Math.random() * 3);

                out.println("    E " + genWords((int) Math.ceil(Math.random() * 45)));
                out.println(String.format("      | %s |", genWords((int) Math.ceil(Math.random() * 5))));
                out.println(String.format(
                        "      | %s |",
                        String.format(imageTemplate.get(template), filename)
                ));
            }
        }

        if(randomBoolean()){
            String filename = genImageURL();
            out.println("    E " + genWords((int) Math.ceil(Math.random() * 45)));
            out.println(String.format("      | %s |", genWords((int) Math.ceil(Math.random() * 5))));
            out.println(String.format("      | ![Image](%s) |", filename));
        }

        if(randomBoolean()){
            out.println(String.format("    E <strike>%s</strike>", genWords((int) Math.ceil(Math.random() * 10))));
        }

        if(randomBoolean()){
            out.println("    E Link Google: [Google](https://www.google.com.br)");
        }

        if(randomBoolean()){
            out.println("    E " + genWords((int) Math.ceil(Math.random() * 45)));
            out.println("    \"\"\"");
            out.println("    " + genWords((int) Math.ceil(Math.random() * 45)));
            out.println("    \"\"\"");
        }

        if(randomBoolean()){
            String filename = genHTML(path);
            if(filename != null) {
                out.println("    E " + genWords((int) Math.ceil(Math.random() * 45)));
                out.println(String.format("      | %s |", genWords((int) Math.ceil(Math.random() * 5))));
                out.println(String.format(
                        "      | Link Html Embeded: [Link Embeded](%s) |",
                        filename
                ));
            }
        }

        if(randomBoolean() && !features.isEmpty()){
            int selected = (int) Math.floor(Math.random() * features.size());
            File feature = features.get(selected);

            String id = feature.getParent()
                    .replace(root.getPath(), "")
                    .replace(File.separator, "_")
                    + "-" + feature.getName().replace(".feature", "")
                    ;


            out.println(String.format(
                    "    E %s [DEF003](#/scenario/%s/1)",
                    genWords((int) Math.ceil(Math.random() * 10)),
                    id
            ));
        }

        out.println("");
    }

    public String genImageURL(){
        int width = (int) Math.ceil(Math.random() * 10) * 100;
        int height = (int) Math.ceil(Math.random() * 10) * 100;

        return String.format(
                "https://picsum.photos/%s/%s",
                width,
                height
        );
    }

    public String downloadImage(File path) {
        try {
            URL url = new URL(genImageURL());

            String filename = RandomStringUtils.randomAlphabetic(10) + ".jpg";
            File file = new File(path, filename);

            try (
                    InputStream in = new BufferedInputStream(url.openStream());
                    FileOutputStream fos = new FileOutputStream(file);
            ) {
                int value;
                while ((value = in.read()) != -1) {
                    fos.write(value);
                }
            }

            return filename;
        }catch (Throwable e){
            log.error(e.getMessage());
            return null;
        }
    }

    public String genHTML(File path){
        String filename = RandomStringUtils.randomAlphabetic(10) + ".html";
        File file = new File(path, filename);
        int interations = (int) Math.ceil(Math.random() * 200);

        try (
                FileOutputStream fos = new FileOutputStream(file);
                PrintWriter out = new PrintWriter(fos);
        ) {
            IntStream.rangeClosed(1, interations).forEach(i -> {
                out.println(String.format("<h1>%s</h1>", genWords((int) Math.ceil(Math.random() * 45))));
                out.println(String.format("<p>%s</p>", genWords((int) Math.ceil(Math.random() * 45))));
            });

            return filename;
        }catch (Throwable e){
            log.error(e.getMessage());
            return null;
        }
    }
}
