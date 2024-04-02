package com.github.clagomess.pirilampo.core.parsers;

import com.github.clagomess.pirilampo.core.Common;
import com.github.clagomess.pirilampo.core.dto.ParametersDto;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.File;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class PdfImageProviderTest extends Common {
    private final ParametersDto parameters = new ParametersDto(){{
        setProjectSource(featureFolder);
    }};

    private final PdfImageProvider pdfImageProvider = new PdfImageProvider(parameters);

    @ParameterizedTest
    @CsvSource(value = {
            "smallest.png, true",
            "xx_a.png, false",
            "https://picsum.photos/800/200, true",
            "https://picsum.photos/800/200, true", // hit
            "https://iVBORw0KGgo.com.br/iVBORw0KGgo.png, false"
    })
    public void retrieve(String src, boolean expected){
        pdfImageProvider.setCurrentFeature(featureFile);
        assertEquals(expected, pdfImageProvider.retrieve(src) != null);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "https://picsum.photos/800/200, true",
            "https://iVBORw0KGgo.com.br/iVBORw0KGgo.png, false"
    })
    public void downloadImageToCache(String src, boolean expected){
        File cache = new File(pdfImageProvider.getHomeCacheDir(), src);
        if(cache.isFile()) cache.delete();

        assertEquals(expected, pdfImageProvider.downloadImageToCache(src) != null);
    }

    @Test
    public void getHomeCacheDir(){
        val result = pdfImageProvider.getHomeCacheDir();
        log.info("{}", result);
        assertTrue(result.isDirectory());
    }

    @Test
    public void md5() throws NoSuchAlgorithmException {
        assertEquals("900150983cd24fb0d6963f7d28e17f72", pdfImageProvider.md5("abc"));
    }
}
