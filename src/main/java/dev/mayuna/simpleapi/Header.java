package dev.mayuna.simpleapi;

import lombok.Getter;

public class Header {

    private final @Getter String name;
    private final @Getter String value;

    public Header(String name, String value) {
        this.name = name;
        this.value = value;
    }
}
