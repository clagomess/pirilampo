package br.com.pirilampo.core.compilers;

import br.com.pirilampo.core.Common;
import br.com.pirilampo.core.dto.ParametroDto;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class ParseDocumentTest extends Common {
    @Test
    public void build() throws Exception {
        val parametro = new ParametroDto();
        parametro.setTxtSrcFonte(featureFile);
        ParseDocument parseDocument = new ParseDocument(parametro, featureFile);

        File tmpFile = File.createTempFile("result-", ".html");
        log.info("Created: {}", tmpFile);

        try (
                FileOutputStream fos = new FileOutputStream(tmpFile, true);
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8));
                PrintWriter out = new PrintWriter(bw);
        ){
            parseDocument.build(out);
        }
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
    public void setIndiceValue(String raw, String expected){
        val parametro = new ParametroDto();
        parametro.setTxtSrcFonte(featureFolder);
        ParseDocument parseDocument = new ParseDocument(parametro, featureFile);

        assertEquals(expected, parseDocument.putIndexValue(raw));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "![Image](https://picsum.photos/800/200)$<br/><p><img src=\"https://picsum.photos/800/200\"  alt=\"Image\" //></p>",
            "<img src=\"smallest.png\">$<br/><p><img src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABAQAAAAA3bvkkAAAACklEQVR4AWNgAAAAAgABc3UBGAAAAABJRU5ErkJggg==\" /></p>",
            "<img src=\"NMdSnfAaxO.png\">$<br/><p><img src=\"NMdSnfAaxO.png\" /></p>",
            "Link Google: [Google](https://www.google.com.br)$Link Google: <a href=\"https://www.google.com.br\">Google</a>",
            "Link Html Embeded: [Link Embeded](KEqOGcTrgn.html)$Link Html Embeded: <a href=\"KEqOGcTrgn.html\">Link Embeded</a>",
            "Link Html Embeded: [Link Embeded](html_embed.html)$Link Html Embeded: <a href=\"#/html/html_embed.html\">Link Embeded</a>",
            "zztjynblb[DEF003](#/scenario/VVlUTIBDZa/1)$zztjynblb<a href=\"#/scenario/VVlUTIBDZa/1\">DEF003</a>",
            "  jZErZDIoaI Rlewk$jZErZDIoaI Rlewk",
            "<img src=\"smallest.png\" width=\"50\">$<br/><p><img src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABAQAAAAA3bvkkAAAACklEQVR4AWNgAAAAAgABc3UBGAAAAABJRU5ErkJggg==\"  width=\"50\"/></p>",
            "<img src=\"NMdSnfAaxO.png\" width=\"50\">$<br/><p><img src=\"NMdSnfAaxO.png\"  width=\"50\"/></p>",
            "<strike>strike<br>strike</strike>$<strike>strike<br/>strike</strike>",
            "- YYY_MASTER_YYY$'<ul>\n<li>YYY_MASTER_YYY</li>\n</ul>\n'",
            "<user> and <password>$&lt;user&gt; and &lt;password&gt;",
            "<img src=\"NMdSnfAaxO.png\"/>$<br/><p><img src=\"NMdSnfAaxO.png\" //></p>",
            "a: <img src=\"xx_a.png\"> b: <img src=\"xx_b.png\">$a: <br/><p><img src=\"xx_a.png\" /></p> b: <br/><p><img src=\"xx_b.png\" /></p>",
            "a: ![ImageA](xx_a.png) b: ![ImageB](xx_b.png)$a: <br/><p><img src=\"xx_a.png\"  alt=\"ImageA\" //></p> b: <br/><p><img src=\"xx_b.png\"  alt=\"ImageB\" //></p>",
            "a: [xx_a](https://xx_a.com) b: [xx_b](https://xx_b.com)$a: <a href=\"https://xx_a.com\">xx_a</a> b: <a href=\"https://xx_b.com\">xx_b</a>",
            "a: [xx_a](xx_a.html) b: [xx_b](xx_b.html)$a: <a href=\"xx_a.html\">xx_a</a> b: <a href=\"xx_b.html\">xx_b</a>",
            "'aaa \n bbb ![ImageA](xx_a.png)'$'aaa\nbbb <br/><p><img src=\"xx_a.png\"  alt=\"ImageA\" //></p>'",
    }, delimiter = '$', ignoreLeadingAndTrailingWhitespace = false)
    public void format(String raw, String expected){
        val parametro = new ParametroDto();
        parametro.setTxtSrcFonte(featureFolder);

        ParseDocument parseDocument = new ParseDocument(parametro, featureFile);

        assertEquals(expected, parseDocument.format(raw, true));
    }
}
