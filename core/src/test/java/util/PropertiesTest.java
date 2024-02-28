package util;

import br.com.pirilampo.bean.Parametro;
import br.com.pirilampo.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.util.Properties;

@Slf4j
public class PropertiesTest {
    @Test
    public void parametroToProperties(){
        Properties prop = PropertiesUtil.parametroToProperties(new Parametro());

        Assert.assertNotNull(prop.getProperty("txtNome"));
    }
}
