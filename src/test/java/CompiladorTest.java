import br.com.pirilampo.main.Main;
import br.com.pirilampo.util.Compilador;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.log4j.*;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.*;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Base64;
import java.util.Calendar;

public class CompiladorTest {
    private static final Logger logger = LoggerFactory.getLogger(CompiladorTest.class);
    private final String projectName = "XXX_PROJECT_NAME_XXX";
    private final String projectVersion = "1.2.3";
    private final String featureName = "xxx.Feature";
    private final String featureExt = ".Feature";
    private final String htmlEmbedName = "html_embed.html";
    private final String imgUrl = "https://pt.wikipedia.org/static/images/project-logos/ptwiki.png";
    private final String imgName = "xxx.png";
    private final String imgBase64 = "iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAC40lEQVR4XnVTy09TWRz+7r3lFi19" +
    "UBRKyrTAAjVWCpQCZQj1QSRBqRMfMZowjQtdzMKdM8PSP8EYNy7MbNy50zCLyUwU48KEmTiRd4QhBVpKO22hva/2nns8bRpiI97ky+/8ft/3/" +
    "c6558FRSvGtz9ffXwfAzqDMz81Jh2n4b5nPBIPDPM8/4zjuJcMTlvsPFVJKDxCNRunpQEDsHhh46B8cLATDYToyPk5DY2PUPzSk9w4Pv2B8Q1" +
    "lX9dQ2EATBc/3aVdoXCqmjExP0/JUIvfvrLzQyNUXPXrpEz01O0siVSdrq8Xirntpf+DHo/sGXequH7TmzVTBwsqcHtsZGnPB3o6XNDbe8hd7" +
    "se3K2hYuy/RFr9oAVjrsE5YHHKZhuhY7h4QUHvFvvoP/9B8z/vMLNpjh++t6Cgd4OoV2U7wHw1zTwCIWwyNMmp1VAIZVAm9uJO5d96JLXcP9G" +
    "D0aCnTBBRymzBaZztvFSF5vUyldn73DxSp/NDNFUx0MQTdCkHAxSgkFRibqmQinkUOatTOcS5G4A7oMVWJq903UCFSgpGwiSawvI7Wwgr5SQT" +
    "8cRX5qr1Mu8yHTWZu/PAForDfp9vvXtjU9PNZ0jeomgqOrQJA3ZRAwSG6c316Dk5Uq9zJd1ZT3z/VVp8HJmJrBnbnyd10CUIkF2T0OhoEFhkD" +
    "SdRRWSxMYszysETGcw/Zs/Z2evmgCAnetC0oCzRPl4RuHa6y1m5JMKLPU8ZM1AaleGrBooEg6qIaIILZk0jvxvdx5Z5wGA3XMNgLyqWx/t7uk" +
    "EvAlFmwsp2QRCKRJ7FAXOAuJwIZ1TjZWi7TEAg2H7y4u0uKw7FrOG+HsykaX16j6E1vbKKfDfnYKpwYZMbJOmiDi7Suz/AkizieWDBizJAYj9" +
    "tiivpqydXGxnn6RXVipcZnkeO//FSLqhg3u+JH0AUGT4+NVjAnC0+qAuMvQxRHyBwDSLtxlGGdxl3tHU5Kh68BluxIWLuaA4mgAAAABJRU5Er" +
    "kJggg==";

    private String featureDir = null;
    private String rootDir = null;
    private String rootDirMaster = null;

    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    @Before
    public void before() throws Exception {
        //-- config logger
        PatternLayout patternLayout = new PatternLayout("%-5p %d{yyyy-MM-dd HH:mm:ss} %c{1}:%L - %m%n");
        ConsoleAppender consoleAppender = new ConsoleAppender(patternLayout);
        BasicConfigurator.resetConfiguration();
        BasicConfigurator.configure(consoleAppender);
        LogManager.getRootLogger().setLevel(Level.INFO);

        //-- cria diretorio
        rootDir = criarPasta();

        //-- cria feature
        featureDir = criarFeature(false);
    }

    private String criarPasta(){
        try {
            Thread.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String dir = System.getProperty("java.io.tmpdir");
        dir += File.separator;
        dir += "pirilampo_test";

        File f = new File(dir);

        if (f.isDirectory() || f.mkdir()) {
            dir += File.separator;
            dir += (new Long(Calendar.getInstance().getTime().getTime())).toString();

            f = new File(dir);
            f.mkdir();

            logger.info("Pasta de teste: {}", dir);
        }

        return dir;
    }

    private String criarFeature(Boolean featureMaster) {
        if(featureMaster){
            rootDirMaster = rootDir;
            rootDir = criarPasta();
        }

        Boolean toReturn = rootDir != null;

        // -- cria feature
        if (toReturn) {
            final String feature = "# language: pt\n" +
                    "# encoding: utf-8\n" +
                    "Funcionalidade: XX\n" +
                    "\nXXX\n" +
                    "\nContexto: XXX\n" + (featureMaster ? " - YYY_MASTER_YYY" : "") +
                    "\nDado XXX" +
                    "\nE Teste" +
                    "\n| Ibagem |" +
                    "\n| ![Image](" + imgName + ") |" +
                    "\n| <img src=\"" + imgName + "\"> |   " +
                    "\n| <img src=\"" + imgName + "\" width=\"50\"> |" +
                    "\n| ![Image]("+ imgUrl +") |" +
                    "\n| Link Html Embeded: [Link Embeded]("+ htmlEmbedName +") |" +
                    "\n| Link Google: [Google](https://www.google.com.br) |" +
                    "\n| <strike>strike</strike> |" +
                    "\n| <strike>strike<br>strike</strike> |" +
                    "\n\n" +
                    "\nEsquema do Cenário: JJJ" +
                    "\nQuando xxx " +
                    "\nE YYY " +
                    "\nExemplos: " +
                    "\n| a | b | " +
                    "\n| c | d | ";

            String featurePath = rootDir;
            featurePath += File.separator;
            featurePath += featureName;

            try {
                Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(featurePath)), "UTF-8"));
                out.write(feature);
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            File ff = new File(featurePath);
            toReturn = ff.isFile();
        }

        //-- Cria imagem
        if (toReturn) {
            try {
                byte[] imgBytes = Base64.getDecoder().decode(imgBase64);

                FileOutputStream fos = new FileOutputStream(rootDir + File.separator + imgName);
                fos.write(imgBytes);
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            File ffi = new File(rootDir + File.separator + imgName);
            toReturn = ffi.isFile();
        }

        //-- Cria Html Embed
        if (toReturn) {
            try {
                FileOutputStream fos = new FileOutputStream(rootDir + File.separator + htmlEmbedName);
                fos.write("<strong>html_embed_txt</strong>".getBytes());
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            File ffh = new File(rootDir + File.separator + htmlEmbedName);
            toReturn = ffh.isFile();
        }

        return (toReturn ? rootDir : null);
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
    public void testCriarFeature(){
        Assert.assertNotNull(featureDir);
    }

    @Test
    public void testCompileFeaturePath(){
        Assert.assertNotNull(featureDir);

        File f;
        Compilador compilador = new Compilador();
        final String COR_MENU = "#666";
        final String NM_MN_RAIZ = "NOME_MENU_RAIZ_666";
        final String imgLogoName = "logo_xxx.png";
        final String logoPath = featureDir + File.separator + imgLogoName;

        //-- Cria logo
        try {
            byte[] imgBytes = Base64.getDecoder().decode(imgBase64);

            FileOutputStream fos = new FileOutputStream(logoPath);
            fos.write(imgBytes);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

        f = new File(logoPath);
        Assert.assertTrue(f.isFile());

        Compilador.setConfig(COR_MENU, NM_MN_RAIZ, new File(logoPath));

        Assert.assertEquals(Compilador.COR_MENU, COR_MENU);
        Assert.assertEquals(Compilador.NOME_MENU_RAIZ, NM_MN_RAIZ);

        try {
            // cria pasta de saida;
            f = new File(featureDir + File.separator + "outdir");
            Assert.assertTrue(f.mkdir());

            compilador.compilarPasta(
                    featureDir,
                    null,
                    projectName,
                    projectVersion,
                    featureDir + File.separator + "outdir" + File.separator
            );

            String html = featureDir + File.separator + "outdir" + File.separator + "index.html";

            f = new File(html);
            Assert.assertTrue(f.isFile());

            String htmlString = load(html);
            Assert.assertNotEquals(htmlString, "");

            Assert.assertTrue(htmlString.contains(Compilador.COR_MENU));
            Assert.assertTrue(htmlString.contains(Compilador.NOME_MENU_RAIZ));
            Assert.assertFalse(htmlString.contains(Compilador.LOGO_PATH.getName()));
            Assert.assertFalse(htmlString.contains(imgLogoName));
        }catch (Exception e){
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testCompileFeature(){
        Compilador compilador = new Compilador();

        Assert.assertNotNull(featureDir);

        try {
            File f;

            //-- compila sem output
            compilador.compilarFeature(
                    featureDir + File.separator + featureName,
                    projectName,
                    projectVersion,
                    null
            );

            f = new File(featureDir + File.separator + featureName.replace(featureExt, ".html"));
            Assert.assertTrue(f.isFile());

            //-- compila com output
            // cria pasta de saida;
            f = new File(featureDir + File.separator + "outdir");
            Assert.assertTrue(f.mkdir());

            compilador.compilarFeature(
                    featureDir + File.separator + featureName,
                    projectName,
                    projectVersion,
                    featureDir + File.separator + "outdir" + File.separator
            );

            f = new File(featureDir + File.separator + "outdir" + File.separator + featureName.replace(featureExt, ".html"));
            Assert.assertTrue(f.isFile());
        }catch (Exception e){
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testCompileImage(){
        Compilador compilador = new Compilador();

        Assert.assertNotNull(featureDir);

        try {
            File f;

            //-- compila sem output
            compilador.compilarFeature(
                    featureDir + File.separator + featureName,
                    projectName,
                    projectVersion,
                    null
            );

            String html = featureDir + File.separator + featureName.replace(featureExt, ".html");

            f = new File(html);
            Assert.assertTrue(f.isFile());

            String htmlString = load(html);
            Assert.assertNotEquals(htmlString, "");

            Assert.assertFalse(htmlString.contains(imgName));
            Assert.assertTrue(htmlString.contains(imgUrl));
            Assert.assertTrue(htmlString.contains("width=\"50\""));
        }catch (Exception e){
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testCompilePdf(){
        Compilador compilador = new Compilador();

        Assert.assertNotNull(featureDir);

        try {
            compilador.compilarFeaturePdf(
                    featureDir + File.separator + featureName,
                    projectName,
                    projectVersion,
                    "P"
            );

            String pdf = featureDir + File.separator + featureName.replace(featureExt, ".pdf");
            File f = new File(pdf);
            Assert.assertTrue(f.isFile());

            PDDocument pdfDocument = PDDocument.load(f);
            String pdfAsStr = new PDFTextStripper().getText(pdfDocument);

            Assert.assertTrue(pdfAsStr.contains(projectName));
            Assert.assertTrue(pdfAsStr.contains(projectVersion));

            // Verifica se tem as imagens
            Boolean possuiImagens = false;
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
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testCompilePdfPath(){
        Assert.assertNotNull(featureDir);

        String outDir = featureDir + File.separator + ".." + File.separator + "html";

        File f;
        Compilador compilador = new Compilador();

        try {
            compilador.compilarPastaPdf(
                    featureDir,
                    projectName,
                    projectVersion,
                    "R"
            );
            String pdf = outDir + File.separator + "index.pdf";
            f = new File(pdf);
            Assert.assertTrue(f.isFile());

            PDDocument pdfDocument = PDDocument.load(f);
            String pdfAsStr = new PDFTextStripper().getText(pdfDocument);

            Assert.assertTrue(pdfAsStr.contains(projectName));
            Assert.assertTrue(pdfAsStr.contains(projectVersion));

            pdfDocument.close();
        }catch (Exception e){
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testCompileFeatureMaster() {
        Compilador compilador = new Compilador();
        String featureMasterDir = criarFeature(true);

        Assert.assertNotNull(featureMasterDir);
        Assert.assertNotNull(featureDir);
        Assert.assertFalse(featureMasterDir.equals(featureDir));

        try {
            File f = new File(featureDir + File.separator + "outdir_w_master");
            Assert.assertTrue(f.mkdir());

            compilador.compilarPasta(
                    featureDir,
                    featureMasterDir,
                    projectName,
                    projectVersion,
                    featureDir + File.separator + "outdir_w_master" + File.separator
            );

            String html = featureDir + File.separator + "outdir_w_master" + File.separator + "index.html";

            f = new File(html);
            Assert.assertTrue(f.isFile());

            String htmlString = load(html);

            Assert.assertTrue(htmlString.contains("YYY_MASTER_YYY"));
        }catch (Exception e){
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testAbsolutePathMethod(){
        Compilador compilador = new Compilador();

        String result = compilador.absoluteNameFeature("foo\\\\bar\\123", "foo\\bar\\123\\xxx.feature");

        Assert.assertEquals("xxx.feature", result);

        result = compilador.absoluteNameFeature("foo//bar/123", "foo/bar/123/xxx.feature");

        Assert.assertEquals("xxx.feature", result);
    }

    @Test
    public void testMain(){
        final String outDir = featureDir + File.separator + "out_dir_main";

        File f = new File(outDir);
        Assert.assertTrue(f.mkdir());

        exit.expectSystemExit();

        try {
            Main.main(new String[]{
                "-feature_path",
                featureDir,
                "-name",
                "XXX",
                "-version",
                "1.2.3",
                "-output",
                outDir + File.separator,
            });
        } catch (Exception e) {
            logger.info("OK");
        }

        f = new File(outDir + File.separator + "index.html");

        Assert.assertTrue(f.isFile());

        try {
            Main.main(new String[]{
                    "-feature",
                    featureDir + File.separator + featureName,
                    "-name",
                    "XXX",
                    "-version",
                    "1.2.3",
                    "-output",
                    outDir + File.separator,
            });
        } catch (Exception e) {
            logger.info("OK");
        }

        f = new File(outDir + File.separator + "xxx.html");

        Assert.assertTrue(f.isFile());
    }

    @Test
    public void testCompileHtmlAnexo(){
        Assert.assertNotNull(featureDir);

        File f;
        Compilador compilador = new Compilador();

        try {
            // cria pasta de saida;
            f = new File(featureDir + File.separator + "outdir");
            Assert.assertTrue(f.mkdir());

            compilador.compilarPasta(
                    featureDir,
                    null,
                    projectName,
                    projectVersion,
                    featureDir + File.separator + "outdir" + File.separator
            );

            String html = featureDir + File.separator + "outdir" + File.separator + "index.html";

            f = new File(html);
            Assert.assertTrue(f.isFile());

            String htmlString = load(html);
            Assert.assertNotEquals(htmlString, "");

            // teste html embeded
            Assert.assertTrue(htmlString.contains("#/html/" + htmlEmbedName));
            Assert.assertTrue(htmlString.contains("html_embed_txt"));
        }catch (Exception e){
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testCompileTagsExcecoes(){
        Compilador compilador = new Compilador();

        Assert.assertNotNull(featureDir);

        try {
            File f;

            //-- compila sem output
            compilador.compilarFeature(
                    featureDir + File.separator + featureName,
                    projectName,
                    projectVersion,
                    null
            );

            String html = featureDir + File.separator + featureName.replace(featureExt, ".html");

            f = new File(html);
            Assert.assertTrue(f.isFile());

            String htmlString = load(html);

            Assert.assertFalse(htmlString.contains("&lt;strike&gt;"));
            Assert.assertTrue(htmlString.contains("<strike>"));
            Assert.assertFalse(htmlString.contains("&lt;br&gt;"));
            Assert.assertTrue(htmlString.contains("<br/>"));
        }catch (Exception e){
            e.printStackTrace();
            Assert.fail();
        }
    }

    @After
    public void after() throws Exception {
        String outDir = featureDir + File.separator + ".." + File.separator + "html";
        FileUtils.deleteDirectory(new File(outDir));

        if(rootDir != null) {
            FileUtils.deleteDirectory(new File(rootDir));
        }

        if(rootDirMaster != null) {
            FileUtils.deleteDirectory(new File(rootDirMaster));
        }
    }
}