package dev.mayuna.simpleapi.impl.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import dev.mayuna.simpleapi.APIRequest;
import dev.mayuna.simpleapi.APIResponse;
import dev.mayuna.simpleapi.PathParameter;
import dev.mayuna.simpleapi.deserializers.GsonDeserializer;
import dev.mayuna.simpleapi.impl.HttpBinAPI;
import lombok.Getter;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;

public class HttpBinResponse extends APIResponse<HttpBinAPI> implements GsonDeserializer {

    private @Getter @Expose String url;

    @Override
    public Gson getGson() {
        return new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    }

    public static class Request implements APIRequest {

        @Override
        public String getEndpoint() {
            return "/anything?test={test_id}";
        }

        @Override
        public String getMethod() {
            return "POST";
        }

        @Override
        public PathParameter[] getPathParameters() {
            return new PathParameter[]{
                    new PathParameter("test_id", String.valueOf(new Random().nextInt()))
            };
        }

        @Override
        public void processHttpRequestBuilder(HttpRequest.Builder httpRequestBuilder) {
            httpRequestBuilder.header("HeaderName", "HeaderValue");
        }

        @Override
        public HttpResponse.BodyHandler<?> getBodyHandler() {
            return HttpResponse.BodyHandlers.ofString();
        }
    }
}
