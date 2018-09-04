package br.com.pirilampo.core;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.input.BOMInputStream;

import java.io.*;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class Resource {
    public static void writeHtml(String html, String path) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(path)){
            OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            Writer out = new BufferedWriter(osw);
            out.write(html);
            out.flush();
        }catch (Exception e){
            throw e;
        }
    }

    public static String loadResource(String src) throws IOException {
        StringBuilder buffer = new StringBuilder();
        String linha;

        URL url = Thread.currentThread().getContextClassLoader().getResource(src);

        if(url != null) {
            BufferedReader br;

            try (FileReader fr = new FileReader(url.getFile())) {
                br = new BufferedReader(fr, 200 * 1024);

                while ((linha = br.readLine()) != null) {
                    buffer.append(linha).append("\n");
                }
            } catch (Exception e) {
                try(InputStreamReader isr = new InputStreamReader(url.openStream())){
                    br = new BufferedReader(isr, 200 * 1024);

                    while ((linha = br.readLine()) != null) {
                        buffer.append(linha).append("\n");
                    }
                } catch(Exception ea){
                    log.warn(Compilador.class.getName(), e);
                    log.warn(Compilador.class.getName(), ea);
                }
            }
        } else {
            log.warn("Falha ao carregar Resource");
        }

        return buffer.toString();
    }

    public static String loadFeature(String pathFeature){
        StringBuilder buffer = new StringBuilder();
        String toReturn = "";
        String linha;
        BufferedReader br;

        try (FileInputStream fis = new FileInputStream(pathFeature)){
            BOMInputStream bis = new BOMInputStream(fis);

            br = new BufferedReader(new InputStreamReader(bis, StandardCharsets.UTF_8));

            while ((linha = br.readLine()) != null) {
                buffer.append(linha).append("\n");
            }

            toReturn = buffer.toString().replaceAll("\\t", "   ");
            toReturn = toReturn.trim();
        }catch (Exception e){
            log.warn(Compilador.class.getName(), e);
        }

        return toReturn;
    }

    public static String absoluteNameFeature(String path, String absolutePath){
        path = path.replaceAll("\\\\", "");
        absolutePath = absolutePath.replaceAll("\\\\", "");

        path = path.replaceAll("\\/", "");
        absolutePath = absolutePath.replaceAll("\\/", "");

        return absolutePath.replace(path, "");
    }

    public static String getExtension(File f){
        String ext = "";
        if(f != null && f.isFile()) {
            Pattern p = Pattern.compile("\\.[a-zA-Z]+$");
            Matcher m = p.matcher(f.getName());

            if (m.find()) {
                ext = m.group(0);
            }
        }

        return ext;
    }
}
