package br.com.pirilampo.core.compilers;

import br.com.pirilampo.core.dto.ParametroDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.BOMInputStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class FolderToHTMLCompilerTest {
    private final String projectName = "XXX_PROJECT_NAME_XXX";
    private final String projectVersion = "1.2.3";
    private final String featureName = "xxx.Feature";
    private final String featureExt = ".Feature";

    private String resourcePath = null;
    private final ParametroDto parametro = new ParametroDto();
    private List<File> pastas = new ArrayList<>();

    @BeforeEach
    public void before() {
        resourcePath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        parametro.setTxtNome(projectName);
        parametro.setTxtVersao(projectVersion);
    }

    private File criarPasta(){
        String dir = System.getProperty("java.io.tmpdir");
        dir += File.separator;
        dir += "pirilampo_test";

        File f = new File(dir);

        if (f.isDirectory() || f.mkdir()) {
            dir += File.separator;
            dir += (new Long(Calendar.getInstance().getTime().getTime())).toString();

            f = new File(dir);
            if(f.mkdir()){
                pastas.add(f);
            }

            log.info("Pasta de teste: {}", f.getAbsolutePath());
        }

        return f;
    }

    private String load(String path) {
        StringBuilder buffer = new StringBuilder();
        String linha;
        BufferedReader br;

        try (FileInputStream fis = new FileInputStream(path)){
            BOMInputStream bis = new BOMInputStream(fis);

            br = new BufferedReader(new InputStreamReader(bis, "UTF-8"));

            while ((linha = br.readLine()) != null) {
                buffer.append(linha).append("\n");
            }
        }catch (Exception e){
            e.printStackTrace();
            buffer = new StringBuilder();
        }

        return buffer.toString();
    }

    @Test
    public void build(){
        parametro.setClrMenu("#666");
        parametro.setTxtLogoSrc(new File(resourcePath + File.separator + "logo_xxx.png"));
        parametro.setTxtSrcFonte(new File(resourcePath + File.separator + "feature"));
        parametro.setTxtOutputTarget(new File(criarPasta().getAbsolutePath()));

        try {
            new FolderToHTMLCompiler(parametro).build();

            String html = parametro.getTxtOutputTarget() + File.separator + "index.html";
            assertTrue((new File(html)).isFile());

            String htmlString = load(html);
            assertNotEquals(htmlString, "");

            assertTrue(htmlString.contains(parametro.getClrMenu()));
            assertFalse(htmlString.contains((new File(String.valueOf(parametro.getTxtLogoSrc()))).getName()));
            assertFalse(htmlString.contains("logo_xxx.png"));
            assertTrue(htmlString.contains("#/html/html_embed.html"));
            assertTrue(htmlString.contains("html_embed_txt"));
        }catch (Exception e){
            log.error(log.getName(), e);
            fail();
        }
    }

    @Test
    public void build_master() {
        try {
            parametro.setTxtSrcFonte(new File(resourcePath + File.separator + "feature"));
            parametro.setTxtSrcFonteMaster(new File(resourcePath + File.separator + "master"));
            parametro.setTxtOutputTarget(new File(criarPasta().getAbsolutePath()));
            new FolderToHTMLCompiler(parametro).build();

            String html = parametro.getTxtOutputTarget() + File.separator + "index.html";
            assertTrue((new File(html)).isFile());

            String htmlString = load(html);

            assertTrue(htmlString.contains("YYY_MASTER_YYY"));
        }catch (Exception e){
            log.error(log.getName(), e);
            fail();
        }
    }

    @AfterEach
    public void after() throws Exception {
        for (File dir : pastas){
            FileUtils.deleteDirectory(dir);
        }
    }
}
