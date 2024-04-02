package com.github.clagomess.pirilampo.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeatureIndexDto {
    private Integer title;
    private final Set<Integer> phrases = new LinkedHashSet<>();
}
