package br.com.pirilampo.core.bean;

import br.com.pirilampo.core.enums.DiffEnum;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class MenuDto implements Comparable<MenuDto> {
    private String title;

    @ToString.Exclude
    private String url = null;

    @ToString.Exclude
    private DiffEnum diff = DiffEnum.NAO_COMPARADO;

    @ToString.Exclude
    private List<MenuDto> children;

    public MenuDto(String title){
        this.title = title;
        this.children = new ArrayList<>();
    }

    public List<MenuDto> getChildren(){
        Collections.sort(this.children);

        return this.children;
    }

    @Override
    public int compareTo(MenuDto o) {
        return title.compareTo(o.getTitle());
    }
}
