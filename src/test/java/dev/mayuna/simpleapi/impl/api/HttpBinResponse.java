package dev.mayuna.simpleapi.impl.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import dev.mayuna.simpleapi.APIResponse;
import dev.mayuna.simpleapi.deserializers.GsonDeserializer;
import dev.mayuna.simpleapi.impl.HttpBinAPI;
import lombok.Getter;

public class HttpBinResponse extends APIResponse<HttpBinAPI> implements GsonDeserializer {

    private @Getter @Expose String url;

    @Override
    public Gson getGson() {
        return new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    }
}
