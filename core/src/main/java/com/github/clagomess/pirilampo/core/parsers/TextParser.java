package com.github.clagomess.pirilampo.core.parsers;

import com.github.clagomess.pirilampo.core.compilers.Compiler;
import com.github.clagomess.pirilampo.core.dto.ParametersDto;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextParser extends Compiler {
    private final MarkdownParser markdownParser = new MarkdownParser();
    private final ImageParser imageParser = new ImageParser();
    private final IndexParser indexParser;

    private final ParametersDto parameters;
    private final File feature;
    private final String featureId;

    @Getter
    private final List<File> paginaHtmlAnexo = new LinkedList<>();

    public TextParser(
            IndexParser indexParser,
            ParametersDto parameters,
            File feature
    ) {
        this.indexParser = indexParser;
        this.parameters = parameters;
        this.feature = feature;
        this.featureId = getFeatureMetadata(parameters, feature).getId();
    }

    public void format(PrintWriter out, String txtRaw, boolean makdown){
        String txt = txtRaw.trim();
        txt = StringUtils.replace(txt, "<", "&lt;");
        txt = StringUtils.replace(txt, ">", "&gt;");

        if(txt.length() < 3){
            out.print(txt);
            return;
        }

        if(makdown) txt = markdownParser.build(txt);

        String img = "<br/><p><img src=\"$1\" $2/></p>";
        txt = txt.replaceAll("<img src=\"(.+?)\"(.*?)>", img);
        txt = txt.replaceAll("&lt;img src=&quot;(.+?)&quot;(.*?)&gt;", img);
        txt = txt.replaceAll("&lt;strike&gt;(.+?)&lt;/strike&gt;", "<strike>$1</strike>");
        txt = StringUtils.replace(txt, "&quot;", "\"");
        txt = StringUtils.replace(txt, "&lt;br&gt;", "<br/>");

        if(indexParser != null) indexParser.putFeaturePhrase(featureId, txt);

        if(txt.length() < 10){
            out.print(txt);
            return;
        }

        txt = findAndReplaceEmbededHtmlHref(txt);

        findAndReplaceImagesSrcAndWriteOut(out, txt);
    }

    protected String findAndReplaceEmbededHtmlHref(String txt){
        Matcher mHtmlHref = Pattern.compile("href=\"(.+?\\.html)\"")
                .matcher(txt);

        while(mHtmlHref.find()) {
            String filename = mHtmlHref.group(1);
            File htmlEmbed = getAbsolutePathFeatureAsset(parameters, feature, filename);

            if (htmlEmbed != null && htmlEmbed.isFile()) {
                paginaHtmlAnexo.add(htmlEmbed);
                txt = txt.replace(mHtmlHref.group(), "href=\"#/html/" + filename + "\"");
            }
        }

        return txt;
    }

    protected void findAndReplaceImagesSrcAndWriteOut(PrintWriter out, String txt){
        Matcher mImgSrc = Pattern.compile("src=\"(.+?)\"")
                .matcher(txt);

        int position = 0;

        while (mImgSrc.find()){
            String group = mImgSrc.group();
            int groupPosition = txt.indexOf(group, position);

            out.write(txt, position, groupPosition - position);
            out.write("src=\"");
            imageParser.parse(out, parameters, feature, mImgSrc.group(1));
            out.write("\"");

            position = groupPosition + group.length();
        }

        out.print(txt.substring(position));
    }
}
