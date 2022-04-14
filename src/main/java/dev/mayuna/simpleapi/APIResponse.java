package dev.mayuna.simpleapi;

import lombok.Getter;
import lombok.Setter;

public abstract class APIResponse<T extends SimpleAPI> {

    protected @Getter @Setter T api = null;
    protected @Getter @Setter int responseCode;
}
