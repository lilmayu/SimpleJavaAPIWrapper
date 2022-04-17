package dev.mayuna.simpleapi;

import com.google.gson.Gson;
import dev.mayuna.simpleapi.impl.HttpBinAPI;
import dev.mayuna.simpleapi.impl.api.HttpBinResponse;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class SimpleTest {

    @Test
    public void testSimpleAPI() {
        HttpBinAPI api = new HttpBinAPI();

        api.requestAnything()
                .onHttpError(httpError -> {
                    // Handle HTTP error
                })
                .onSuccess(((httpResponse, httpBinResponse) -> {
                    // Handle raw HttpResponse + deserialized object
                    System.out.println(httpResponse.body());
                    Assertions.assertEquals(200, httpResponse.statusCode());
                }))
                .onDeserialization(httpResponse -> {
                    // Handle custom deserialization, if your response object implements GsonDeserialization, you don't have to do this step
                    return new Gson().fromJson((String) httpResponse.body(), HttpBinResponse.class);
                })
                .execute().thenAcceptAsync(httpBinResponse -> {
                    // #execute() returns CompletableFuture, you can wait before completion with #join(), do async stuff with #thenAcceptAsync() and all other fun things
                    Assertions.assertNotNull(httpBinResponse.getUrl());
                }).join(); // #join() here just to wait, in real world cases, you don't want to wait here since you've handled the response asynchronously
    }
}
