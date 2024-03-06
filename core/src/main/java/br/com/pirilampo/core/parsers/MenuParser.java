package br.com.pirilampo.core.parsers;

import br.com.pirilampo.core.compilers.Compiler;
import br.com.pirilampo.core.dto.MenuDto;
import br.com.pirilampo.core.dto.ParametersDto;
import br.com.pirilampo.core.enums.DiffEnum;
import lombok.Getter;

import java.io.File;
import java.util.Optional;
import java.util.Set;

public class MenuParser extends Compiler {
    @Getter
    private final MenuDto menu;
    private final ParametersDto parameters;

    public MenuParser(ParametersDto parameters){
        this.menu = new MenuDto("ROOT");
        this.parameters = parameters;
    }

    public void addMenuItem(File feature, DiffEnum diff, String featureTitulo){
        MenuDto item = new MenuDto(
                featureTitulo,
                getFeatureMetadata(parameters, feature).getId(),
                diff
        );

        String[] nodes = getFeaturePathWithoutAbsolute(parameters.getProjectSource(), feature).split("(\\\\|/)");

        walker(menu.getChildren(), nodes, 0, item);
    }

    private void walker(Set<MenuDto> children, String[] nodes, final int level, MenuDto itemToAdd){
        Optional<MenuDto> child = children.stream()
                .filter(item -> item.getTitle().equals(nodes[level]))
                .findFirst();

        if(child.isPresent()){
            if(level == nodes.length - 1){
                children.remove(child.get());
                children.add(itemToAdd);
            }else{
                walker(child.get().getChildren(), nodes, level +1, itemToAdd);
            }
        }else{
            children.add(new MenuDto(nodes[level]));
            walker(children, nodes, level, itemToAdd);
        }
    }
}
