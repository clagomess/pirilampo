package br.com.pirilampo.util;


import br.com.pirilampo.bean.Parametro;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Properties;

@Slf4j
public class PropertiesUtil {
    private static final String FILENAME = "/../html/config.properties";

    public static Parametro getData(String sourcePath){
        Properties prop = new Properties();
        InputStream input = null;

        if((new File(sourcePath + FILENAME)).isFile()) {
            try {
                input = new FileInputStream(sourcePath + FILENAME);

                prop.load(input);
            } catch (IOException ex) {
                log.warn(PropertiesUtil.class.getName(), ex);
            } finally {
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        log.warn(PropertiesUtil.class.getName(), e);
                    }
                }
            }
        }

        return new Parametro(prop);
    }

    public static void setData(Parametro parametro){
        File file = new File(parametro.getTxtSrcFonte() + FILENAME);

        if(!file.isFile()){
            try {
                if(file.createNewFile()){
                    log.info("Arquivo de configuração criado em: {}", file.getAbsolutePath());
                }
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }

        try (OutputStream output = new FileOutputStream(parametro.getTxtSrcFonte() + FILENAME)){
            Properties prop = parametroToProperties(parametro);
            prop.store(output, null);
        } catch (IOException ex) {
            log.error(PropertiesUtil.class.getName(), ex);
        }
    }

    public static Properties parametroToProperties(Parametro parametro){
        Properties prop = new Properties();
        prop.setProperty("txtNome", parametro.getTxtNome());
        prop.setProperty("txtVersao", parametro.getTxtVersao());
        prop.setProperty("txtLogoSrc", parametro.getTxtLogoSrc() != null ? parametro.getTxtLogoSrc() : "");
        prop.setProperty("clrMenu", parametro.getClrMenu());
        prop.setProperty("clrTextoMenu", parametro.getClrTextoMenu());
        prop.setProperty("txtNomeMenuRaiz", parametro.getTxtNomeMenuRaiz());
        prop.setProperty("sitEmbedarImagens", parametro.getSitEmbedarImagens().toString());

        return prop;
    }
}
