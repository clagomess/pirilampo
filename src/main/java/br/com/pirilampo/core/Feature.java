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
        htmlFeatureRoot = StringUtils.isEmpty(htmlFeatureRoot) ? parametro.getTxtNomeMenuRaiz() : htmlFeatureRoot;

        return htmlFeatureRoot;
    }

    public static String idHtml(Parametro parametro, File feature){
        return root(parametro, feature) + "_" + feature.getName().replace(Resource.getExtension(feature), ".html");
    }

    public static String idFeature(Parametro parametro, File feature){
        return root(parametro, feature) + "_" + feature.getName().replace(Resource.getExtension(feature), ".feature");
    }
}
