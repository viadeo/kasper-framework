package com.viadeo.kasper.core.ids;

public class FailedToTransformIDException extends RuntimeException {

    public FailedToTransformIDException(String message) {
        super(message);
    }

    public FailedToTransformIDException(String message, Throwable cause) {
        super(message, cause);
    }
}
