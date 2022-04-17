package dev.mayuna.simpleapi;

import lombok.Getter;

public class HttpError {

    private final @Getter int code;
    private final @Getter Exception exception;

    /**
     * HTTP error with its response code and {@link Exception}
     * @param code HTTP response code
     * @param exception {@link Exception}
     */
    public HttpError(int code, Exception exception) {
        this.code = code;
        this.exception = exception;
    }
}
