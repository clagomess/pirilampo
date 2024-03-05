package br.com.pirilampo.core.compilers;

import br.com.pirilampo.core.bean.MenuDto;
import br.com.pirilampo.core.dto.ParametroDto;
import br.com.pirilampo.core.enums.DiffEnum;
import lombok.Getter;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.stream.IntStream;

public class ParseMenu extends Compiler {
    @Getter
    private MenuDto menu;
    private ParametroDto parametro;
    private int level;
    private String[] nodes;
    private int htmlNodeNum;

    private String featureId = "ID";
    private String featureName = "TITULO";
    private DiffEnum diff = DiffEnum.NAO_COMPARADO;

    public static final String HTML_MENU_FILHO = "\t\t<li><a href=\"#/feature/%s\">%s%s</a></li>\n";
    public static final String HTML_MENU_PAI = "<li>\n" +
            "\t<a href=\"javascript:;\" data-toggle=\"collapse\" data-target=\"#menu-%s\">%s</a>\n" +
            "\t<ul id=\"menu-%s\" class=\"collapse\">\n%s\t</ul>\n" +
            "</li>\n";
    public static final String HTML_MENU_ICON_DIFF_NOVO = "<span class=\"icon-diff-novo\"></span> ";
    public static final String HTML_MENU_ICON_DIFF_DIFERENTE = "<span class=\"icon-diff-diferente\"></span> ";

    public ParseMenu(ParametroDto parametro){
        this.menu = new MenuDto("ROOT");
        this.parametro = parametro;
    }

    public void addMenuItem(File feature, DiffEnum diff, String featureTitulo){
        final String curDir = parametro.getTxtSrcFonte().getAbsolutePath();
        this.featureId = getFeatureMetadata(parametro, feature).getId();
        this.featureName = featureTitulo;
        this.diff = diff;

        String item = feature.getAbsolutePath().replace(curDir, "");
        item = item.replaceFirst("^[\\/|\\\\]", "");
        nodes = item.split("(\\\\|/)");
        level = 0;

        walker(menu.getChildren());
    }

    public String getHtml(){
        htmlNodeNum = 0;

        return getHtml(menu).toString();
    }

    private StringBuilder getHtml(MenuDto node){
        StringBuilder buffer = new StringBuilder();

        if(node.getChildren().isEmpty()){
            buffer.append(String.format(
                    HTML_MENU_FILHO,
                    node.getUrl(),
                    diffIcon(node.getDiff()),
                    node.getTitle()
            ));
        }else {
            for (MenuDto item : node.getChildren()) {
                htmlNodeNum++;

                if(!item.getChildren().isEmpty()) {
                    buffer.append(String.format(
                            HTML_MENU_PAI,
                            htmlNodeNum,
                            item.getTitle(),
                            htmlNodeNum,
                            getHtml(item)
                    ));
                }else{
                    buffer.append(getHtml(item));
                }
            }
        }

        return buffer;
    }

    private void walker(List<MenuDto> node){
        OptionalInt oi = IntStream
                .range(0, node.size())
                .filter(i -> Objects.equals(node.get(i).getTitle(), nodes[level]))
                .findFirst();

        if(oi.isPresent()){
            if(level == nodes.length - 1){
                node.get(oi.getAsInt()).setUrl(this.featureId);
                node.get(oi.getAsInt()).setTitle(this.featureName);
                node.get(oi.getAsInt()).setDiff(this.diff);
            }else{
                level++;
                walker(node.get(oi.getAsInt()).getChildren());
            }
        }else{
            node.add(new MenuDto(nodes[level]));
            walker(node);
        }
    }

    private String diffIcon(DiffEnum diff){
        switch (diff){
            case NOVO:
                return HTML_MENU_ICON_DIFF_NOVO;
            case DIFERENTE:
                return HTML_MENU_ICON_DIFF_DIFERENTE;
            default:
                return "";
        }
    }
}
