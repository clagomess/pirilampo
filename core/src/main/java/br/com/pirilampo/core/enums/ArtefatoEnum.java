package br.com.pirilampo.core.enums;

public enum ArtefatoEnum {
    HTML("HTML"), PDF("PDF");

    private final String vl;

    ArtefatoEnum(String vl){
        this.vl = vl;
    }

    public String getValue(){
        return vl;
    }
}
