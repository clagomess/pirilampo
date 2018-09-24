package br.com.pirilampo.constant;

public enum Artefato {
    HTML("HTML"), PDF("PDF");

    private final String vl;

    Artefato(String vl){
        this.vl = vl;
    }

    public String getValue(){
        return vl;
    }
}
