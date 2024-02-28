package br.com.pirilampo.core.bean;

import br.com.pirilampo.core.dto.ParametroDto;
import javafx.scene.paint.Color;
import org.junit.Assert;
import org.junit.Test;

public class ParametroTest {
    @Test
    public void colorHex(){
        ParametroDto parametro = new ParametroDto();

        Assert.assertEquals("#003300", parametro.colorHex(Color.web("#003300")));
    }
}
