package dev.mayuna.simpleapi;

import lombok.Getter;
import lombok.NonNull;

public class RequestHeader {

    private final @Getter String key;
    private final @Getter String value;

    private RequestHeader(String key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Create a new RequestHeader instance.
     * @param key Non-null Header key
     * @param value Non-null Header value
     * @return Non-null {@link RequestHeader} instance
     */
    public static @NonNull RequestHeader of(@NonNull String key, @NonNull String value) {
        return new RequestHeader(key, value);
    }

    /**
     * Create a new RequestHeader instance with the key <code>Content-Type</code>.
     * @param value Non-null Header value
     * @return Non-null {@link RequestHeader} instance
     */
    public static @NonNull RequestHeader ofContentType(@NonNull String value) {
        return new RequestHeader("Content-Type", value);
    }
}
