package com.github.clagomess.pirilampo.core.parsers;

import com.github.clagomess.pirilampo.core.dto.FeatureIndexDto;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
public class IndexParserTest {
    @Test
    public void getPhraseId(){
        IndexParser indexParser = new IndexParser();

        assertEquals(0, indexParser.getPhraseId("b"));
        assertEquals(1, indexParser.getPhraseId("a"));
        assertEquals(1, indexParser.getPhraseId("a"));
        assertEquals(2, indexParser.phrases.size());
    }

    @Test
    public void setFeatureTitle_new(){
        IndexParser indexParser = new IndexParser();
        indexParser.setFeatureTitle("f-id", "func");

        assertEquals(1, indexParser.index.size());
        assertNotNull(indexParser.index.get(0).getTitle());
    }

    @Test
    public void setFeatureTitle_update(){
        IndexParser indexParser = new IndexParser();
        indexParser.index.put(indexParser.getPhraseId("f-id"), new FeatureIndexDto());
        indexParser.setFeatureTitle("f-id", "func");

        assertEquals(1, indexParser.index.size());
        assertNotNull(indexParser.index.get(0).getTitle());
    }

    @ParameterizedTest
    @CsvSource(value = {
            "<br/><p><img src=\"https://picsum.photos/800/200\"  alt=\"Image\" //></p>$",
            "<br/><p><img src=\"data:image/png;base64,iVBORw0KGg...\" /></p>$",
            "<br/><p><img src=\"NMdSnfAaxO.png\" /></p>$",
            "Link Google: <a href=\"https://www.google.com.br\">Google</a>$Link Google: Google",
            "Link Html Embeded: <a href=\"KEqOGcTrgn.html\">Link Embeded</a>$Link Html Embeded: Link Embeded",
            "zztjynblb<a href=\"#/scenario/VVlUTIBDZa/1\">DEF003</a>$zztjynblbDEF003",
            "jZErZDIoaI Rlewk$jZErZDIoaI Rlewk",
            "<br/><p><img src=\"data:image/png;base64,iVBORw0KGg...\"  width=\"50\"/></p>$",
            "<br/><p><img src=\"NMdSnfAaxO.png\"  width=\"50\"/></p>$",
            "<strike>strike<br/>strike</strike>$strikestrike",
            "'<ul>\n<li>YYY_MASTER_YYY</li>\n</ul>'$YYY_MASTER_YYY",
            "&lt;user&gt; and &lt;password&gt;$user and password",
    }, delimiter = '$')
    public void parseRawPhrase(String raw, String expected){
        IndexParser indexParser = new IndexParser();
        assertEquals(expected, indexParser.parseRawPhrase(raw));
    }

    @Test
    public void putFeaturePhrase(){
        IndexParser indexParser = new IndexParser();
        indexParser.index.put(indexParser.getPhraseId("f-id"), new FeatureIndexDto());

        indexParser.putFeaturePhrase("f-id", "a new func <a>");
        log.info("{}", indexParser.index.get(0).getPhrases());
        assertEquals(1, indexParser.index.get(0).getPhrases().size());
        assertEquals(1, indexParser.index.get(0).getPhrases().size());
    }

    @Test
    public void buildIndex() throws IOException {
        IndexParser indexParser = new IndexParser();
        indexParser.index.put(indexParser.getPhraseId("f-id"), new FeatureIndexDto());
        indexParser.putFeaturePhrase("f-id", "a new func");
        indexParser.setFeatureTitle("f-id", "checkpoint");

        StringWriter sw = new StringWriter();
        try (PrintWriter out = new PrintWriter(sw)){
            indexParser.buildIndex(out);
            log.info("{}", sw);
        }

        Assertions.assertThat(sw.toString())
                .contains("let indexPhrases = {\"a new func\":1,\"checkpoint\":2,\"f-id\":0};")
                .contains("let indexMap = {\"0\":{\"title\":2,\"phrases\":[1]}};");
    }
}
