package dev.mayuna.simpleapi;

import lombok.Getter;
import lombok.NonNull;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Consumer;

public interface APIRequest {

    /**
     * Returns API endpoint. Should start with <code>/</code><br>
     * Example: <code>/users/{user_id}/stats</code><br><br>
     * This method is required.
     *
     * @return Non-null API Endpoint
     */
    @NonNull String getEndpoint();

    /**
     * HTTP Method which will be used for requesting<br>
     * Example: <code>GET</code><br><br>
     * This method is required.
     *
     * @return HTTP Request method
     */
    @NonNull String getMethod();

    /**
     * Returns array of {@link PathParameter}s which will be used for replacing path parameters in APIs endpoint.<br>
     * Example: You have <code>/users/{user_id}/stats</code> API endpoint, so you must declare {@link PathParameter} with parameter <code>user_id</code> and your desired replacement
     *
     * @return Nullable {@link PathParameter} array
     */
    default PathParameter[] getPathParameters() {
        return null;
    }

    /**
     * Returns array of {@link Query}(ies) which will be added after APIs endpoint.<br>
     * Example: <br>
     * <pre>
     *     {@code
     *        return new Query[] {
     *            new Query("name", "first_value"),
     *            new Query("other_name", "second_value")
     *        }
     *     }
     * </pre><br>
     * Which will result in: <code>?name=first_value&#38;other_name=second_value</code>
     *
     * @return Nullable {@link Query} array
     */
    default Query[] getQueries() {
        return null;
    }

    /**
     * This method is called when requesting. You can specify special headers via {@link HttpRequest.Builder#header(String, String)} method.
     *
     * @param httpRequestBuilder Non-null {@link HttpRequest.Builder} object
     */
    default void processHttpRequestBuilder(@NonNull HttpRequest.Builder httpRequestBuilder) {
    }

    /**
     * This method must return expected API response body.<br>
     * Example: You know, that the API will respond with Raw JSON, so you return {@link HttpResponse.BodyHandlers#ofString()}
     *
     * @return Non-null {@link HttpResponse.BodyHandler} object
     */
    default @NonNull HttpResponse.BodyHandler<?> getBodyHandler() {
        return HttpResponse.BodyHandlers.ofString();
    }

    /**
     * Returns value which will be used in <code>Content-Type</code> header. Null for no <code>Content-Type</code> header
     *
     * @return Content-type
     */
    default String getContentType() {
        return null;
    }

    /**
     * Returns {@link java.net.http.HttpRequest.BodyPublisher} with request body.<br>
     * Example: You are sending JSON in POST Request, so you pass your JSON into {@link java.net.http.HttpRequest.BodyPublishers#ofString(String)} method
     *
     * @return Non-null {@link java.net.http.HttpRequest.BodyPublisher}
     */
    default @NonNull HttpRequest.BodyPublisher getBodyPublisher() {
        return HttpRequest.BodyPublishers.noBody();
    }

    /**
     * Returns redirect policy which will be used when requesting.
     * @return Non-null {@link java.net.http.HttpClient.Redirect}
     */
    default @NonNull HttpClient.Redirect getRedirectPolicy() {
        return HttpClient.Redirect.NORMAL;
    }

    /**
     * You should not override this method.<br>
     * This method is used for inserting specified queries in {@link #getQueries()} method
     *
     * @return Final API endpoint which will used for requesting
     */
    default String getFinalEndpoint() {
        String finalEndpoint = getEndpoint();

        if (getQueries() != null && getQueries().length != 0) {
            boolean first = true;
            for (Query query : getQueries()) {
                finalEndpoint += (first ? "?" : "&") + query.toString();
                first = false;
            }
        }

        return finalEndpoint;
    }

    public static class Builder {

        private @Getter String endpoint;
        private @Getter String method;
        private @Getter String contentType;
        private @Getter PathParameter[] pathParameters;
        private @Getter Query[] queries;
        private @Getter Consumer<HttpRequest.Builder> httpRequestBuilderConsumer;
        private @Getter HttpRequest.BodyPublisher bodyPublisher;
        private @Getter HttpResponse.BodyHandler<?> bodyHandler;
        private @Getter HttpClient.Redirect redirectPolicy;

        public Builder() {

        }

        /**
         * Builds you {@link APIRequest}
         *
         * @return Non-null {@link APIRequest}
         *
         * @throws IllegalArgumentException if you have not called {@link #setEndpoint(String)} or {@link #setMethod(String)} methods
         */
        public @NonNull APIRequest build() {
            if (endpoint == null) {
                throw new IllegalArgumentException("Endpoint cannot be null!");
            }

            if (method == null) {
                throw new IllegalArgumentException("Method cannot be null!");
            }

            return new APIRequest() {

                @Override
                public String getEndpoint() {
                    return endpoint;
                }

                @Override
                public String getMethod() {
                    return method;
                }

                @Override
                public String getContentType() {
                    if (contentType != null) {
                        return contentType;
                    } else {
                        return APIRequest.super.getContentType();
                    }
                }

                @Override
                public HttpRequest.BodyPublisher getBodyPublisher() {
                    if (bodyPublisher != null) {
                        return bodyPublisher;
                    } else {
                        return APIRequest.super.getBodyPublisher();
                    }
                }

                @Override
                public PathParameter[] getPathParameters() {
                    return pathParameters;
                }

                @Override
                public Query[] getQueries() {
                    return queries;
                }

                @Override
                public void processHttpRequestBuilder(HttpRequest.Builder httpRequestBuilder) {
                    if (httpRequestBuilderConsumer != null) {
                        httpRequestBuilderConsumer.accept(httpRequestBuilder);
                    } else {
                        APIRequest.super.processHttpRequestBuilder(httpRequestBuilder);
                    }
                }

                @Override
                public HttpResponse.BodyHandler<?> getBodyHandler() {
                    if (bodyHandler != null) {
                        return bodyHandler;
                    } else {
                        return APIRequest.super.getBodyHandler();
                    }
                }

                @Override
                public @NonNull HttpClient.Redirect getRedirectPolicy() {
                    if (redirectPolicy != null) {
                        return redirectPolicy;
                    } else {
                        return APIRequest.super.getRedirectPolicy();
                    }
                }
            };
        }

        /**
         * Sets APIs endpoint
         *
         * @param endpoint Non-null API endpoint
         *
         * @return {@link Builder}, great for chaining
         *
         * @see APIRequest#getEndpoint()
         */
        public @NonNull Builder setEndpoint(@NonNull String endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        /**
         * Sets HTTP Request method
         *
         * @param method Non-null HTTP Request method
         *
         * @return {@link Builder}, great for chaining
         *
         * @see APIRequest#getMethod()
         */
        public Builder setMethod(@NonNull String method) {
            this.method = method;
            return this;
        }

        /**
         * Sets Content-Type header value
         *
         * @param contentType Non-null Content-Type header value
         *
         * @return {@link Builder}, great for chaining
         *
         * @see APIRequest#getContentType()
         */
        public Builder setContentType(@NonNull String contentType) {
            this.contentType = contentType;
            return this;
        }

        /**
         * Sets {@link java.net.http.HttpRequest.BodyPublisher}
         *
         * @param bodyPublisher Non-null {@link java.net.http.HttpRequest.BodyPublisher}
         *
         * @return {@link Builder}, great for chaining
         *
         * @see APIRequest#getBodyPublisher()
         */
        public Builder setBodyPublisher(@NonNull HttpRequest.BodyPublisher bodyPublisher) {
            this.bodyPublisher = bodyPublisher;
            return this;
        }

        /**
         * Sets {@link PathParameter}s
         *
         * @param pathParameters Non-null {@link PathParameter} array
         *
         * @return {@link Builder}, great for chaining
         *
         * @see APIRequest#getPathParameters()
         */
        public Builder setPathParameters(@NonNull PathParameter... pathParameters) {
            this.pathParameters = pathParameters;
            return this;
        }

        /**
         * Adds {@link PathParameter} to an existing {@link PathParameter} array (or creates a new array if you have not set any {@link PathParameter})
         *
         * @param pathParameter Non-null {@link PathParameter}
         *
         * @return {@link Builder}, great for chaining
         *
         * @see APIRequest#getPathParameters()
         */
        public Builder addPathParameter(@NonNull PathParameter pathParameter) {
            if (this.pathParameters == null || this.pathParameters.length == 0) {
                this.pathParameters = new PathParameter[1];
                this.pathParameters[0] = pathParameter;
                return this;
            }

            PathParameter[] newPathParameters = new PathParameter[this.pathParameters.length + 1];
            System.arraycopy(this.pathParameters, 0, newPathParameters, 0, this.pathParameters.length);
            newPathParameters[newPathParameters.length - 1] = pathParameter;
            this.pathParameters = newPathParameters;
            return this;
        }

        /**
         * Sets {@link Query}ies
         *
         * @param queries Non-null {@link Query} array
         *
         * @return {@link Builder}, great for chaining
         *
         * @see APIRequest#getQueries()
         */
        public Builder setQueries(@NonNull Query... queries) {
            this.queries = queries;
            return this;
        }

        /**
         * Adds {@link Query} to an existing {@link Query} array (or creates a new array if you have not set any {@link Query})
         *
         * @param query Non-null {@link Query}
         *
         * @return {@link Builder}, great for chaining
         *
         * @see APIRequest#getQueries()
         */
        public Builder addQuery(@NonNull Query query) {
            if (this.queries == null || this.queries.length == 0) {
                this.queries = new Query[1];
                this.queries[0] = query;
                return this;
            }

            Query[] newQueries = new Query[this.queries.length + 1];
            System.arraycopy(this.queries, 0, newQueries, 0, this.queries.length);
            newQueries[newQueries.length - 1] = query;
            this.queries = newQueries;
            return this;
        }

        /**
         * Sets {@link Consumer} which has {@link HttpRequest.Builder} argument
         *
         * @param httpRequestBuilderConsumer Non-null {@link HttpRequest.Builder} {@link Consumer}
         *
         * @return {@link Builder}, great for chaining
         *
         * @see APIRequest#processHttpRequestBuilder(HttpRequest.Builder)
         */
        public Builder setHttpRequestBuilder(@NonNull Consumer<HttpRequest.Builder> httpRequestBuilderConsumer) {
            this.httpRequestBuilderConsumer = httpRequestBuilderConsumer;
            return this;
        }

        /**
         * Sets {@link java.net.http.HttpResponse.BodyHandler}
         * @param bodyHandler Non-null {@link java.net.http.HttpResponse.BodyHandler}
         * @return {@link Builder}, great for chaining
         * @see APIRequest#getBodyHandler()
         */
        public Builder setBodyHandler(@NonNull HttpResponse.BodyHandler<?> bodyHandler) {
            this.bodyHandler = bodyHandler;
            return this;
        }

        /**
         * Sets {@link java.net.http.HttpClient.Redirect} policy
         * @param redirectPolicy Non-null {@link java.net.http.HttpClient.Redirect}
         * @return {@link Builder}, great for chaining
         * @see APIRequest#getRedirectPolicy()
         */
        public Builder setRedirectPolicy(@NonNull HttpClient.Redirect redirectPolicy) {
            this.redirectPolicy = redirectPolicy;
            return this;
        }
    }
}
