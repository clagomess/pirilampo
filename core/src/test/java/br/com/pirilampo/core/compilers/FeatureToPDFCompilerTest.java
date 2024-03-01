package br.com.pirilampo.core.compilers;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class FeatureToPDFCompilerTest {
    @Test //(timeout = 8000) @TODO: check
    public void build(){

            /*
            parametro.setTxtSrcFonte(new File(resourcePath + File.separator + "feature/xxx.Feature"));
            parametro.setTxtOutputTarget(new File(criarPasta().getAbsolutePath()));
            new FolderToHTMLCompiler(parametro).build();

            String pdf = parametro.getTxtOutputTarget() + File.separator + featureName.replace(featureExt, ".pdf");
            assertTrue((new File(pdf)).isFile());

            PDDocument pdfDocument = PDDocument.load(new File(pdf));
            String pdfAsStr = new PDFTextStripper().getText(pdfDocument);

            assertTrue(pdfAsStr.contains(projectName));
            assertTrue(pdfAsStr.contains(projectVersion));

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

            assertTrue(possuiImagens);
        }catch (Exception e){
            log.error(log.getName(), e);
            fail();
        }
        */
    }
}
