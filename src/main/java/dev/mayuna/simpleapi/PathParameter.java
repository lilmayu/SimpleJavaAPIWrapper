package dev.mayuna.simpleapi;

import lombok.Getter;
import lombok.NonNull;

public class PathParameter {

    private final @Getter @NonNull String parameter;
    private final @Getter @NonNull String replacement;

    public PathParameter(String parameter, String replacement) {
        this.parameter = parameter;
        this.replacement = replacement;
    }

    @Override
    public String toString() {
        return "{" + parameter + "}";
    }
}
