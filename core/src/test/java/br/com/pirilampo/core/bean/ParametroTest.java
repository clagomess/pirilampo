package br.com.pirilampo.core.bean;

import br.com.pirilampo.core.dto.ParametroDto;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParametroTest {
    @Test
    public void colorHex(){
        ParametroDto parametro = new ParametroDto();

        assertEquals("#003300", parametro.colorHex(Color.web("#003300")));
    }
}
