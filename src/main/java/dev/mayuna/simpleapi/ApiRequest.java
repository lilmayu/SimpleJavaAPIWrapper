package dev.mayuna.simpleapi;

import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

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
        return getWrappedApi().computeEndpoint(this);
    }

    /**
     * Applies the {@link RequestHeader}s to the given {@link HttpRequest.Builder}.
     *
     * @param httpRequestBuilder The {@link HttpRequest.Builder} to apply the {@link RequestHeader}s to.
     * @param requestHeaders     The {@link RequestHeader}s to apply.
     */
    default void applyHeadersToHttpRequestBuilder(HttpRequest.Builder httpRequestBuilder, RequestHeader[] requestHeaders) {
        getWrappedApi().applyHeadersToHttpRequestBuilder(this, httpRequestBuilder, requestHeaders);
    }

    /**
     * Creates a new {@link HttpClient} instance.
     *
     * @return The created {@link HttpClient} instance.
     */
    default HttpClient createHttpClientInstance() {
        return getWrappedApi().createHttpClientInstance();
    }

    /**
     * Creates a new {@link HttpRequest.Builder} instance.
     *
     * @return The created {@link HttpRequest.Builder} instance.
     */
    default HttpRequest.Builder createHttpRequestBuilderInstance() {
        return getWrappedApi().createHttpRequestBuilderInstance();
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
        return getWrappedApi().createInstanceOfResponseClass(getResponseClass());
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
    default T handleResponse(HttpResponse<?> httpResponse) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return getWrappedApi().handleResponse(this, httpResponse);
    }

    /**
     * Sends the request asynchronously using {@link WrappedApi#runAsync(Runnable)}.
     *
     * @return A {@link CompletableFuture} that will be completed with the instance of response class.
     */
    default CompletableFuture<T> sendAsync() {
        CompletableFuture<T> completableFuture = new CompletableFuture<>();

        getWrappedApi().runAsync(() -> {
            try {
                completableFuture.complete(send());
            } catch (Throwable throwable) {
                completableFuture.completeExceptionally(throwable);
            }
        });

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
        return getWrappedApi().send(this);
    }
}
