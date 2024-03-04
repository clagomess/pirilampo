package br.com.pirilampo.core.compilers;

import br.com.pirilampo.core.Common;
import br.com.pirilampo.core.dto.ParametroDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class FolderToHTMLCompilerTest extends Common {
    @Test
    public void build() throws Exception {
        File logoFile = new File(Thread.currentThread()
                .getContextClassLoader()
                .getResource("logo_xxx.png").getFile());

        File targetFile = new File("target/FolderToHTMLCompilerTest");
        if(!targetFile.isDirectory()) assertTrue(targetFile.mkdir());

        ParametroDto parametro = new ParametroDto();
        parametro.setClrMenu("#666");
        parametro.setTxtLogoSrc(logoFile);
        parametro.setTxtSrcFonte(featureFolder);
        parametro.setTxtOutputTarget(targetFile);

        new FolderToHTMLCompiler(parametro).build();

        File htmlFile = new File(targetFile,"html/index.html");
        assertTrue(htmlFile.isFile());

        String htmlString = FileUtils.readFileToString(htmlFile);
        assertNotEquals(htmlString, "");

        assertTrue(htmlString.contains(parametro.getClrMenu()));
        assertFalse(htmlString.contains((new File(String.valueOf(parametro.getTxtLogoSrc()))).getName()));
        assertFalse(htmlString.contains("logo_xxx.png"));
        assertTrue(htmlString.contains("#/html/html_embed.html"));
        assertTrue(htmlString.contains("html_embed_txt"));
    }

    @Test
    public void build_master() throws Exception {
        File targetFile = new File("target/FolderToHTMLCompilerTest");
        if(!targetFile.isDirectory()) assertTrue(targetFile.mkdir());

        ParametroDto parametro = new ParametroDto();
        parametro.setTxtSrcFonte(featureFolder);
        parametro.setTxtSrcFonteMaster(featureMasterFolder);
        parametro.setTxtOutputTarget(targetFile);
        new FolderToHTMLCompiler(parametro).build();

        File htmlFile = new File(targetFile,"html/index.html");
        assertTrue(htmlFile.isFile());

        String htmlString = FileUtils.readFileToString(htmlFile);

        assertTrue(htmlString.contains("YYY_MASTER_YYY"));
    }

    // @TODO: impl unit for test remove buffer on error
}
