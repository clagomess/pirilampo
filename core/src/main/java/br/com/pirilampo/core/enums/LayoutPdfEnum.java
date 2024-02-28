package br.com.pirilampo.core.enums;

public enum LayoutPdfEnum {
    RETRATO("R"), PAISAGEM("P");

    private final String vl;

    LayoutPdfEnum(String vl){
        this.vl = vl;
    }

    public String getValue(){
        return vl;
    }
}
