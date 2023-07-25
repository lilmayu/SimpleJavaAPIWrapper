package dev.mayuna.simpleapi.impl;

import dev.mayuna.simpleapi.GsonApiResponse;
import lombok.Getter;

public class HttpBinResponse extends GsonApiResponse<HttpBinApi> {

    private @Getter String url;

}
