package br.com.pirilampo.core.enums;

public enum CompilacaoEnum {
    PASTA("PASTA"), FEATURE("FEATURE"), DIFF("DIFF");

    private final String vl;

    CompilacaoEnum(String vl){
        this.vl = vl;
    }

    public String getValue(){
        return vl;
    }
}
