package dev.mayuna.simpleapi;

import lombok.Getter;
import lombok.NonNull;

public class Query {

    private final @Getter String name;
    private final @Getter String value;

    /**
     * Creates {@link Query} with name and value
     * @param name Non-null {@link Query} name
     * @param value Non-null {@link Query} value
     */
    public Query(@NonNull String name, @NonNull String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Returns {@link Query} in <code>query_name=query_value</code> format
     * @return Non-null {@link Query} in <code>query_name=query_value</code> format
     */
    @Override
    public String toString() {
        return name + "=" + value;
    }
}
