package br.com.pirilampo.core.core;

import br.com.pirilampo.core.bean.Menu;
import br.com.pirilampo.core.dto.ParametroDto;
import br.com.pirilampo.core.enums.DiffEnum;
import br.com.pirilampo.core.constant.HtmlTemplate;
import lombok.Getter;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.stream.IntStream;

public class ParseMenu {
    @Getter
    private Menu menu;
    private ParametroDto parametro;
    private int level;
    private String[] nodes;
    private int htmlNodeNum;

    private String featureId = "ID";
    private String featureName = "TITULO";
    private DiffEnum diff = DiffEnum.NAO_COMPARADO;

    public ParseMenu(ParametroDto parametro){
        this.menu = new Menu("ROOT");
        this.parametro = parametro;
    }

    public void addMenuItem(File feature, DiffEnum diff, String featureTitulo){
        final String curDir = (new File(parametro.getTxtSrcFonte())).getAbsolutePath();
        this.featureId = Feature.id(parametro, feature);
        this.featureName = featureTitulo;
        this.diff = diff;

        String item = feature.getAbsolutePath().replace(curDir, "");
        item = item.replaceFirst("^[\\/|\\\\]", "");
        nodes = item.split("(\\\\|/)");
        level = 0;

        walker(menu.getFilho());
    }

    public String getHtml(){
        htmlNodeNum = 0;

        return getHtml(menu).toString();
    }

    private StringBuilder getHtml(Menu node){
        StringBuilder buffer = new StringBuilder();

        if(node.getFilho().isEmpty()){
            buffer.append(String.format(
                    HtmlTemplate.HTML_MENU_FILHO,
                    node.getUrl(),
                    diffIcon(node.getDiff()),
                    node.getTitulo()
            ));
        }else {
            for (Menu item : node.getFilho()) {
                htmlNodeNum++;

                if(!item.getFilho().isEmpty()) {
                    buffer.append(String.format(
                            HtmlTemplate.HTML_MENU_PAI,
                            htmlNodeNum,
                            item.getTitulo(),
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

    private void walker(List<Menu> node){
        OptionalInt oi = IntStream
                .range(0, node.size())
                .filter(i -> Objects.equals(node.get(i).getTitulo(), nodes[level]))
                .findFirst();

        if(oi.isPresent()){
            if(level == nodes.length - 1){
                node.get(oi.getAsInt()).setUrl(this.featureId);
                node.get(oi.getAsInt()).setTitulo(this.featureName);
                node.get(oi.getAsInt()).setDiff(this.diff);
            }else{
                level++;
                walker(node.get(oi.getAsInt()).getFilho());
            }
        }else{
            node.add(new Menu(nodes[level]));
            walker(node);
        }
    }

    private String diffIcon(DiffEnum diff){
        switch (diff){
            case NOVO:
                return HtmlTemplate.HTML_MENU_ICON_DIFF_NOVO;
            case DIFERENTE:
                return HtmlTemplate.HTML_MENU_ICON_DIFF_DIFERENTE;
            default:
                return "";
        }
    }
}
