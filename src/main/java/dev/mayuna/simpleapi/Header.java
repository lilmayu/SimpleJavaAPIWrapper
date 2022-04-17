package dev.mayuna.simpleapi;

import lombok.Getter;

public class Header {

    private final @Getter String name;
    private final @Getter String value;

    /**
     * Creates {@link Header} with name and value
     * @param name Header name, for example <code>Content-Type</code>
     * @param value Header value, for example <code>application/json</code>
     */
    public Header(String name, String value) {
        this.name = name;
        this.value = value;
    }
}
