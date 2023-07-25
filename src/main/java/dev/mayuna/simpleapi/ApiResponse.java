package dev.mayuna.simpleapi;

import lombok.Getter;
import lombok.Setter;

/**
 * An abstract class to represent an API response. Your class <strong>must have a constructor without any arguments</strong>.
 * @param <T> The type of the wrapped API.
 */
public abstract class ApiResponse<T extends WrappedApi> {

    private transient @Getter @Setter int httpStatusCode = -1;
    private transient @Getter @Setter T wrappedApi = null;

    public ApiResponse() {
    }
}
