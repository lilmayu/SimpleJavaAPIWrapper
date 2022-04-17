package dev.mayuna.simpleapi;

import lombok.Getter;

public class Query {

    private final @Getter String name;
    private final @Getter String value;

    public Query(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String toString() {
        return name + "=" + value;
    }
}
