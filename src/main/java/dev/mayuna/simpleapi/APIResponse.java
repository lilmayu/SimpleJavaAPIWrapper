package dev.mayuna.simpleapi;

import lombok.Getter;
import lombok.Setter;

/**
 * Abstract class with which you can extend your response classes - They will be able to get http response code and your {@link SimpleAPI} object (useful for "live" API objects)
 * @param <T> Your class which extends {@link SimpleAPI}
 */
public abstract class APIResponse<T extends SimpleAPI> {

    protected @Getter @Setter T api = null;
    protected @Getter @Setter int responseCode;
}
