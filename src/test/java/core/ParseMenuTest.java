package core;

import br.com.pirilampo.bean.Menu;
import br.com.pirilampo.bean.Parametro;
import br.com.pirilampo.core.ParseMenu;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ParseMenuTest {
    private List<String> itens = new ArrayList<>();

    @Before
    public void before(){
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
        ParseMenu pm = new ParseMenu(new Parametro());

        for(String item : itens){
            pm.addMenuItem(item);
        }

        log.info("{}", pm.getMenu());
    }

    @Test
    public void testAddMenuItem(){
        ParseMenu pm = new ParseMenu(new Parametro());
        pm.addMenuItem("Features\\\\001.feature");
        pm.addMenuItem("\\bar\\002.feature");
        pm.addMenuItem("\\bar\\foo\\003.feature");

        Menu menu = pm.getMenu();

        log.info("MENU: {}", menu);

        Assert.assertEquals("Features", menu.getFilho().get(0).getTitulo());
        Assert.assertEquals("bar", menu.getFilho().get(1).getTitulo());
        Assert.assertEquals("foo", menu.getFilho().get(1).getFilho().get(1).getTitulo());
    }
}
