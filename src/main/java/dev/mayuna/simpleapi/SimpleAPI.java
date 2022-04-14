package dev.mayuna.simpleapi;

import lombok.Getter;

public abstract class SimpleAPI {

    public abstract String getURL();

    protected @Getter Header[] defaultHeaders = null;

}
