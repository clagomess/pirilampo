package com.github.clagomess.pirilampo.core.parsers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

public class JsonParser extends ObjectMapper {
    @Getter
    private static final JsonParser instance = new JsonParser();

    private JsonParser() {
        disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
    }
}
