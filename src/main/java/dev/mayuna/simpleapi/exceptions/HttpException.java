package dev.mayuna.simpleapi.exceptions;

import dev.mayuna.simpleapi.HttpError;
import lombok.Getter;

public class HttpException extends RuntimeException {

    private final @Getter HttpError httpError;

    public HttpException(HttpError httpError) {
        super("During requesting, HTTP Error occurred! Code: " + httpError.getCode(), httpError.getException());
        this.httpError = httpError;
    }
}
