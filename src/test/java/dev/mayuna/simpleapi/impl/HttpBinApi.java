package dev.mayuna.simpleapi.impl;

import com.google.gson.JsonObject;
import dev.mayuna.simpleapi.*;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpBinApi implements WrappedApi {

    @Override
    public String getDefaultUrl() {
        return "https://httpbin.org";
    }

    public ApiRequest<HttpBinResponse> fetchAnything() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("someJsonValue", 100);

        return ApiRequest.builder(this, HttpBinResponse.class)
                         .withEndpoint("/{someParameter}")
                         .withRequestMethod(RequestMethod.GET)
                         .withPathParameters(PathParameter.of("someParameter", "anything"))
                         .withRequestQueries(RequestQuery.of("some_query", "some_query_value"))
                         .withRequestHeaders(RequestHeader.of("SomeHeader", "SomeHeaderValue"),
                                             RequestHeader.ofContentType("application/json")
                         )
                         .withBodyPublisher(HttpRequest.BodyPublishers.ofString(jsonObject.toString()))
                         .withBodyHandler(HttpResponse.BodyHandlers.ofString())
                         .build();
    }
}
