package br.com.pirilampo.core.compilers;

import br.com.pirilampo.core.dto.ParametroDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ParseMenuTest {
    private List<String> itens = new ArrayList<>();
    private ParametroDto parametro = new ParametroDto();

    @BeforeEach
    public void before(){
        parametro.setTxtSrcFonte(new File(""));

        itens.add("01 - Módulo I - Portal Público\\Login\\00_Login\\MDIC_US00_02 - Logout.feature");
        itens.add("01 - Módulo I - Portal Público\\Login\\00_Login\\MDIC_US00_01 - Login.feature");
        itens.add("01 - Módulo I - Portal Público\\Login\\01_Dashboard\\MDIC_US01_02 - Visualizar_Dashboard_Demais_Perfis.feature");
        itens.add("01 - Módulo I - Portal Público\\Login\\01_Dashboard\\MDIC_US01_01 - Visualizar_Dashboard_Administrador.feature");
        itens.add("01 - Módulo I - Portal Público\\Notícias\\02_Solicitar_Noticias\\MDIC_US02_01 - Cadastrar_solicitacao_de_noticia.feature");
        itens.add("01 - Módulo I - Portal Público\\Notícias\\02_Solicitar_Noticias\\MDIC_US02_02 - Editar_solicitacao_de_noticia.feature");
        itens.add("01 - Módulo I - Portal Público\\Notícias\\03_Moderar_Noticias\\MDIC_US03_02 - Visualizar_noticia_recebida.feature");
        itens.add("01 - Módulo I - Portal Público\\Notícias\\03_Moderar_Noticias\\MDIC_US03_01 - Editar_noticia_recebida.feature");
        itens.add("01 - Módulo I - Portal Público\\Notícias\\03_Moderar_Noticias\\MDIC_US03_11 - Regras_de_interface_moderar_noticias.feature");
        itens.add("01 - Módulo I - Portal Público\\Parametrizar Informações\\04_Parametrizar_Menus\\MDIC_US04_01 - Cadastrar_Menu.feature");
        itens.add("01 - Módulo I - Portal Público\\Parametrizar Informações\\04_Parametrizar_Menus\\MDIC_US04_02 - Editar_Menu.feature");
        itens.add("01 - Módulo I - Portal Público\\Parametrizar Informações\\05_Parametrizar_Páginas\\MDIC_US05_01 - Cadastrar_Página_Menu.feature");
        itens.add("01 - Módulo I - Portal Público\\Parametrizar Informações\\05_Parametrizar_Páginas\\MDIC_US05_02 - Cadastrar_Página_Modulo.feature");
        itens.add("01 - Módulo I - Portal Público\\Parametrizar Informações\\07_Parametrizar_Home_Estaduais\\MDIC_US07_01 - Cadastrar_Conteúdo_Home_Estadual.feature");
        itens.add("01 - Módulo I - Portal Público\\Parametrizar Informações\\07_Parametrizar_Home_Estaduais\\MDIC_US07_02 - Editar_Conteúdo_Home_Estadual.feature");
        itens.add("Mensagem\\PAB-Emails.feature");
        itens.add("Mensagem\\PAB-Mensagens.feature");
        itens.add("Mensagem\\PAB-Termos.feature");
        itens.add("Regras_de_negocio\\US01-PAB-Regras_de_negocio.feature");
    }

    @Test
    public void walker(){
        ParseMenu pm = new ParseMenu(parametro);

        for(String item : itens){
            File f = new File(item);
            // pm.addMenuItem(f, DiffEnum.NAO_COMPARADO, Feature.name(f)); //@TODO: check
        }

        log.info("{}", pm.getMenu());
    }
}
