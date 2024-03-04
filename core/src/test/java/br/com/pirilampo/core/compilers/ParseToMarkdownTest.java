package br.com.pirilampo.core.compilers;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class ParseToMarkdownTest {
    private final ParseToMarkdown parseToMarkdown = new ParseToMarkdown();

    @Test
    public void build(){
        assertEquals(
                "a: <a href=\"xx_a.html\">xx_a</a>",
                parseToMarkdown.build("a: [xx_a](xx_a.html)")
        );
    }
}
