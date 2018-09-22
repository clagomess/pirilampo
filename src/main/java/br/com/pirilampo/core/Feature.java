package br.com.pirilampo.core;

import br.com.pirilampo.bean.Parametro;
import org.apache.commons.lang.StringUtils;

import java.io.File;

public class Feature {

    public static String root(Parametro parametro, File feature){
        File curDir = new File(parametro.getTxtSrcFonte());

        String htmlFeatureRoot = feature.getAbsolutePath().replace(curDir.getAbsolutePath(), "");
        htmlFeatureRoot = htmlFeatureRoot.replace(feature.getName(), "");
        htmlFeatureRoot = htmlFeatureRoot.replace(File.separator, " ");
        htmlFeatureRoot = htmlFeatureRoot.trim();

        return htmlFeatureRoot;
    }

    public static String name(File feature){
        return feature.getName().replace(Resource.getExtension(feature), "");
    }

    public static String id(Parametro parametro, File feature){
        return root(parametro, feature) + "_" + name(feature);
    }

    public static String idHtml(Parametro parametro, File feature){
        return id(parametro, feature) + ".html";
    }

    public static String idFeature(Parametro parametro, File feature){
        return id(parametro, feature) + ".feature";
    }
}
