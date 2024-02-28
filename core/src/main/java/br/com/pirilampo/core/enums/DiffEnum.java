package br.com.pirilampo.core.enums;

public enum DiffEnum {
    NAO_COMPARADO(0),
    IGUAL(1),
    DIFERENTE(2),
    NOVO(3);

    private final Integer vl;

    DiffEnum(Integer vl){
        this.vl = vl;
    }

    public Integer getValue(){
        return vl;
    }
}
