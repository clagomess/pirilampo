package br.com.pirilampo.core.enums;

public enum PainelEnum {
    FECHADO("F"), ABERTO("A");

    private final String vl;

    PainelEnum(String vl){
        this.vl = vl;
    }

    public String getValue(){
        return vl;
    }
}
