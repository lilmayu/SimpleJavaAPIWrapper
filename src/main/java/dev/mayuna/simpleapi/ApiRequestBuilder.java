package dev.mayuna.simpleapi;

import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A builder for {@link ApiRequest}.
 * @param <T> The type of the response.
 */
public class ApiRequestBuilder<T> {

    private final @Getter WrappedApi wrappedApi;
    private final @Getter Class<T> responseClass;

    private String url;
    private String endpoint;
    private RequestMethod requestMethod;
    private List<PathParameter> pathParameters = new ArrayList<>();
    private List<RequestQuery> requestQueries = new ArrayList<>();
    private List<RequestHeader> requestHeaders = new ArrayList<>();
    private HttpRequest.BodyPublisher bodyPublisher;
    private HttpResponse.BodyHandler<?> bodyHandler;

    private ApiRequestBuilder(WrappedApi wrappedApi, Class<T> responseClass) {
        this.wrappedApi = wrappedApi;
        this.responseClass = responseClass;
    }

    /**
     * Creates a new {@link ApiRequestBuilder} for the given {@link WrappedApi} and {@link Class}.
     *
     * @param wrappedApi    The {@link WrappedApi} to use.
     * @param responseClass The {@link Class} of the response.
     * @param <T>           The type of the response.
     *
     * @return The created {@link ApiRequestBuilder}.
     */
    public static <T> ApiRequestBuilder<T> ofResponse(WrappedApi wrappedApi, Class<T> responseClass) {
        return new ApiRequestBuilder<>(wrappedApi, responseClass);
    }

    /**
     * Sets the url of the request. This will override the default url of the {@link WrappedApi}.
     *
     * @param url The url to set. Should not contain a trailing slash.
     *
     * @return The {@link ApiRequestBuilder} instance.
     */
    public ApiRequestBuilder<T> withUrl(@NonNull String url) {
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        this.url = url;
        return this;
    }

    /**
     * Sets the endpoint of the request.
     *
     * @param endpoint The endpoint to set. Should contain a leading slash.
     *
     * @return The {@link ApiRequestBuilder} instance.
     */
    public ApiRequestBuilder<T> withEndpoint(@NonNull String endpoint) {
        if (!endpoint.startsWith("/")) {
            endpoint = "/" + endpoint;
        }

        this.endpoint = endpoint;
        return this;
    }

    /**
     * Sets the request method of the request.<br> Pro-tip: You can use pre-defined instances of {@link RequestMethod} like
     * {@link RequestMethod#GET}.
     *
     * @param requestMethod The request method to set.
     *
     * @return The {@link ApiRequestBuilder} instance.
     */
    public ApiRequestBuilder<T> withRequestMethod(@NonNull RequestMethod requestMethod) {
        this.requestMethod = requestMethod;
        return this;
    }

    public ApiRequestBuilder<T> withRequestMethod(@NonNull String requestMethod) {
        this.requestMethod = RequestMethod.of(requestMethod);
        return this;
    }

    /**
     * Adds a {@link PathParameter} to the request.
     *
     * @param pathParameter The {@link PathParameter} to add.
     *
     * @return The {@link ApiRequestBuilder} instance.
     */
    public ApiRequestBuilder<T> withPathParameter(@NonNull PathParameter pathParameter) {
        this.pathParameters.add(pathParameter);
        return this;
    }

    /**
     * Adds multiple {@link PathParameter}s to the request.
     *
     * @param pathParameter  The first {@link PathParameter} to add.
     * @param pathParameters The other {@link PathParameter}s to add.
     *
     * @return The {@link ApiRequestBuilder} instance.
     */
    public ApiRequestBuilder<T> withPathParameters(@NonNull PathParameter pathParameter, PathParameter... pathParameters) {
        withPathParameter(pathParameter);

        if (pathParameters != null) {
            for (PathParameter anotherPathParameter : pathParameters) {
                withPathParameter(anotherPathParameter);
            }
        }

        return this;
    }

    /**
     * Adds a {@link RequestQuery} to the request.
     *
     * @param requestQuery The {@link RequestQuery} to add.
     *
     * @return The {@link ApiRequestBuilder} instance.
     */
    public ApiRequestBuilder<T> withRequestQuery(@NonNull RequestQuery requestQuery) {
        this.requestQueries.add(requestQuery);
        return this;
    }

    /**
     * Adds multiple {@link RequestQuery}s to the request.
     *
     * @param requestQuery   The first {@link RequestQuery} to add.
     * @param requestQueries The other {@link RequestQuery}s to add.
     *
     * @return The {@link ApiRequestBuilder} instance.
     */
    public ApiRequestBuilder<T> withRequestQueries(@NonNull RequestQuery requestQuery, RequestQuery... requestQueries) {
        withRequestQuery(requestQuery);

        if (requestQueries != null) {
            for (RequestQuery anotherRequestQuery : requestQueries) {
                withRequestQuery(anotherRequestQuery);
            }
        }

        return this;
    }

    /**
     * Adds a {@link RequestHeader} to the request.
     *
     * @param requestHeader The {@link RequestHeader} to add.
     *
     * @return The {@link ApiRequestBuilder} instance.
     */
    public ApiRequestBuilder<T> withRequestHeader(@NonNull RequestHeader requestHeader) {
        this.requestHeaders.add(requestHeader);
        return this;
    }

    /**
     * Adds multiple {@link RequestHeader}s to the request.
     *
     * @param requestHeader  The first {@link RequestHeader} to add.
     * @param requestHeaders The other {@link RequestHeader}s to add.
     *
     * @return The {@link ApiRequestBuilder} instance.
     */
    public ApiRequestBuilder<T> withRequestHeaders(@NonNull RequestHeader requestHeader, RequestHeader... requestHeaders) {
        withRequestHeader(requestHeader);

        if (requestHeaders != null) {
            for (RequestHeader anotherRequestHeader : requestHeaders) {
                withRequestHeader(anotherRequestHeader);
            }
        }

        return this;
    }

    /**
     * Sets the {@link HttpRequest.BodyPublisher} of the request.
     * @param bodyPublisher The {@link HttpRequest.BodyPublisher} to set.
     * @return The {@link ApiRequestBuilder} instance.
     */
    public ApiRequestBuilder<T> withBodyPublisher(@NonNull HttpRequest.BodyPublisher bodyPublisher) {
        this.bodyPublisher = bodyPublisher;
        return this;
    }

    /**
     * Sets the {@link HttpResponse.BodyHandler} of the request.
     * @param bodyHandler The {@link HttpResponse.BodyHandler} to set.
     * @return The {@link ApiRequestBuilder} instance.
     */
    public ApiRequestBuilder<T> withBodyHandler(@NonNull HttpResponse.BodyHandler<?> bodyHandler) {
        this.bodyHandler = bodyHandler;
        return this;
    }

    /**
     * Builds the {@link ApiRequest}.
     * @return The built {@link ApiRequest}.
     */
    public ApiRequest<T> build() {
        return new ApiRequest<>() {
            @Override
            public @NotNull Class<T> getResponseClass() {
                return responseClass;
            }

            @Override
            public @NotNull WrappedApi getWrappedApi() {
                return wrappedApi;
            }

            @Override
            public @NotNull String getUrl() {
                return url;
            }

            @Override
            public @NotNull String getEndpoint() {
                return endpoint;
            }

            @Override
            public @NotNull RequestMethod getRequestMethod() {
                return requestMethod;
            }

            @Override
            public @Nullable PathParameter[] getPathParameters() {
                if (pathParameters.isEmpty()) {
                    return null;
                }

                return pathParameters.toArray(new PathParameter[0]);
            }

            @Override
            public @Nullable RequestQuery[] getRequestQueries() {
                if (requestQueries.isEmpty()) {
                    return null;
                }

                return requestQueries.toArray(new RequestQuery[0]);
            }

            @Override
            public @Nullable RequestHeader[] getRequestHeaders() {
                if (requestHeaders.isEmpty()) {
                    return null;
                }

                return requestHeaders.toArray(new RequestHeader[0]);
            }

            @Override
            public @NonNull HttpRequest.BodyPublisher getBodyPublisher() {
                return Objects.requireNonNullElseGet(bodyPublisher, ApiRequest.super::getBodyPublisher);
            }

            @Override
            public @NonNull HttpResponse.BodyHandler<?> getBodyHandler() {
                return Objects.requireNonNullElseGet(bodyHandler, ApiRequest.super::getBodyHandler);
            }
        };
    }
}
