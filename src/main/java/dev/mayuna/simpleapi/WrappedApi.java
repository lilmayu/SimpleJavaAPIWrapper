package dev.mayuna.simpleapi;

public interface WrappedApi {

    /**
     * Gets the default URL for this API.
     * @return The default URL for this API.
     */
    String getDefaultUrl();

    /**
     * Is called before the request is sent.
     * @param request The request.
     */
    default void onApiRequest(ApiRequest<?> request) {
    }
}
