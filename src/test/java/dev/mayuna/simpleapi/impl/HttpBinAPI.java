package dev.mayuna.simpleapi.impl;

import dev.mayuna.simpleapi.Action;
import dev.mayuna.simpleapi.Header;
import dev.mayuna.simpleapi.SimpleAPI;
import dev.mayuna.simpleapi.impl.api.HttpBinResponse;

public class HttpBinAPI extends SimpleAPI {

    public HttpBinAPI() {
        this.defaultHeaders = new Header[]{
            new Header("DefaultHeaderName", "DefaultHeaderValue")
        };
    }

    @Override
    public String getURL() {
        return "https://httpbin.org";
    }

    public Action<HttpBinResponse> requestAnything() {
        return new Action<>(this, HttpBinResponse.class, new HttpBinResponse.Request());
    }
}
