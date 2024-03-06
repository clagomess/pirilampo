package com.github.clagomess.pirilampo.core.exception;

public class ParametersException extends Exception {
    public ParametersException(String message) {
        super(message);
    }

    public static ParametersException required(String optionName){
        return new ParametersException(String.format(
            "Option <%s> is required",
            optionName
        ));
    }
}
