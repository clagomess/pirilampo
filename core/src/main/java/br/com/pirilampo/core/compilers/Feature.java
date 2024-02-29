package br.com.pirilampo.core.compilers;

import br.com.pirilampo.core.dto.ParametroDto;

import java.io.File;

public class Feature {

    public static String root(ParametroDto parametro, File feature){
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

    public static String id(ParametroDto parametro, File feature){
        return root(parametro, feature) + "_" + name(feature);
    }

    public static String idHtml(ParametroDto parametro, File feature){
        return id(parametro, feature) + ".html";
    }

    public static String idFeature(ParametroDto parametro, File feature){
        return id(parametro, feature) + ".feature";
    }
}
