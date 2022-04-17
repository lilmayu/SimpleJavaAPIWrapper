package dev.mayuna.simpleapi.exceptions;

import dev.mayuna.simpleapi.APIRequest;
import lombok.Getter;

/**
 * This exception is thrown if you've forgotten to replace some path parameters in endpoint path
 */
public class MissingPathParametersException extends RuntimeException {

    private final @Getter String url;
    private final @Getter APIRequest apiRequest;

    public MissingPathParametersException(String url, APIRequest apiRequest) {
        super("Missing path parameters: " + url);

        this.url = url;
        this.apiRequest = apiRequest;
    }
}
