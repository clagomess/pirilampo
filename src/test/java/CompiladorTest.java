import br.com.pirilampo.util.Compilador;
import org.apache.commons.io.input.BOMInputStream;
import org.junit.Test;

import java.io.*;
import java.util.Base64;
import java.util.Calendar;

import static org.junit.Assert.*;

public class CompiladorTest {
    private final String featureName = "xxx.feature";
    private final String imgName = "xxx.png";
    private final String imgLogoName = "logo_xxx.png";
    private final String imgBase64 = "iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAC40lEQVR4XnVTy09TWRz+7r3lFi19UBRKyrTAAjVWCpQCZQj1QSRBqRMfMZowjQtdzMKdM8PSP8EYNy7MbNy50zCLyUwU48KEmTiRd4QhBVpKO22hva/2nns8bRpiI97ky+/8ft/3/c6558FRSvGtz9ffXwfAzqDMz81Jh2n4b5nPBIPDPM8/4zjuJcMTlvsPFVJKDxCNRunpQEDsHhh46B8cLATDYToyPk5DY2PUPzSk9w4Pv2B8Q1lX9dQ2EATBc/3aVdoXCqmjExP0/JUIvfvrLzQyNUXPXrpEz01O0siVSdrq8Xirntpf+DHo/sGXequH7TmzVTBwsqcHtsZGnPB3o6XNDbe8hd7se3K2hYuy/RFr9oAVjrsE5YHHKZhuhY7h4QUHvFvvoP/9B8z/vMLNpjh++t6Cgd4OoV2U7wHw1zTwCIWwyNMmp1VAIZVAm9uJO5d96JLXcP9GD0aCnTBBRymzBaZztvFSF5vUyldn73DxSp/NDNFUx0MQTdCkHAxSgkFRibqmQinkUOatTOcS5G4A7oMVWJq903UCFSgpGwiSawvI7Wwgr5SQT8cRX5qr1Mu8yHTWZu/PAForDfp9vvXtjU9PNZ0jeomgqOrQJA3ZRAwSG6c316Dk5Uq9zJd1ZT3z/VVp8HJmJrBnbnyd10CUIkF2T0OhoEFhkDSdRRWSxMYszysETGcw/Zs/Z2evmgCAnetC0oCzRPl4RuHa6y1m5JMKLPU8ZM1AaleGrBooEg6qIaIILZk0jvxvdx5Z5wGA3XMNgLyqWx/t7ukEvAlFmwsp2QRCKRJ7FAXOAuJwIZ1TjZWi7TEAg2H7y4u0uKw7FrOG+HsykaX16j6E1vbKKfDfnYKpwYZMbJOmiDi7Suz/AkizieWDBizJAYj9tiivpqydXGxnn6RXVipcZnkeO//FSLqhg3u+JH0AUGT4+NVjAnC0+qAuMvQxRHyBwDSLtxlGGdxl3tHU5Kh68BluxIWLuaA4mgAAAABJRU5ErkJggg==";

    private String criarFeature() {
        Boolean toReturn;
        File f;

        //-- cria diretorio
        String dir = System.getProperty("java.io.tmpdir");
        dir += File.separator;
        dir += "pirilampo_test";

        f = new File(dir);
        toReturn = f.isDirectory() || f.mkdir();

        if (toReturn) {
            dir += File.separator;
            dir += (new Long(Calendar.getInstance().getTime().getTime())).toString();

            f = new File(dir);
            toReturn = f.mkdir();

            System.out.println("Pasta de teste: " + dir);
        }

        //-----------

        // -- cria feature
        String featurePath = null;
        if (toReturn) {
            final String feature = "# language: pt\n" +
                    "# encoding: utf-8\n" +
                    "Funcionalidade: XX\n" +
                    "\nXXX\n" +
                    "\nContexto: XXX\n" +
                    "\nDado XXX" +
                    "\nE Teste" +
                    "\n| Ibagem |" +
                    "\n| ![Image](" + imgName + ") |" +
                    "\n| <img src=\"" + imgName + "\"> |   " +
                    "\n| <img src=\"" + imgName + "\" width=\"50\"> |";

            featurePath = dir;
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

                FileOutputStream fos = new FileOutputStream(dir + File.separator + imgName);
                fos.write(imgBytes);
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            File ffi = new File(dir + File.separator + imgName);
            toReturn = ffi.isFile();
        }

        return (toReturn ? dir : null);
    }

    private String load(String path) {
        String buffer = "";
        String linha;
        BufferedReader br;

        try {
            BOMInputStream bis = new BOMInputStream(new FileInputStream(path));

            br = new BufferedReader(new InputStreamReader(bis, "UTF-8"));

            while ((linha = br.readLine()) != null) {
                buffer += linha + "\n";
            }
        }catch (Exception e){
            e.printStackTrace();
            buffer = "";
        }

        return buffer;
    }

    @Test
    public void testCriarFeature(){
        assertNotNull(criarFeature());
    }

    @Test
    public void testCompileFeaturePath(){
        File f;
        Compilador compilador = new Compilador();
        final String COR_MENU = "#666";
        final String NM_MN_RAIZ = "NOME_MENU_RAIZ_666";
        String logoPath = null;

        //-- Cria feature
        String dir = criarFeature();

        assertNotNull(dir);

        //-- Cria logo
        logoPath = dir + File.separator + imgLogoName;
        try {
            byte[] imgBytes = Base64.getDecoder().decode(imgBase64);

            FileOutputStream fos = new FileOutputStream(logoPath);
            fos.write(imgBytes);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        f = new File(logoPath);
        assertTrue(f.isFile());

        Compilador.setConfig(COR_MENU, NM_MN_RAIZ, new File(logoPath));

        assertEquals(Compilador.COR_MENU, COR_MENU);
        assertEquals(Compilador.NOME_MENU_RAIZ, NM_MN_RAIZ);

        try {
            // cria pasta de saida;
            f = new File(dir + File.separator + "outdir");
            assertTrue(f.mkdir());

            compilador.compilarPasta(
                    dir,
                    null,
                    "XXX",
                    "XXX",
                    dir + File.separator + "outdir" + File.separator
            );

            String html = dir + File.separator + "outdir" + File.separator + "index.html";

            f = new File(html);
            assertTrue(f.isFile());

            String htmlString = load(html);
            assertNotEquals(htmlString, "");

            assertTrue(htmlString.contains(Compilador.COR_MENU));
            assertTrue(htmlString.contains(Compilador.NOME_MENU_RAIZ));
            assertFalse(htmlString.contains(Compilador.LOGO_PATH.getName()));
        }catch (Exception e){
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testCompileFeature(){
        Compilador compilador = new Compilador();
        String dir = criarFeature();

        assertNotNull(dir);

        try {
            File f;

            //-- compila sem output
            compilador.compilarFeature(
                    dir + File.separator + featureName,
                    "XXX",
                    "XXX",
                    null
            );

            f = new File(dir + File.separator + featureName.replace(".feature", ".html"));
            assertTrue(f.isFile());

            //-- compila com output
            // cria pasta de saida;
            f = new File(dir + File.separator + "outdir");
            assertTrue(f.mkdir());

            compilador.compilarFeature(
                    dir + File.separator + featureName,
                    "XXX",
                    "XXX",
                    dir + File.separator + "outdir" + File.separator
            );

            f = new File(dir + File.separator + "outdir" + File.separator + featureName.replace(".feature", ".html"));
            assertTrue(f.isFile());
        }catch (Exception e){
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testCompileImage(){
        Compilador compilador = new Compilador();
        String dir = criarFeature();

        assertNotNull(dir);

        try {
            File f;

            //-- compila sem output
            compilador.compilarFeature(
                    dir + File.separator + featureName,
                    "XXX",
                    "XXX",
                    null
            );

            String html = dir + File.separator + featureName.replace(".feature", ".html");

            f = new File(html);
            assertTrue(f.isFile());

            String htmlString = load(html);
            assertNotEquals(htmlString, "");

            assertFalse(htmlString.contains(imgName));
            assertTrue(htmlString.contains("width=\"50\""));
        }catch (Exception e){
            e.printStackTrace();
            fail();
        }
    }
}