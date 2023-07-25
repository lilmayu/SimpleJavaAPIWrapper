package dev.mayuna.simpleapi;

import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadFactory;

/**
 * Represents an API request. Can send API requests.
 *
 * @param <T> The type of the response.
 */
public interface ApiRequest<T> {

    /**
     * Creates a new {@link ApiRequestBuilder} for the given {@link WrappedApi} and {@link Class}.
     *
     * @param wrappedApi    The {@link WrappedApi} to use.
     * @param responseClass The {@link Class} of the response.
     * @param <T>           The type of the response.
     *
     * @return The created {@link ApiRequestBuilder}.
     */
    static <T> ApiRequestBuilder<T> builder(WrappedApi wrappedApi, Class<T> responseClass) {
        return ApiRequestBuilder.ofResponse(wrappedApi, responseClass);
    }

    /**
     * Gets the {@link WrappedApi} of this {@link ApiRequest}.
     *
     * @return The {@link WrappedApi} of this {@link ApiRequest}.
     */
    @NotNull WrappedApi getWrappedApi();

    /**
     * Gets the {@link Class} of the response.
     *
     * @return The {@link Class} of the response.
     */
    @NotNull Class<T> getResponseClass();

    /**
     * Gets the URL of this {@link ApiRequest}.
     *
     * @return The URL of this {@link ApiRequest}. If null, the default URL of the {@link WrappedApi} will be used.
     */
    @Nullable String getUrl();

    /**
     * Gets the endpoint of this {@link ApiRequest}.
     *
     * @return The endpoint of this {@link ApiRequest}.
     */
    @NotNull String getEndpoint();

    /**
     * Gets the {@link RequestMethod} of this {@link ApiRequest}.
     *
     * @return The {@link RequestMethod} of this {@link ApiRequest}.
     */
    @NotNull RequestMethod getRequestMethod();

    /**
     * Gets the {@link PathParameter}s of this {@link ApiRequest}.
     *
     * @return The {@link PathParameter}s of this {@link ApiRequest}.
     */
    default @Nullable PathParameter[] getPathParameters() {
        return null;
    }

    /**
     * Gets the {@link RequestQuery}s of this {@link ApiRequest}.
     *
     * @return The {@link RequestQuery}s of this {@link ApiRequest}.
     */
    default @Nullable RequestQuery[] getRequestQueries() {
        return null;
    }

    /**
     * Gets the {@link RequestHeader}s of this {@link ApiRequest}.
     *
     * @return The {@link RequestHeader}s of this {@link ApiRequest}.
     */
    default @Nullable RequestHeader[] getRequestHeaders() {
        return null;
    }

    /**
     * Gets the {@link HttpRequest.BodyPublisher} of this {@link ApiRequest}.
     *
     * @return The {@link HttpRequest.BodyPublisher} of this {@link ApiRequest}.
     */
    default @NonNull HttpRequest.BodyPublisher getBodyPublisher() {
        return HttpRequest.BodyPublishers.noBody();
    }

    /**
     * Gets the {@link HttpResponse.BodyHandler} of this {@link ApiRequest}.
     *
     * @return The {@link HttpResponse.BodyHandler} of this {@link ApiRequest}.
     */
    default @NotNull HttpResponse.BodyHandler<?> getBodyHandler() {
        return HttpResponse.BodyHandlers.ofString();
    }

    /**
     * Gets the computed endpoint of this {@link ApiRequest}.
     *
     * @return The computed endpoint of this {@link ApiRequest} with all {@link PathParameter}s replaces and {@link RequestQuery}s added.
     */
    default String getComputedEndpoint() {
        String computedEndpoint = getEndpoint();

        PathParameter[] pathParameters = getPathParameters();
        if (pathParameters != null && pathParameters.length > 0) {
            for (PathParameter pathParameter : getPathParameters()) {
                //noinspection DataFlowIssue
                computedEndpoint = computedEndpoint.replace("{" + pathParameter.getId() + "}", pathParameter.getReplacement());
            }
        }

        RequestQuery[] requestQueries = getRequestQueries();

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
     * Applies the {@link RequestHeader}s to the given {@link HttpRequest.Builder}.
     *
     * @param httpRequestBuilder The {@link HttpRequest.Builder} to apply the {@link RequestHeader}s to.
     * @param requestHeaders     The {@link RequestHeader}s to apply.
     */
    default void applyHeadersToHttpRequestBuilder(HttpRequest.Builder httpRequestBuilder, RequestHeader[] requestHeaders) {
        if (requestHeaders != null) {
            for (RequestHeader requestHeader : requestHeaders) {
                httpRequestBuilder.header(requestHeader.getKey(), requestHeader.getValue());
            }
        }
    }

    /**
     * Creates a new {@link HttpClient} instance.
     *
     * @return The created {@link HttpClient} instance.
     */
    default HttpClient createHttpClientInstance() {
        return HttpClient.newBuilder().build();
    }

    /**
     * Creates a new {@link HttpRequest.Builder} instance.
     *
     * @return The created {@link HttpRequest.Builder} instance.
     */
    default HttpRequest.Builder createHttpRequestBuilderInstance() {
        return HttpRequest.newBuilder();
    }

    /**
     * Creates a new instance of the response class.
     *
     * @return The created instance of the response class.
     *
     * @throws NoSuchMethodException     Is thrown if the response class does not have a default constructor.
     * @throws InvocationTargetException Is thrown if the constructor of the response class throws an exception.
     * @throws InstantiationException    Is thrown if the response class is abstract or interface.
     * @throws IllegalAccessException    Is thrown if the constructor of the response class is not accessible.
     */
    default T createInstanceOfResponseClass() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (getResponseClass().isArray()) {
            throw new RuntimeException("Cannot create instance of array");
        }

        return getResponseClass().getDeclaredConstructor().newInstance();
    }

    /**
     * Handles the given {@link HttpResponse}.
     *
     * @param httpResponse The {@link HttpResponse} to handle.
     *
     * @return The instance of the response class.
     *
     * @throws NoSuchMethodException     Is thrown if the response class does not have a default constructor.
     * @throws InvocationTargetException Is thrown if the constructor of the response class throws an exception.
     * @throws InstantiationException    Is thrown if the response class is abstract or interface.
     * @throws IllegalAccessException    Is thrown if the constructor of the response class is not accessible.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    default T handleResponse(HttpResponse<?> httpResponse) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        T responseInstance = createInstanceOfResponseClass();

        if (responseInstance instanceof ApiResponse) {
            ApiResponse apiResponse = (ApiResponse) responseInstance;

            apiResponse.setHttpStatusCode(httpResponse.statusCode());
            apiResponse.setWrappedApi(getWrappedApi());
        }

        if (responseInstance instanceof DeserializableApiResponse) {
            responseInstance = (T) ((DeserializableApiResponse) responseInstance).deserialize(this, httpResponse);
        }

        return responseInstance;
    }

    /**
     * Gets the {@link ThreadFactory} to use for sending the async request.
     *
     * @return The {@link ThreadFactory} to use for sending the async request.
     */
    default ThreadFactory getThreadFactory() {
        return Thread::new;
    }

    /**
     * Sends the request asynchronously.
     *
     * @return A {@link CompletableFuture} that will be completed with the instance of response class.
     */
    default CompletableFuture<T> sendAsync() {
        CompletableFuture<T> completableFuture = new CompletableFuture<>();

        getThreadFactory().newThread(() -> {
            try {
                completableFuture.complete(send());
            } catch (Exception exception) {
                completableFuture.completeExceptionally(exception);
            }
        }).start();

        return completableFuture;
    }

    /**
     * Sends the request synchronously.
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
    default T send() throws IOException, InterruptedException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        String url = getUrl();

        if (url == null) {
            url = getWrappedApi().getDefaultUrl();
        }

        String requestUrl = url + getComputedEndpoint();

        HttpClient httpClient = createHttpClientInstance();
        HttpRequest.Builder httpRequestBuilder = createHttpRequestBuilderInstance();

        try {
            httpRequestBuilder.uri(new URI(requestUrl));
        } catch (IllegalArgumentException | URISyntaxException e) {
            throw new RuntimeException("Invalid URI " + requestUrl, e);
        }

        applyHeadersToHttpRequestBuilder(httpRequestBuilder, getRequestHeaders());
        httpRequestBuilder.method(getRequestMethod().getName(), getBodyPublisher());

        getWrappedApi().onApiRequest(this);

        HttpResponse<?> httpResponse = httpClient.send(httpRequestBuilder.build(), getBodyHandler());

        return handleResponse(httpResponse);
    }
}
