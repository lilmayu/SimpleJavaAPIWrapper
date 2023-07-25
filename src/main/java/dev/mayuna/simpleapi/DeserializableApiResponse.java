package dev.mayuna.simpleapi;

import java.net.http.HttpResponse;

/**
 * An API response that can be deserialized. Your class <strong>must have a constructor without any arguments</strong>.
 * @param <T> The type of the wrapped API.
 */
public abstract class DeserializableApiResponse<T extends WrappedApi> extends ApiResponse<T> {

    /**
     * Deserialize the response body into an object of current type.
     * @param apiRequest The API request.
     * @param httpResponse The HTTP response.
     * @return The deserialized object.
     */
    public abstract Object deserialize(ApiRequest<?> apiRequest, HttpResponse<?> httpResponse);
}
