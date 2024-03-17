package com.github.clagomess.pirilampo.core.parsers;

import com.github.clagomess.pirilampo.core.compilers.Compiler;
import com.github.clagomess.pirilampo.core.dto.ParametersDto;
import com.itextpdf.text.Image;
import com.itextpdf.tool.xml.pipeline.html.AbstractImageProvider;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

@Slf4j
public class PdfImageProvider extends AbstractImageProvider {
    private final Compiler compiler = new Compiler(){};
    private final ParametersDto parametersDto;

    @Setter
    private File currentFeature;

    public PdfImageProvider(ParametersDto parametersDto) {
        this.parametersDto = parametersDto;
    }

    @Override
    public Image retrieve(String src) {
        log.info("- Processing image: {}", src);

        try {
            if(Pattern.compile("^http").matcher(src).find()){
                File imageCached = downloadImageToCache(src);
                if(imageCached != null) return Image.getInstance(imageCached.getAbsolutePath());
            }

            File imageFile = compiler.getAbsolutePathFile(parametersDto, currentFeature, src);
            if (imageFile != null && imageFile.isFile()){
                return Image.getInstance(imageFile.getAbsolutePath());
            }
        } catch (Throwable e) {
            log.error(e.getMessage());
        }

        log.warn("-- Image not found");

        return null;
    }

    @Override
    public String getImageRootPath() {
        return null;
    }

    protected File downloadImageToCache(String src){
        try {
            File cacheDir = getHomeCacheDir();
            File cacheImage = new File(cacheDir, md5(src));

            if(cacheImage.isFile()){
                log.info("- hit cache {}", cacheImage);
                return cacheImage;
            }

            try (
                    InputStream in = new BufferedInputStream(new URL(src).openStream());
                    FileOutputStream fos = new FileOutputStream(cacheImage);
            ) {
                byte[] buffer = new byte[1024 * 4];
                int n;

                while ((n = in.read(buffer)) != -1) {
                    fos.write(buffer, 0, n);
                }
            }

            return cacheImage;
        }catch (Throwable e){
            log.error(e.getMessage());
            return null;
        }
    }

    protected String md5(String input) throws NoSuchAlgorithmException {
        return String.format(
                "%032x",
                new BigInteger(1, MessageDigest.getInstance("MD5").digest(input.getBytes()))
        );
    }

    protected File getHomeCacheDir(){
        File cacheDir = new File(String.format(
                "%s%s.pirilampo%scache",
                System.getProperty("user.home"),
                File.separator,
                File.separator
        ));

        if(!cacheDir.exists()) cacheDir.mkdirs();

        return cacheDir;
    }
}
