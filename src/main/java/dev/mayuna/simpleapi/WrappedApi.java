package dev.mayuna.simpleapi;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public interface WrappedApi {

    /**
     * Gets the default URL for this API.
     *
     * @return The default URL for this API.
     */
    String getDefaultUrl();

    /**
     * Gets the default {@link RequestHeader}s for this API.<br> Useful if you want to set some headers for all requests.
     *
     * @return Nullable array of {@link RequestHeader}s.
     */
    default RequestHeader[] getDefaultRequestHeaders() {
        return null;
    }

    /**
     * Computers the endpoint of this {@link ApiRequest}.<br>If the {@link ApiRequest#getComputedEndpoint()} is overridden, this method may not be
     * called, depending on the implementation.
     *
     * @param apiRequest The {@link ApiRequest} to compute the endpoint of.
     * @param <T>        The type of the response.
     *
     * @return The computed endpoint of this {@link ApiRequest} with all {@link PathParameter}s replaces and {@link RequestQuery}s added.
     */
    default <T> String computeEndpoint(ApiRequest<T> apiRequest) {
        String computedEndpoint = apiRequest.getEndpoint();

        PathParameter[] pathParameters = apiRequest.getPathParameters();
        if (pathParameters != null && pathParameters.length > 0) {
            for (PathParameter pathParameter : apiRequest.getPathParameters()) {
                //noinspection DataFlowIssue
                computedEndpoint = computedEndpoint.replace("{" + pathParameter.getId() + "}", pathParameter.getReplacement());
            }
        }

        RequestQuery[] requestQueries = apiRequest.getRequestQueries();

        if (requestQueries != null && requestQueries.length > 0) {
            for (int i = 0; i < requestQueries.length; i++) {
                RequestQuery requestQuery = requestQueries[i];

                String querySymbol = "&";

                if (i == 0) {
                    querySymbol = "?";
                }

                computedEndpoint += querySymbol + requestQuery.getName() + "=" + requestQuery.getValue();
            }
        }

        return computedEndpoint;
    }

    /**
     * Applies the {@link RequestHeader}s to the given {@link HttpRequest.Builder}.<br>If the
     * {@link ApiRequest#applyHeadersToHttpRequestBuilder(HttpRequest.Builder, RequestHeader[])} is overridden, this method may not be called,
     * depending on the implementation.
     *
     * @param apiRequest         The {@link ApiRequest} to apply the {@link RequestHeader}s to.
     * @param httpRequestBuilder The {@link HttpRequest.Builder} to apply the {@link RequestHeader}s to.
     * @param requestHeaders     The {@link RequestHeader}s to apply.
     * @param <T>                The type of the response.
     */
    default <T> void applyHeadersToHttpRequestBuilder(ApiRequest<T> apiRequest, HttpRequest.Builder httpRequestBuilder, RequestHeader[] requestHeaders) {
        if (requestHeaders != null) {
            for (RequestHeader requestHeader : requestHeaders) {
                httpRequestBuilder.header(requestHeader.getKey(), requestHeader.getValue());
            }
        }

        RequestHeader[] defaultRequestHeaders = apiRequest.getWrappedApi().getDefaultRequestHeaders();

        if (defaultRequestHeaders != null) {
            for (RequestHeader defaultRequestHeader : defaultRequestHeaders) {
                httpRequestBuilder.header(defaultRequestHeader.getKey(), defaultRequestHeader.getValue());
            }
        }
    }

    /**
     * Creates a new {@link HttpClient} instance that will be used for sending requests.<br>If the {@link ApiRequest#createHttpClientInstance()} is
     * overridden, this method may not be called, depending on the implementation.
     *
     * @return The created {@link HttpClient} instance.
     */
    default HttpClient createHttpClientInstance() {
        return HttpClient.newBuilder().build();
    }

    /**
     * Creates a new {@link HttpRequest.Builder} instance that will be used for building requests.<br>If the
     * {@link ApiRequest#createHttpRequestBuilderInstance()} is overridden, this method may not be called, depending on the implementation.
     *
     * @return The created {@link HttpRequest.Builder} instance.
     */
    default HttpRequest.Builder createHttpRequestBuilderInstance() {
        return HttpRequest.newBuilder();
    }

    /**
     * Gets the default timeout duration for the API requests.
     *
     * @return The default timeout duration for the API requests.
     */
    default Duration getTimeoutDuration() {
        return Duration.ofSeconds(10);
    }

    /**
     * This method is used for async requests. You may override this method to change the way async requests are sent, for example, using a thread
     * pool, etc.
     *
     * @param runnable The runnable to run.
     */
    default void runAsync(Runnable runnable) {
        new Thread(runnable).start();
    }

    /**
     * Creates a new instance of the response class.<br>If the {@link ApiRequest#createInstanceOfResponseClass()} is overridden, this method may not
     * be called, depending on the implementation.
     *
     * @param responseClass The response class to create an instance of.
     * @param <T>           The type of the response.
     *
     * @return The created instance of the response class.
     *
     * @throws NoSuchMethodException     Is thrown if the response class does not have a default constructor.
     * @throws InvocationTargetException Is thrown if the constructor of the response class throws an exception.
     * @throws InstantiationException    Is thrown if the response class is abstract or interface.
     * @throws IllegalAccessException    Is thrown if the constructor of the response class is not accessible.
     */
    default <T> T createInstanceOfResponseClass(Class<T> responseClass) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (responseClass.isArray()) {
            throw new RuntimeException("Cannot create instance of array");
        }

        return responseClass.getDeclaredConstructor().newInstance();
    }

    /**
     * Handles the given {@link HttpResponse}.<br>If the {@link ApiRequest#handleResponse(HttpResponse)} is overridden, this method may not be called,
     * depending on the implementation.
     *
     * @param apiRequest   The {@link ApiRequest} to handle the response for.
     * @param httpResponse The {@link HttpResponse} to handle.
     * @param <T>          The type of the response.
     *
     * @return The instance of the response class.
     *
     * @throws NoSuchMethodException     Is thrown if the response class does not have a default constructor.
     * @throws InvocationTargetException Is thrown if the constructor of the response class throws an exception.
     * @throws InstantiationException    Is thrown if the response class is abstract or interface.
     * @throws IllegalAccessException    Is thrown if the constructor of the response class is not accessible.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    default <T> T handleResponse(ApiRequest<T> apiRequest, HttpResponse<?> httpResponse) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        T responseInstance = apiRequest.createInstanceOfResponseClass();

        if (responseInstance instanceof ApiResponse) {
            ApiResponse apiResponse = (ApiResponse) responseInstance;

            apiResponse.setHttpStatusCode(httpResponse.statusCode());
            apiResponse.setWrappedApi(apiRequest.getWrappedApi());
        }

        if (responseInstance instanceof DeserializableApiResponse) {
            responseInstance = (T) ((DeserializableApiResponse) responseInstance).deserialize(apiRequest, httpResponse);
        }

        return responseInstance;
    }

    /**
     * Sends the request synchronously.<br>If the {@link ApiRequest#send()} is overridden, this method may not be called, depending on the
     * implementation.
     *
     * @param apiRequest The request to send.
     * @param <T>        The type of the response.
     *
     * @return The instance of the response class.
     *
     * @throws IOException               If an I/O error occurs.
     * @throws InterruptedException      If the operation is interrupted.
     * @throws NoSuchMethodException     Is thrown if the response class does not have a default constructor.
     * @throws InvocationTargetException Is thrown if the constructor of the response class throws an exception.
     * @throws InstantiationException    Is thrown if the response class is abstract or interface.
     * @throws IllegalAccessException    Is thrown if the constructor of the response class is not accessible.
     */
    default <T> T send(ApiRequest<T> apiRequest) throws IOException, InterruptedException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        String url = apiRequest.getUrl();

        if (url == null) {
            url = apiRequest.getWrappedApi().getDefaultUrl();
        }

        String requestUrl = url + apiRequest.getComputedEndpoint();

        HttpClient httpClient = createHttpClientInstance();
        HttpRequest.Builder httpRequestBuilder = createHttpRequestBuilderInstance();
        httpRequestBuilder.timeout(getTimeoutDuration());

        try {
            httpRequestBuilder.uri(new URI(requestUrl));
        } catch (IllegalArgumentException | URISyntaxException e) {
            throw new RuntimeException("Invalid URI " + requestUrl, e);
        }

        apiRequest.applyHeadersToHttpRequestBuilder(httpRequestBuilder, apiRequest.getRequestHeaders());
        httpRequestBuilder.method(apiRequest.getRequestMethod().getName(), apiRequest.getBodyPublisher());

        apiRequest.getWrappedApi().onApiRequest(apiRequest);

        HttpResponse<?> httpResponse;

        try {
            httpResponse = httpClient.send(httpRequestBuilder.build(), apiRequest.getBodyHandler());
        } catch (Throwable throwable) {
            apiRequest.getWrappedApi().onException(apiRequest, throwable);

            if (apiRequest.getWrappedApi().rethrowExceptions()) {
                throw throwable;
            } else {
                return null;
            }
        }

        apiRequest.getWrappedApi().onAfterApiRequest(apiRequest);

        T response;

        try {
            response = apiRequest.handleResponse(httpResponse);
        } catch (Throwable throwable) {
            apiRequest.getWrappedApi().onException(apiRequest, throwable);

            if (apiRequest.getWrappedApi().rethrowExceptions()) {
                throw throwable;
            } else {
                return null;
            }
        }

        apiRequest.getWrappedApi().onAfterHandledApiRequest(apiRequest, response);

        return response;
    }

    /**
     * Is called before the request is sent.
     *
     * @param request The request.
     * @param <T>     The type of the response.
     */
    default <T> void onApiRequest(ApiRequest<T> request) {
    }

    /**
     * It is called after the request is sent but before the response is handled.<br>If the request send fails with exception, this method is not
     * called.
     *
     * @param request The request.
     * @param <T>     The type of the response.
     */
    default <T> void onAfterApiRequest(ApiRequest<T> request) {
    }

    /**
     * It is called after the request's response is handled.<br>If the request send and/or request's response handler fails with exception, this
     * method is not called.
     *
     * @param request  The request.
     * @param response The response.
     * @param <T>      The type of the response.
     */
    default <T> void onAfterHandledApiRequest(ApiRequest<T> request, T response) {
    }

    /**
     * It is called when any exception occurs when sending the request or handling the response.<br><br>Note: This method does not catch any
     * exceptions.<br>All exceptions are re-thrown after this method is called, if the {@link #rethrowExceptions()} returns {@code true}.
     *
     * @param request   The request.
     * @param throwable The exception.
     * @param <T>       The type of the response.
     */
    default <T> void onException(ApiRequest<T> request, Throwable throwable) {
    }

    /**
     * Determines if exceptions should be re-thrown after {@link #onException(ApiRequest, Throwable)} is called.<br>Also, if this method returns
     * false, all responses that failed to be sent or handled will be {@code null}.
     *
     * @return If exceptions should be re-thrown after {@link #onException(ApiRequest, Throwable)} is called.
     */
    default boolean rethrowExceptions() {
        return true;
    }
}
