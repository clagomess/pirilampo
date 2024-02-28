package br.com.pirilampo.core.bean;

import lombok.Data;

import java.util.LinkedHashSet;

@Data
public class Indice {
    private String name = null;
    private LinkedHashSet<String> values = new LinkedHashSet<>();
}
