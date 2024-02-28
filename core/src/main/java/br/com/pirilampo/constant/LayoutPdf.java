package br.com.pirilampo.constant;

public enum LayoutPdf {
    RETRATO("R"), PAISAGEM("P");

    private final String vl;

    LayoutPdf(String vl){
        this.vl = vl;
    }

    public String getValue(){
        return vl;
    }
}
