package br.com.pirilampo.core.util;

import br.com.pirilampo.core.dto.ParametroDto;
import br.com.pirilampo.core.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.util.Properties;

@Slf4j
public class PropertiesTest {
    @Test
    public void parametroToProperties(){
        Properties prop = PropertiesUtil.parametroToProperties(new ParametroDto());

        Assert.assertNotNull(prop.getProperty("txtNome"));
    }
}
