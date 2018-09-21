package br.com.pirilampo.core;

import br.com.pirilampo.bean.Menu;
import br.com.pirilampo.bean.Parametro;
import br.com.pirilampo.constant.HtmlTemplate;
import lombok.Getter;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.stream.IntStream;

public class ParseMenu {
    @Getter
    private Menu menu;
    private Parametro parametro;
    private int level;
    private String[] nodes;
    private int htmlNodeNum;

    private String featureId = "ID";
    private String featureName = "TITULO";

    public ParseMenu(Parametro parametro){
        this.menu = new Menu(parametro.getTxtNomeMenuRaiz());
        this.parametro = parametro;
    }

    public void addMenuItem(String item){
        item = item.replaceFirst("^[\\/|\\\\]", "");
        nodes = item.split("(\\\\|/)");
        level = 0;

        walker(menu.getFilho());
    }

    void addMenuItem(File feature){
        final String curDir = (new File(parametro.getTxtSrcFonte())).getAbsolutePath();
        this.featureId = Feature.id(parametro, feature);
        this.featureName = Feature.name(feature);

        addMenuItem(feature.getAbsolutePath().replace(curDir, ""));
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
            }else{
                level++;
                walker(node.get(oi.getAsInt()).getFilho());
            }
        }else{
            node.add(new Menu(nodes[level]));
            walker(node);
        }
    }
}
