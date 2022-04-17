package dev.mayuna.simpleapi.impl;

import com.google.gson.JsonObject;
import dev.mayuna.simpleapi.*;
import dev.mayuna.simpleapi.impl.api.HttpBinResponse;

import java.net.http.HttpRequest;
import java.util.Random;

public class HttpBinAPI extends SimpleAPI {

    @Override
    public String getURL() {
        return "https://httpbin.org";
    }

    @Override
    public Header[] getDefaultHeads() { // Optional, good for token authorization, etc
        return new Header[]{
                new Header("DefaultHeaderName", "DefaultHeaderValue")
        };
    }

    public Action<HttpBinResponse> requestAnything() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("test", 100);

        return new Action<>(this,
                            HttpBinResponse.class,
                            new APIRequest.Builder()
                                    .setEndpoint("/anything?test={test_id}") // Required
                                    .setMethod("POST") // Required
                                    .addPathParameter(new PathParameter("test_id", String.valueOf(new Random().nextInt())))
                                    .addQuery(new Query("some_query", "69"))
                                    .setHttpRequestBuilder(builder -> {
                                        builder.header("HeaderName", "HeaderValue");
                                    })
                                    .setContentType("application/json")
                                    .setBodyPublisher(HttpRequest.BodyPublishers.ofString(jsonObject.toString()))
                                    .build()
        );
    }
}
