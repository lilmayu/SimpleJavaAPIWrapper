package dev.mayuna.simpleapi;

import lombok.NonNull;

public abstract class SimpleAPI {

    /**
     * Base API URL<br>
     * Example: <code>https://example.come/v1</code>
     * @return Non-null Base API URL
     */
    public abstract @NonNull String getURL();

    /**
     * Default headers which will be added in every request. Useful for token based <code>Authorization</code> headers
     * @return Nullable {@link Header} array
     */
    public Header[] getDefaultHeads() {
        return null;
    }
}
