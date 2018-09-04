package br.com.pirilampo.core;

import br.com.pirilampo.bean.Parametro;

import java.io.File;
import java.util.*;

public class ParseMenu {
    private static Map<String, Object> menu;
    private static int level;
    private static String[] nodes;

    private static int htmlNodeNum;

    public ParseMenu(){
        menu = new TreeMap<>();
    }

    public void addMenuItem(Parametro parametro, File feature){
        final String htmlFeatureRoot = Feature.root(parametro, feature);
        File curDir = new File(parametro.getTxtSrcFonte());

        if(parametro.getTxtNomeMenuRaiz().equals(htmlFeatureRoot)){
            addMenuItem(
                    htmlFeatureRoot +
                    File.separator +
                    feature.getAbsolutePath().replace(curDir.getAbsolutePath(), "").replace(Resource.getExtension(feature), ".feature")
            );
        }else{
            addMenuItem(feature.getAbsolutePath().replace(curDir.getAbsolutePath().replace(Resource.getExtension(feature), ".feature"), ""));
        }
    }

    public void addMenuItem(String item){
        item = item.replaceFirst("(\\\\|/)", "");
        nodes = item.split("(\\\\|/)");
        level = 0;

        walker(menu);
    }

    public String getHtml(){
        htmlNodeNum = 0;

        return walkerHtml(menu);
    }

    private String walkerHtml(Object node){
        final String HTML_MENU_FILHO = "<li><a href=\"#/feature/%s\">%s</a></li>";
        final String HTML_MENU_PAI = "<li><a href=\"javascript:;\" data-toggle=\"collapse\" data-target=\"#menu-%s\">" +
                "%s</a><ul id=\"menu-%s\" class=\"collapse\">%s</ul></li>";

        StringBuilder buffer = new StringBuilder();

        if(node instanceof Map){
            Map<String, Object> nodeCasted = (Map<String, Object>) node;

            for (Map.Entry<String, Object> entry : nodeCasted.entrySet()) {
                htmlNodeNum++;

                buffer.append(String.format(
                        HTML_MENU_PAI,
                        htmlNodeNum,
                        entry.getKey(),
                        htmlNodeNum,
                        walkerHtml(entry.getValue())
                ));
            }
        }else{
            List<Map<String, String>> nodeCasted = (ArrayList<Map<String, String>>) node;

            for (Map<String, String> item : nodeCasted){
                buffer.append(String.format(
                        HTML_MENU_FILHO,
                        item.get("url"),
                        item.get("name")
                ));
            }
        }

        return buffer.toString();
    }

    private Object walker (Object node){
        if(level == nodes.length){
            return node;
        }

        Object toReturn;

        if(node instanceof Map){
            Map<String, Object> nodeCasted = (Map<String, Object>) node;

            if(!nodeCasted.containsKey(nodes[level])){
                if(level == nodes.length - 2){
                    nodeCasted.put(nodes[level], new ArrayList<String>());
                }else{
                    nodeCasted.put(nodes[level], new TreeMap<String, Object>());
                }
            }

            level ++;
            toReturn = walker(nodeCasted.get(nodes[level - 1]));
        }else{
            List<Map<String, String>> nodeCasted = (List<Map<String, String>>) node;

            String htmlFeatureId = String.join(" ", nodes);
            htmlFeatureId = htmlFeatureId.replace(nodes[nodes.length - 1], "");
            htmlFeatureId = htmlFeatureId.trim();
            htmlFeatureId = htmlFeatureId + "_" + nodes[nodes.length - 1].replace(".feature", "");

            Map<String, String> link = new HashMap<>();
            link.put("url", htmlFeatureId);
            link.put("name", nodes[nodes.length - 1].replace(".feature", ""));

            if(!nodeCasted.contains(link)) {
                nodeCasted.add(link);
            }

            toReturn = nodeCasted;
        }

        return toReturn;
    }

    public Map<String, Object> getMenuMap(){
        return menu;
    }
}
