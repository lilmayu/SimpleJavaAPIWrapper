package dev.mayuna.simpleapi;

import com.google.gson.Gson;

import java.net.http.HttpResponse;

/**
 * Implements {@link DeserializableApiResponse} using {@link Gson} library. Your class <strong>must have a constructor without any arguments</strong>.
 * @param <T> The type of the API.
 */
public abstract class GsonApiResponse<T extends WrappedApi> extends DeserializableApiResponse<T> {

    /**
     * Get a Gson instance.
     * @return A Gson instance.
     */
    public Gson getGson() {
        return new Gson();
    }

    @Override
    public Object deserialize(ApiRequest<?> apiRequest, HttpResponse<?> httpResponse) {
        Object responseBody = httpResponse.body();

        if (!(responseBody instanceof String)) {
            throw new IllegalArgumentException("Response body must be a string, currently is: " + responseBody.getClass());
        }

        return getGson().fromJson((String) responseBody, getClass());
    }
}
