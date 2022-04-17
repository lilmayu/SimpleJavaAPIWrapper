package dev.mayuna.simpleapi;

import dev.mayuna.simpleapi.deserializers.GsonDeserializer;
import dev.mayuna.simpleapi.exceptions.HttpException;
import dev.mayuna.simpleapi.exceptions.MissingPathParametersException;
import lombok.Getter;
import lombok.NonNull;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class Action<T> {

    private final @Getter SimpleAPI api;
    private final @Getter Class<T> responseClass;
    private final @Getter APIRequest apiRequest;

    private Consumer<HttpError> httpErrorCallback = httpError -> {throw new HttpException(httpError);};
    private BiConsumer<HttpResponse<?>, T> successCallback = (responseBody, object) -> {};
    private Function<HttpResponse<?>, T> deserializationCallback = null;

    /**
     * Creates {@link Action} object
     * @param api Your {@link SimpleAPI} object
     * @param responseClass Non-null Class which you expect to be responded
     * @param apiRequest Non-null {@link APIRequest}
     */
    public Action(SimpleAPI api, @NonNull Class<T> responseClass, @NonNull APIRequest apiRequest) {
        this.api = api;
        this.responseClass = responseClass;
        this.apiRequest = apiRequest;
    }

    /**
     * {@link HttpError} {@link Consumer} which will be called if there would be any {@link HttpException}
     * @param httpErrorCallback Non-null {@link HttpError} {@link Consumer}
     * @return {@link Action}, great for chaining
     */
    public Action<T> onHttpError(@NonNull Consumer<HttpError> httpErrorCallback) {
        this.httpErrorCallback = httpErrorCallback;
        return this;
    }

    /**
     * {@link HttpResponse} and your response class {@link BiConsumer} which will be called if the request was successful
     * @param successCallback Non-null {@link HttpResponse} and your response class {@link BiConsumer}
     * @return {@link Action}, great for chaining
     */
    public Action<T> onSuccess(BiConsumer<HttpResponse<?>, T> successCallback) {
        this.successCallback = successCallback;
        return this;
    }

    /**
     * {@link Function} with {@link HttpResponse} argument and your response class return<br>
     * This {@link Function} will be called for deserializing. You should return your deserialized object here. If your API returns JSON, your can use {@link GsonDeserializer} to automate this step.
     * @param deserializationCallback Nullable {@link Function} with {@link HttpResponse} argument and your response class return
     * @return {@link Action}, great for chaining
     */
    public Action<T> onDeserialization(Function<HttpResponse<?>, T> deserializationCallback) {
        this.deserializationCallback = deserializationCallback;
        return this;
    }

    /**
     * Requests the API with specified {@link APIRequest}
     * @return {@link CompletableFuture}
     */
    public CompletableFuture<T> execute() {
        CompletableFuture<T> completableFuture = new CompletableFuture<>();
        String urlStart = api.getURL();
        String urlEndpoint = apiRequest.getFinalEndpoint();

        if (urlStart.endsWith("/")) {
            urlStart = urlStart.substring(0, urlStart.length() - 2);

            if (!urlEndpoint.startsWith("/")) {
                urlEndpoint = "/" + urlEndpoint;
            }
        }

        String stringUrl = urlStart + urlEndpoint;

        if (apiRequest.getPathParameters() != null && apiRequest.getPathParameters().length != 0) {
            for (PathParameter pathParameters : apiRequest.getPathParameters()) {
                stringUrl = stringUrl.replace(pathParameters.toString(), pathParameters.getReplacement());
            }
        }

        if (stringUrl.contains("{") || stringUrl.contains("}")) {
            throw new MissingPathParametersException(stringUrl, apiRequest);
        }

        int responseCode = -1;

        try {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder()
                    .uri(new URI(stringUrl));

            if (api.getDefaultHeads() != null && api.getDefaultHeads().length != 0) {
                for (Header header : api.getDefaultHeads()) {
                    httpRequestBuilder.header(header.getName(), header.getValue());
                }
            }

            apiRequest.getBodyPublisher();
            httpRequestBuilder.method(apiRequest.getMethod(), apiRequest.getBodyPublisher());

            if (apiRequest.getContentType() != null) {
                httpRequestBuilder.header("Content-Type", apiRequest.getContentType());
            }

            apiRequest.processHttpRequestBuilder(httpRequestBuilder);

            HttpResponse<?> httpResponse = httpClient.send(httpRequestBuilder.build(), apiRequest.getBodyHandler());
            responseCode = httpResponse.statusCode();

            T t = null;

            if (!responseClass.isArray()) {
                t = responseClass.getDeclaredConstructor().newInstance();

                if (t instanceof GsonDeserializer) {
                    t = ((GsonDeserializer) t).getGson().fromJson((String) httpResponse.body(), responseClass);
                } else {
                    if (deserializationCallback != null) {
                        t = deserializationCallback.apply(httpResponse);
                    }
                }
            } else {
                Object object = responseClass.getComponentType().getDeclaredConstructor().newInstance();

                if (object instanceof GsonDeserializer) {
                    t = ((GsonDeserializer) object).getGson().fromJson((String) httpResponse.body(), responseClass);
                } else {
                    if (deserializationCallback != null) {
                        t = deserializationCallback.apply(httpResponse);
                    }
                }
            }

            if (t instanceof APIResponse) {
                ((APIResponse) t).responseCode = responseCode;
                ((APIResponse) t).api = api;
            }

            successCallback.accept(httpResponse, t);
            completableFuture.complete(t);
        } catch (IOException | InterruptedException | InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException | URISyntaxException
                exception) {
            httpErrorCallback.accept(new HttpError(responseCode, exception));
            completableFuture.completeExceptionally(exception);
        }

        return completableFuture;
    }
}
