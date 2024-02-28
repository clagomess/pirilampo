package br.com.pirilampo.constant;

public enum Diff {
    NAO_COMPARADO(0),
    IGUAL(1),
    DIFERENTE(2),
    NOVO(3);

    private final Integer vl;

    Diff(Integer vl){
        this.vl = vl;
    }

    public Integer getValue(){
        return vl;
    }
}
