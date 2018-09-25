package br.com.pirilampo.bean;

import br.com.pirilampo.constant.Diff;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class Menu implements Comparable<Menu> {
    private String titulo;

    @ToString.Exclude
    private String url = null;

    @ToString.Exclude
    private Diff diff = Diff.NAO_COMPARADO;

    @ToString.Exclude
    private List<Menu> filho;

    public Menu(String titulo){
        this.titulo = titulo;
        this.filho = new ArrayList<>();
    }

    public List<Menu> getFilho(){
        Collections.sort(this.filho);

        return this.filho;
    }

    @Override
    public int compareTo(Menu o) {
        return titulo.compareTo(o.getTitulo());
    }
}
