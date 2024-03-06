package com.github.clagomess.pirilampo.core.dto;

import com.github.clagomess.pirilampo.core.enums.DiffEnum;
import lombok.Data;

import java.util.Set;
import java.util.TreeSet;

@Data
public class MenuDto implements Comparable<MenuDto> {
    private final String title;
    private final String url;
    private final DiffEnum diff;
    private final Set<MenuDto> children = new TreeSet<>();

    public MenuDto(String title){
        this.title = title;
        this.url = null;
        this.diff = DiffEnum.NOT_COMPARED;
    }

    public MenuDto(String title, String url, DiffEnum diff) {
        this.title = title;
        this.url = url;
        this.diff = diff;
    }

    @Override
    public int compareTo(MenuDto o) {
        return title.compareTo(o.getTitle());
    }
}
