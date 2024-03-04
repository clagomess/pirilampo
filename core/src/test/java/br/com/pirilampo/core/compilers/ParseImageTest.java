package br.com.pirilampo.core.compilers;

import br.com.pirilampo.core.dto.ParametroDto;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class ParseImageTest {
    private final ParseImage parseImage = new ParseImage();

    @ParameterizedTest
    @CsvSource(value = {
            "true$smallest.png$data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABAQAAAAA3bvkkAAAACklEQVR4AWNgAAAAAgABc3UBGAAAAABJRU5ErkJggg==",
            "false$smallest.png$../feature/smallest.png",
            "true$xx_a.png$xx_a.png",
            "false$xx_a.png$xx_a.png",
            "true$https://picsum.photos/800/200$https://picsum.photos/800/200",
            "false$https://picsum.photos/800/200$https://picsum.photos/800/200",
    }, delimiter = '$')
    public void parse(Boolean embedded, String filename, String expected){
        val parametro = new ParametroDto();
        parametro.setSitEmbedarImagens(embedded);
        parametro.setTxtSrcFonte(new File(Thread.currentThread()
                .getContextClassLoader()
                .getResource("feature").getFile()));

        File feature = new File(Thread.currentThread()
                .getContextClassLoader()
                .getResource("feature/xxx.Feature").getFile());

        assertEquals(expected, parseImage.parse(parametro, feature, filename));
    }
}
