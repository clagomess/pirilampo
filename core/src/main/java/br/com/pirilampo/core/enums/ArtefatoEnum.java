package br.com.pirilampo.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ArtefatoEnum {
    HTML("HTML"), PDF("PDF");

    private final String value;
}
