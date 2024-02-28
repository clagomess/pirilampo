package br.com.pirilampo.core.exception;

import lombok.Getter;

import java.io.File;

public class FeatureException extends Exception {
    @Getter
    private File feature;

    public FeatureException(Throwable cause, File feature) {
        super(cause);
        this.feature = feature;
    }
}
