package br.com.pirilampo;

import gherkin.*;
import gherkin.ast.GherkinDocument;
import gherkin.deps.com.google.gson.Gson;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public class Main {
    public static void main(String[] args) throws IOException {
        Gson gson = new Gson();
        Parser<GherkinDocument> parser = new Parser<>(new AstBuilder());
        TokenMatcher matcher = new TokenMatcher();

        long startTime = System.currentTimeMillis();

        Reader in = new InputStreamReader(new FileInputStream("C:\\_DESENV\\gherkin-java\\EPE001.feature"), "UTF-8");
        try {
            GherkinDocument gherkinDocument = parser.parse(in, matcher);
            Stdio.out.println(gson.toJson(gherkinDocument));
        } catch (ParserException e) {
            Stdio.err.println(e.getMessage());
            System.exit(1);
        }

        long endTime = System.currentTimeMillis();
        if(System.getenv("GHERKIN_PERF") != null) {
            Stdio.err.println(endTime - startTime);
        }
    }
}
