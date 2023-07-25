package dev.mayuna.simpleapi;

import lombok.Getter;

public class RequestMethod {

    public static final RequestMethod GET = new RequestMethod("GET");
    public static final RequestMethod POST = new RequestMethod("POST");
    public static final RequestMethod PUT = new RequestMethod("PUT");
    public static final RequestMethod DELETE = new RequestMethod("DELETE");

    private final @Getter String name;

    private RequestMethod(String name) {
        this.name = name;
    }

    /**
     * Creates {@link RequestMethod} with request method name
     * @param name Non-null {@link RequestMethod} name
     * @return Non-null {@link RequestMethod}
     */
    public static RequestMethod of(String name) {
        return new RequestMethod(name);
    }
}
