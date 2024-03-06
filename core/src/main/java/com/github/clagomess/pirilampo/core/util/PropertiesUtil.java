package com.github.clagomess.pirilampo.core.util;


import com.github.clagomess.pirilampo.core.dto.ParametersDto;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Properties;

@Slf4j
public class PropertiesUtil {
    private static final String FILENAME = "/../html/config.properties";

    public static ParametersDto getData(String sourcePath){
        Properties prop = new Properties();
        InputStream input = null;

        if((new File(sourcePath + FILENAME)).isFile()) {
            try {
                input = new FileInputStream(sourcePath + FILENAME);

                prop.load(input);
            } catch (IOException ex) {
                log.warn(log.getName(), ex);
            } finally {
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        log.warn(log.getName(), e);
                    }
                }
            }
        }

        return new ParametersDto(prop);
    }

    public static void setData(ParametersDto parameters){
        File file = new File(parameters.getProjectSource() + FILENAME);

        if(!file.isFile()){
            try {
                if(file.createNewFile()){
                    log.info("Arquivo de configuração criado em: {}", file.getAbsolutePath());
                }
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }

        try (OutputStream output = new FileOutputStream(parameters.getProjectSource() + FILENAME)){
            Properties prop = parametroToProperties(parameters);
            prop.store(output, null);
        } catch (IOException ex) {
            log.error(log.getName(), ex);
        }
    }

    public static Properties parametroToProperties(ParametersDto parameters){
        Properties prop = new Properties();
        /* @TODO: check
        prop.setProperty("txtNome", parameters.getTxtNome());
        prop.setProperty("txtVersao", parameters.getTxtVersao());
        prop.setProperty("txtLogoSrc", parameters.getTxtLogoSrc() != null ? parameters.getTxtLogoSrc() : "");
        prop.setProperty("clrMenu", parameters.getClrMenu());
        prop.setProperty("clrTextoMenu", parameters.getClrTextoMenu());
        prop.setProperty("sitEmbedarImagens", parameters.getSitEmbedarImagens().toString());
        prop.setProperty("tipPainelFechado", parameters.getTipPainel().toString());
        */
        return prop;
    }
}
