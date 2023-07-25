package dev.mayuna.simpleapi;

import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

/**
 * Represents path parameter which will be replaced in your request
 */
public class PathParameter {

    private final @Getter String id;
    private final @Getter String replacement;

    private PathParameter(String id, String replacement) {
        this.id = id;
        this.replacement = replacement;
    }

    /**
     * Creates {@link PathParameter} with parameter name and its replacement
     * @param id Non-null parameter name. Must be without <code>{}</code> symbols
     * @param replacement Non-null replacement which will replace your path parameter
     * @return Non-null {@link PathParameter} instance
     */
    public static PathParameter of(@NotNull String id, @NonNull String replacement) {
        return new PathParameter(id, replacement);
    }
}
