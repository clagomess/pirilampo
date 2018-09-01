package br.com.pirilampo.constant;

public enum Compilacao {
    PASTA("PASTA"), FEATURE("FEATURE"), DIFF("DIFF");

    private final String vl;

    Compilacao(String vl){
        this.vl = vl;
    }

    public String getValue(){
        return vl;
    }
}
