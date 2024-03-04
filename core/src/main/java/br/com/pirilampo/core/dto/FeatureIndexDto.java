package br.com.pirilampo.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class FeatureIndexDto {
    private final String name;
    private final Set<String> values;
}
