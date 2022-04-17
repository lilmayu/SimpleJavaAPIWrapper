package dev.mayuna.simpleapi;

public abstract class SimpleAPI {

    public abstract String getURL();

    public Header[] getDefaultHeads() {
        return null;
    }
}
