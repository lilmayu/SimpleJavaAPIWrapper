package dev.mayuna.simpleapi;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public interface APIRequest {

    String getEndpoint();

    String getMethod();

    PathParameter[] getPathParameters();

    void processHttpRequestBuilder(HttpRequest.Builder httpRequestBuilder);

    HttpResponse.BodyHandler<?> getBodyHandler();
}
