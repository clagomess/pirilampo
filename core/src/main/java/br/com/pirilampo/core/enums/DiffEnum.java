package br.com.pirilampo.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DiffEnum {
    NAO_COMPARADO(0),
    IGUAL(1),
    DIFERENTE(2),
    NOVO(3);

    private final Integer value;
}
