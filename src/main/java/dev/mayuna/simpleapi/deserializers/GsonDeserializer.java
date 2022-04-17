package dev.mayuna.simpleapi.deserializers;

import com.google.gson.Gson;
import lombok.NonNull;

/**
 * You can implement this interface into response object for automatic Gson deserialization via {@link Gson#fromJson(String, Class)}
 */
public interface GsonDeserializer {

    /**
     * Returns {@link Gson} object which will be used for deserialization
     *
     * @return Non-null {@link Gson} object
     */
    @NonNull Gson getGson();

}
