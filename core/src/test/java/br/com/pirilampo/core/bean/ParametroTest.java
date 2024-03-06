package br.com.pirilampo.core.bean;

import br.com.pirilampo.core.dto.ParametersDto;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParametroTest {
    @Test
    public void colorHex(){
        ParametersDto parameters = new ParametersDto();

        assertEquals("#003300", parameters.colorHex(Color.web("#003300")));
    }
}
