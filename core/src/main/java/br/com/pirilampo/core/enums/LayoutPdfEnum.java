package br.com.pirilampo.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LayoutPdfEnum {
    RETRATO("R"), PAISAGEM("P");

    private final String value;
}
