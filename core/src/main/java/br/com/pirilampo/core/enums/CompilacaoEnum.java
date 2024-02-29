package br.com.pirilampo.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CompilacaoEnum {
    PASTA("PASTA"), FEATURE("FEATURE"), DIFF("DIFF");

    private final String value;
}
