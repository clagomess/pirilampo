package com.github.clagomess.pirilampo.core.compilers;

import com.github.clagomess.pirilampo.core.dto.ParametersDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
public class PropertiesCompiler {
    @Test
    public void parametroToProperties(){
        Properties prop = PropertiesCompiler.parametroToProperties(new ParametersDto());

        assertNotNull(prop.getProperty("txtNome"));
    }
}
