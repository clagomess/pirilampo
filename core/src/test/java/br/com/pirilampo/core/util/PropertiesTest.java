package br.com.pirilampo.core.util;

import br.com.pirilampo.core.dto.ParametersDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
public class PropertiesTest {
    @Test
    public void parametroToProperties(){
        Properties prop = PropertiesUtil.parametroToProperties(new ParametersDto());

        assertNotNull(prop.getProperty("txtNome"));
    }
}
