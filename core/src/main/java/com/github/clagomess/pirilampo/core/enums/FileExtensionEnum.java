package com.github.clagomess.pirilampo.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

@Getter
@RequiredArgsConstructor
public enum FileExtensionEnum {
    FEATURE(
            Collections.singletonList(".feature"),
            Pattern.compile("\\.feature$", Pattern.CASE_INSENSITIVE)
    ),
    IMAGE(
            Arrays.asList(".jpg", ".jpeg", ".png"),
            Pattern.compile("\\.(jpg|jpeg|png)$", Pattern.CASE_INSENSITIVE)
    );

    private final List<String> extensions;
    private final Pattern pattern;
}
