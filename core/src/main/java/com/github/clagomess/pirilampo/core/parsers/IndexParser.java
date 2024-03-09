package com.github.clagomess.pirilampo.core.parsers;

import com.github.clagomess.pirilampo.core.compilers.Compiler;
import com.github.clagomess.pirilampo.core.dto.FeatureIndexDto;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.TreeMap;

public class IndexParser extends Compiler {
    protected int idx = -1;
    protected final Map<String, Integer> phrases = new TreeMap<>();
    protected final Map<Integer, FeatureIndexDto> index = new TreeMap<>();

    protected int getPhraseId(String phrase){
        if(phrases.containsKey(phrase)){
            return phrases.get(phrase);
        }else{
            phrases.put(phrase, ++idx);
            return idx;
        }
    }

    public void setFeatureTitle(String featureId, String title){
        int featureIdIdx = getPhraseId(featureId);
        int featureTitle = getPhraseId(title);

        if(index.putIfAbsent(featureIdIdx, new FeatureIndexDto(featureTitle)) != null) {
            index.get(featureIdIdx).setTitle(featureTitle);
        }
    }

    protected String parseRawPhrase(String rawPhrase){
        if(StringUtils.isBlank(rawPhrase) || rawPhrase.length() <= 3) return null;

        rawPhrase = rawPhrase
                .replaceAll("<.+?>", "")
                .replace("&lt;", "")
                .replace("&gt;", "");
        rawPhrase = StringUtils.trimToNull(rawPhrase);

        if(StringUtils.isBlank(rawPhrase) || rawPhrase.length() <= 3){
            return null;
        }else{
            return rawPhrase;
        }
    }

    public void putFeaturePhrase(String featureId, String rawPhrase){
        String phrase = parseRawPhrase(rawPhrase);
        if(phrase == null) return;

        int featureIdIdx = getPhraseId(featureId);
        int rawPhraseIdx = getPhraseId(phrase);

        index.putIfAbsent(featureIdIdx, new FeatureIndexDto());
        index.get(featureIdIdx).getPhrases().add(rawPhraseIdx);
    }

    public void buildIndex(PrintWriter out) throws IOException {
        out.print("let indexPhrases = ");
        mapper.writeValue(out, phrases);
        out.println(";");

        out.print("let indexMap = ");
        mapper.writeValue(out, index);
        out.println(";");
    }
}
