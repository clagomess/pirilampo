package com.github.clagomess.pirilampo.core.parsers;

import com.github.clagomess.pirilampo.core.Common;
import com.github.clagomess.pirilampo.core.dto.MenuDto;
import com.github.clagomess.pirilampo.core.dto.ParametersDto;
import com.github.clagomess.pirilampo.core.enums.DiffEnum;
import com.github.clagomess.pirilampo.core.parsers.MenuParser;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class MenuParserTest extends Common {
    private final List<File> itens = Arrays.asList(
            new File("feature/01 - Módulo I - Portal Público/Login/00_Login/MDIC_US00_02 - Logout.feature"),
            new File("feature/01 - Módulo I - Portal Público/Login/00_Login/MDIC_US00_01 - Login.feature"),
            new File("feature/01 - Módulo I - Portal Público/Login/01_Dashboard/MDIC_US01_02 - Visualizar_Dashboard_Demais_Perfis.feature"),
            new File("feature/01 - Módulo I - Portal Público/Login/01_Dashboard/MDIC_US01_01 - Visualizar_Dashboard_Administrador.feature"),
            new File("feature/01 - Módulo I - Portal Público/Notícias/02_Solicitar_Noticias/MDIC_US02_01 - Cadastrar_solicitacao_de_noticia.feature"),
            new File("feature/01 - Módulo I - Portal Público/Notícias/02_Solicitar_Noticias/MDIC_US02_02 - Editar_solicitacao_de_noticia.feature"),
            new File("feature/01 - Módulo I - Portal Público/Notícias/03_Moderar_Noticias/MDIC_US03_02 - Visualizar_noticia_recebida.feature"),
            new File("feature/01 - Módulo I - Portal Público/Notícias/03_Moderar_Noticias/MDIC_US03_01 - Editar_noticia_recebida.feature"),
            new File("feature/01 - Módulo I - Portal Público/Notícias/03_Moderar_Noticias/MDIC_US03_11 - Regras_de_interface_moderar_noticias.feature"),
            new File("feature/01 - Módulo I - Portal Público/Parametrizar Informações/04_Parametrizar_Menus/MDIC_US04_01 - Cadastrar_Menu.feature"),
            new File("feature/01 - Módulo I - Portal Público/Parametrizar Informações/04_Parametrizar_Menus/MDIC_US04_02 - Editar_Menu.feature"),
            new File("feature/01 - Módulo I - Portal Público/Parametrizar Informações/05_Parametrizar_Páginas/MDIC_US05_01 - Cadastrar_Página_Menu.feature"),
            new File("feature/01 - Módulo I - Portal Público/Parametrizar Informações/05_Parametrizar_Páginas/MDIC_US05_02 - Cadastrar_Página_Modulo.feature"),
            new File("feature/01 - Módulo I - Portal Público/Parametrizar Informações/07_Parametrizar_Home_Estaduais/MDIC_US07_01 - Cadastrar_Conteúdo_Home_Estadual.feature"),
            new File("feature/01 - Módulo I - Portal Público/Parametrizar Informações/07_Parametrizar_Home_Estaduais/MDIC_US07_02 - Editar_Conteúdo_Home_Estadual.feature"),
            new File("feature/Mensagem/PAB-Emails.feature"),
            new File("feature/Mensagem/PAB-Mensagens.feature"),
            new File("feature/Mensagem/PAB-Termos.feature"),
            new File("feature/Regras_de_negocio/US01-PAB-Regras_de_negocio.feature"),
            new File("feature/A/A.feature")
    );
    
    private final ParametersDto parameters = new ParametersDto(){{
        setProjectSource(new File("feature"));
    }};

    private final MenuParser menuParser = new MenuParser(parameters){{
        for(File item : itens){
            addMenuItem(item, DiffEnum.NOT_COMPARED, item.getName().replace(".feature", "_x"));
        }
    }};

    @Test
    public void walker_root(){
        Assertions.assertThat(menuParser.getMenu().getChildren().size()).isGreaterThan(0);
        assertEquals("ROOT", menuParser.getMenu().getTitle());
        assertNull(menuParser.getMenu().getUrl());
    }

    @Test
    public void walker_level_0(){
        Assertions.assertThat(
                menuParser.getMenu().getChildren().stream()
                        .map(MenuDto::getTitle)
                        .collect(Collectors.toList())
        ).containsExactly(
                "01 - Módulo I - Portal Público",
                "A",
                "Mensagem",
                "Regras_de_negocio"
        );
    }

    @Test
    public void walker_level_1_end(){
        Optional<MenuDto> level = menuParser.getMenu().getChildren().stream()
                .filter(item -> item.getTitle().equals("Mensagem"))
                .findFirst();
        assertTrue(level.isPresent());

        Assertions.assertThat(
                level.get().getChildren().stream()
                        .map(MenuDto::getTitle)
                        .collect(Collectors.toList())
        ).containsExactly(
                "PAB-Emails_x",
                "PAB-Mensagens_x",
                "PAB-Termos_x"
        );

        assertTrue(
                level.get().getChildren().stream()
                        .allMatch(item -> item.getChildren().isEmpty())
        );
    }

    @Test
    public void walker_level_1(){
        Optional<MenuDto> level = menuParser.getMenu().getChildren().stream()
                .filter(item -> item.getTitle().equals("01 - Módulo I - Portal Público"))
                .findFirst();
        assertTrue(level.isPresent());

        Assertions.assertThat(
                level.get().getChildren().stream()
                        .map(MenuDto::getTitle)
                        .collect(Collectors.toList())
        ).containsExactly(
                "Login",
                "Notícias",
                "Parametrizar Informações"
        );

        assertTrue(
                level.get().getChildren().stream()
                        .noneMatch(item -> item.getChildren().isEmpty())
        );
    }
}
