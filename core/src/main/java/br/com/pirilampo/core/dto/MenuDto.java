package br.com.pirilampo.core.dto;

import br.com.pirilampo.core.enums.DiffEnum;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class MenuDto implements Comparable<MenuDto> {
    private String title;
    private String url = null;
    private DiffEnum diff = DiffEnum.NAO_COMPARADO;
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
