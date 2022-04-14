package dev.mayuna.simpleapi;

import dev.mayuna.simpleapi.impl.HttpBinAPI;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class SimpleTest {

    @Test
    public void testSimpleAPI() {
        HttpBinAPI api = new HttpBinAPI();

        api.requestAnything().onSuccess(((httpResponse, httpBinResponse) -> {
            Assertions.assertNotNull(httpBinResponse.getUrl());
        }));
    }
}
