package dev.mayuna.simpleapi;

import dev.mayuna.simpleapi.deserializers.CustomDeserializer;
import dev.mayuna.simpleapi.deserializers.GsonDeserializer;
import dev.mayuna.simpleapi.exceptions.HttpException;
import dev.mayuna.simpleapi.exceptions.MissingPathParametersException;
import lombok.Getter;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Action<T> {

    private final @Getter SimpleAPI api;
    private final @Getter Class<T> responseClass;
    private final @Getter APIRequest apiRequest;

    private Consumer<HttpError> httpErrorCallback = httpError -> {throw new HttpException(httpError);};
    private BiConsumer<HttpResponse<?>, T> successCallback = (responseBody, object) -> {};

    public Action(SimpleAPI api, Class<T> responseClass, APIRequest apiRequest) {
        this.api = api;
        this.responseClass = responseClass;
        this.apiRequest = apiRequest;
    }

    public Action<T> onHttpError(Consumer<HttpError> httpErrorCallback) {
        this.httpErrorCallback = httpErrorCallback;
        return this;
    }

    public Action<T> onSuccess(BiConsumer<HttpResponse<?>, T> successCallback) {
        this.successCallback = successCallback;
        return this;
    }

    public T execute() {
        String urlStart = api.getURL();
        String urlEndpoint = apiRequest.getEndpoint();

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

            if (api.defaultHeaders != null && api.defaultHeaders.length != 0) {
                for (Header header : api.defaultHeaders) {
                    httpRequestBuilder.header(header.getName(), header.getValue());
                }
            }

            httpRequestBuilder.method(apiRequest.getMethod(), HttpRequest.BodyPublishers.noBody());
            apiRequest.processHttpRequestBuilder(httpRequestBuilder);

            HttpResponse<?> httpResponse = httpClient.send(httpRequestBuilder.build(), apiRequest.getBodyHandler());
            responseCode = httpResponse.statusCode();

            T t = responseClass.getDeclaredConstructor().newInstance();

            if (t instanceof CustomDeserializer) {
                ((CustomDeserializer) t).deserialize(httpResponse.body());
            } else if (t instanceof GsonDeserializer) {
                t = ((GsonDeserializer) t).getGson().fromJson((String) httpResponse.body(), responseClass);
            }

            if (t instanceof APIResponse) {
                ((APIResponse) t).responseCode = responseCode;
                ((APIResponse) t).api = api;
            }

            successCallback.accept(httpResponse, t);

            return t;
        } catch (IOException | InterruptedException | InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException | URISyntaxException
                exception) {
            httpErrorCallback.accept(new HttpError(responseCode, exception));
            return null;
        }
    }
}
