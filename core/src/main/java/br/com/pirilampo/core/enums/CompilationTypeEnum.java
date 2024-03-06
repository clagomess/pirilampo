package br.com.pirilampo.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CompilationTypeEnum {
    FOLDER,
    FOLDER_DIFF,
    FEATURE,
}
