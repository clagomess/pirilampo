package br.com.pirilampo.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PainelEnum {
    FECHADO("F"), ABERTO("A");

    private final String value;
}
