package br.com.pirilampo.core.exception;

import lombok.Getter;

import java.io.File;

@Getter
public class FeatureException extends Exception {
    private final File feature;

    public FeatureException(Throwable cause, File feature) {
        super(cause);
        this.feature = feature;
    }

    public FeatureException(String message, File feature) {
        super(message);
        this.feature = feature;
    }
}
