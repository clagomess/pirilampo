package br.com.pirilampo.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.File;

@Data
@AllArgsConstructor
public class FeatureMasterDto {
    private String path;
    private File feature;
}
