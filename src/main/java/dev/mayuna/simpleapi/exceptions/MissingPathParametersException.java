package dev.mayuna.simpleapi.exceptions;

import dev.mayuna.simpleapi.APIRequest;
import lombok.Getter;

public class MissingPathParametersException extends RuntimeException {

    private final @Getter String url;
    private final @Getter APIRequest apiRequest;

    public MissingPathParametersException(String url, APIRequest apiRequest) {
        super("Missing path parameters: " + url);

        this.url = url;
        this.apiRequest = apiRequest;
    }
}
