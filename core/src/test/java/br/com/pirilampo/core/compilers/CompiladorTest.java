package br.com.pirilampo.core.compilers;

import br.com.pirilampo.core.dto.ParametroDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Slf4j
public class CompiladorTest {
    private final String projectName = "XXX_PROJECT_NAME_XXX";
    private final String projectVersion = "1.2.3";
    private final String featureName = "xxx.Feature";
    private final String featureExt = ".Feature";

    private String resourcePath = null;
    private final ParametroDto parametro = new ParametroDto();
    private List<File> pastas = new ArrayList<>();

    @Before
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
    public void testCompileFeaturePath(){
        Compilador compilador = new Compilador();
        parametro.setClrMenu("#666");
        parametro.setTxtLogoSrc(resourcePath + File.separator + "logo_xxx.png");
        parametro.setTxtSrcFonte(resourcePath + File.separator + "feature");
        parametro.setTxtOutputTarget(criarPasta().getAbsolutePath());

        try {
            compilador.compilarPasta(parametro);

            String html = parametro.getTxtOutputTarget() + File.separator + "index.html";
            Assert.assertTrue((new File(html)).isFile());

            String htmlString = load(html);
            Assert.assertNotEquals(htmlString, "");

            Assert.assertTrue(htmlString.contains(parametro.getClrMenu()));
            Assert.assertFalse(htmlString.contains((new File(parametro.getTxtLogoSrc())).getName()));
            Assert.assertFalse(htmlString.contains("logo_xxx.png"));
            Assert.assertTrue(htmlString.contains("#/html/html_embed.html"));
            Assert.assertTrue(htmlString.contains("html_embed_txt"));
        }catch (Exception e){
            log.error(CompiladorTest.class.getName(), e);
            Assert.fail();
        }
    }

    @Test
    public void testCompileFeature(){
        Compilador compilador = new Compilador();
        try {
            parametro.setTxtSrcFonte(resourcePath + File.separator + "feature/xxx.Feature");
            parametro.setTxtOutputTarget(criarPasta().getAbsolutePath());
            compilador.compilarFeature(parametro);

            String html = parametro.getTxtOutputTarget() + File.separator + featureName.replace(featureExt, ".html");

            Assert.assertTrue((new File(html)).isFile());

            String htmlString = load(html);
            Assert.assertNotEquals(htmlString, "");

            Assert.assertFalse(htmlString.contains("xxx.png"));
            Assert.assertTrue(htmlString.contains("https://pt.wikipedia.org/static/images/project-logos/ptwiki.png"));
            Assert.assertTrue(htmlString.contains("width=\"50\""));
            Assert.assertFalse(htmlString.contains("&lt;strike&gt;"));
            Assert.assertTrue(htmlString.contains("<strike>"));
            Assert.assertFalse(htmlString.contains("&lt;br&gt;"));
            Assert.assertTrue(htmlString.contains("<br/>"));
        }catch (Exception e){
            log.error(CompiladorTest.class.getName(), e);
            Assert.fail();
        }
    }

    @Test(timeout = 8000)
    public void testCompilePdf(){
        Compilador compilador = new Compilador();

        try {
            parametro.setTxtSrcFonte(resourcePath + File.separator + "feature/xxx.Feature");
            parametro.setTxtOutputTarget(criarPasta().getAbsolutePath());
            compilador.compilarFeaturePdf(parametro);

            String pdf = parametro.getTxtOutputTarget() + File.separator + featureName.replace(featureExt, ".pdf");
            Assert.assertTrue((new File(pdf)).isFile());

            PDDocument pdfDocument = PDDocument.load(new File(pdf));
            String pdfAsStr = new PDFTextStripper().getText(pdfDocument);

            Assert.assertTrue(pdfAsStr.contains(projectName));
            Assert.assertTrue(pdfAsStr.contains(projectVersion));

            // Verifica se tem as imagens
            boolean possuiImagens = false;
            for (COSName cosName : pdfDocument.getPage(0).getResources().getXObjectNames()){
                PDXObject xobject = pdfDocument.getPage(0).getResources().getXObject(cosName);

                if (xobject instanceof PDImageXObject) {
                    possuiImagens  = true;
                    break;
                }
            }

            pdfDocument.close();

            Assert.assertTrue(possuiImagens);
        }catch (Exception e){
            log.error(CompiladorTest.class.getName(), e);
            Assert.fail();
        }
    }

    @Test(timeout = 8000)
    public void testCompilePdfPath(){
        Compilador compilador = new Compilador();

        try {
            parametro.setTxtSrcFonte(resourcePath + File.separator + "feature");
            parametro.setTxtOutputTarget(criarPasta().getAbsolutePath());
            compilador.compilarPastaPdf(parametro);
            String pdf = parametro.getTxtOutputTarget() + File.separator + "index.pdf";
            Assert.assertTrue((new File(pdf)).isFile());

            PDDocument pdfDocument = PDDocument.load(new File(pdf));
            String pdfAsStr = new PDFTextStripper().getText(pdfDocument);

            Assert.assertTrue(pdfAsStr.contains(projectName));
            Assert.assertTrue(pdfAsStr.contains(projectVersion));

            pdfDocument.close();
        }catch (Exception e){
            log.error(CompiladorTest.class.getName(), e);
            Assert.fail();
        }
    }

    @Test
    public void testCompileFeatureMaster() {
        Compilador compilador = new Compilador();

        try {
            parametro.setTxtSrcFonte(resourcePath + File.separator + "feature");
            parametro.setTxtSrcFonteMaster(resourcePath + File.separator + "master");
            parametro.setTxtOutputTarget(criarPasta().getAbsolutePath());
            compilador.compilarPasta(parametro);

            String html = parametro.getTxtOutputTarget() + File.separator + "index.html";
            Assert.assertTrue((new File(html)).isFile());

            String htmlString = load(html);

            Assert.assertTrue(htmlString.contains("YYY_MASTER_YYY"));
        }catch (Exception e){
            log.error(CompiladorTest.class.getName(), e);
            Assert.fail();
        }
    }

    @After
    public void after() throws Exception {
        for (File dir : pastas){
            FileUtils.deleteDirectory(dir);
        }
    }
}
