package bean;

import br.com.pirilampo.bean.Parametro;
import javafx.scene.paint.Color;
import org.junit.Assert;
import org.junit.Test;

public class ParametroTest {
    @Test
    public void colorHex(){
        Parametro parametro = new Parametro();

        Assert.assertEquals("#003300", parametro.colorHex(Color.web("#003300")));
    }
}
