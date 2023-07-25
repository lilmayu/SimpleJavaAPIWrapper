package dev.mayuna.simpleapi;

import dev.mayuna.simpleapi.impl.HttpBinApi;
import org.junit.Test;

public class NewTest {

    @Test
    public void testApi() {
        HttpBinApi httpBinApi = new HttpBinApi();

        httpBinApi.fetchAnything().sendAsync().whenCompleteAsync((httpResponse, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
                return;
            }

            System.out.println("Url: " + httpResponse.getUrl());
        }).join();
    }
}
