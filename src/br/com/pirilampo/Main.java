package br.com.pirilampo;

import gherkin.*;
import gherkin.ast.GherkinDocument;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public class Main {
    public static void main(String[] args) throws IOException {
        Parser<GherkinDocument> parser = new Parser<>(new AstBuilder());
        TokenMatcher matcher = new TokenMatcher();

        Reader in = new InputStreamReader(new FileInputStream("C:\\Users\\02757713183\\Desktop\\DEMANDAS\\FNDE-1785 SICE\\EPE\\Feature\\FNDE_EPE002_Emitir relatório de dados do Cursista\\EPE001_Dados_Dos_Cursistas.feature"), "UTF-8");
        try {
            GherkinDocument gherkinDocument = parser.parse(in, matcher);

            if(gherkinDocument != null){
                ParseDocument pd = new ParseDocument(gherkinDocument);
                System.out.println(pd.getHtml());
            }
        } catch (ParserException e) {
            e.printStackTrace();
        }
    }
}
