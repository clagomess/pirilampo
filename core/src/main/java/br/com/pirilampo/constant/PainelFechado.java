package br.com.pirilampo.constant;

public enum PainelFechado {
    FECHADO("F"), ABERTO("A");

    private final String vl;

    PainelFechado(String vl){
        this.vl = vl;
    }

    public String getValue(){
        return vl;
    }
}
