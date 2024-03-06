package br.com.pirilampo.core.parsers;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class MarkdownParserTest {
    private final MarkdownParser markdownParser = new MarkdownParser();

    @Test
    public void build(){
        assertEquals(
                "a: <a href=\"xx_a.html\">xx_a</a>",
                markdownParser.build("a: [xx_a](xx_a.html)")
        );
    }
}
