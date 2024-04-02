package com.github.clagomess.pirilampo.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DiffEnum {
    NOT_COMPARED,
    EQUAL,
    DIFFERENT,
    NEW,
}
