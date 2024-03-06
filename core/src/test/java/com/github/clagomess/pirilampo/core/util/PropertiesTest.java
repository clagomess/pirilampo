package com.github.clagomess.pirilampo.core.util;

import com.github.clagomess.pirilampo.core.dto.ParametersDto;
import com.github.clagomess.pirilampo.core.util.PropertiesUtil;
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
