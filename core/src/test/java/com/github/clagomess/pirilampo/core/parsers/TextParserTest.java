package com.github.clagomess.pirilampo.core.parsers;

import com.github.clagomess.pirilampo.core.Common;
import com.github.clagomess.pirilampo.core.dto.ParametersDto;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class TextParserTest extends Common {
    @ParameterizedTest
    @CsvSource(value = {
            "![Image](https://picsum.photos/800/200)$<br/><p><img src=\"https://picsum.photos/800/200\" style=\"max-width: 100%\"  alt=\"Image\" //></p>",
            "<img src=\"smallest.png\">$<br/><p><img src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABAQAAAAA3bvkkAAAACklEQVR4AWNgAAAAAgABc3UBGAAAAABJRU5ErkJggg==\" style=\"max-width: 100%\" /></p>",
            "<img src=\"NMdSnfAaxO.png\">$<br/><p><img src=\"NMdSnfAaxO.png\" style=\"max-width: 100%\" /></p>",
            "Link Google: [Google](https://www.google.com.br)$Link Google: <a href=\"https://www.google.com.br\">Google</a>",
            "Link Html Embeded: [Link Embeded](KEqOGcTrgn.html)$Link Html Embeded: <a href=\"KEqOGcTrgn.html\">Link Embeded</a>",
            "Link Html Embeded: [Link Embeded](html_embed.html)$Link Html Embeded: <a href=\"#/html/html_embed.html\">Link Embeded</a>",
            "zztjynblb[DEF003](#/scenario/VVlUTIBDZa/1)$zztjynblb<a href=\"#/scenario/VVlUTIBDZa/1\">DEF003</a>",
            "  jZErZDIoaI Rlewk$jZErZDIoaI Rlewk",
            "<img src=\"smallest.png\" width=\"50\">$<br/><p><img src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABAQAAAAA3bvkkAAAACklEQVR4AWNgAAAAAgABc3UBGAAAAABJRU5ErkJggg==\" style=\"max-width: 100%\"  width=\"50\"/></p>",
            "<img src=\"NMdSnfAaxO.png\" width=\"50\">$<br/><p><img src=\"NMdSnfAaxO.png\" style=\"max-width: 100%\"  width=\"50\"/></p>",
            "<strike>strike<br>strike</strike>$<strike>strike<br/>strike</strike>",
            "- YYY_MASTER_YYY$'<ul>\n<li>YYY_MASTER_YYY</li>\n</ul>\n'",
            "<user> and <password>$&lt;user&gt; and &lt;password&gt;",
            "<img src=\"NMdSnfAaxO.png\"/>$<br/><p><img src=\"NMdSnfAaxO.png\" style=\"max-width: 100%\" //></p>",
            "a: <img src=\"xx_a.png\"> b: <img src=\"xx_b.png\">$a: <br/><p><img src=\"xx_a.png\" style=\"max-width: 100%\" /></p> b: <br/><p><img src=\"xx_b.png\" style=\"max-width: 100%\" /></p>",
            "a: ![ImageA](xx_a.png) b: ![ImageB](xx_b.png)$a: <br/><p><img src=\"xx_a.png\" style=\"max-width: 100%\"  alt=\"ImageA\" //></p> b: <br/><p><img src=\"xx_b.png\" style=\"max-width: 100%\"  alt=\"ImageB\" //></p>",
            "a: [xx_a](https://xx_a.com) b: [xx_b](https://xx_b.com)$a: <a href=\"https://xx_a.com\">xx_a</a> b: <a href=\"https://xx_b.com\">xx_b</a>",
            "a: [xx_a](xx_a.html) b: [xx_b](xx_b.html)$a: <a href=\"xx_a.html\">xx_a</a> b: <a href=\"xx_b.html\">xx_b</a>",
            "'aaa \n bbb ![ImageA](xx_a.png)'$'aaa\nbbb <br/><p><img src=\"xx_a.png\" style=\"max-width: 100%\"  alt=\"ImageA\" //></p>'",
    }, delimiter = '$', ignoreLeadingAndTrailingWhitespace = false)
    public void format(String raw, String expected){
        val parameters = new ParametersDto();
        parameters.setProjectSource(featureFolder);

        val textParser = new TextParser(new IndexParser(), parameters, featureFile);

        StringWriter sw = new StringWriter();
        try (PrintWriter out = new PrintWriter(sw)){
            textParser.format(out, raw, true);
            assertEquals(expected, sw.toString());
        }
    }
}
