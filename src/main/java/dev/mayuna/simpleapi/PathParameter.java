package dev.mayuna.simpleapi;

import lombok.Getter;
import lombok.NonNull;

public class PathParameter {

    private final @Getter String parameter;
    private final @Getter String replacement;

    /**
     * Creates {@link PathParameter} with parameter name and its replacement
     * @param parameter Non-null parameter name. Must be without <code>{}</code> symbols
     * @param replacement Non-null replacement which will replace your path parameter
     */
    public PathParameter(@NonNull String parameter, @NonNull String replacement) {
        this.parameter = parameter;
        this.replacement = replacement;
    }

    /**
     * Returns {@link PathParameter} in <code>{parameter_name}</code> format
     * @return Non-null {@link PathParameter} name in <code>{parameter_name}</code> format
     */
    @Override
    public @NonNull String toString() {
        return "{" + parameter + "}";
    }
}
