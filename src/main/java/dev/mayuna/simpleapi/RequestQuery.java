package dev.mayuna.simpleapi;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class RequestQuery {

    private final @Getter String name;
    private final @Getter String value;

    private RequestQuery(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Creates {@link RequestQuery} with name and value
     * @param name Non-null {@link RequestQuery} name
     * @param value Non-null {@link RequestQuery} value
     * @return Non-null {@link RequestQuery} with name and value
     */
    public static @NotNull RequestQuery of(@NotNull String name, @NotNull String value) {
        return new RequestQuery(name, value);
    }
}
