package dev.mayuna.simpleapi;

import lombok.Getter;
import lombok.NonNull;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Consumer;

public interface APIRequest {

    String getEndpoint();

    String getMethod();

    default PathParameter[] getPathParameters() {
        return null;
    }

    default Query[] getQueries() {
        return null;
    }

    default void processHttpRequestBuilder(HttpRequest.Builder httpRequestBuilder) {
    }

    default HttpResponse.BodyHandler<?> getBodyHandler() {
        return HttpResponse.BodyHandlers.ofString();
    }

    default String getContentType() {
        return null;
    }

    default HttpRequest.BodyPublisher getBodyPublisher() {
        return HttpRequest.BodyPublishers.noBody();
    }

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

        public Builder() {

        }

        public APIRequest build() {
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
            };
        }

        public Builder setEndpoint(@NonNull String endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        public Builder setMethod(@NonNull String method) {
            this.method = method;
            return this;
        }

        public Builder setContentType(@NonNull String contentType) {
            this.contentType = contentType;
            return this;
        }

        public Builder setBodyPublisher(@NonNull HttpRequest.BodyPublisher bodyPublisher) {
            this.bodyPublisher = bodyPublisher;
            return this;
        }

        public Builder setPathParameters(@NonNull PathParameter... pathParameters) {
            this.pathParameters = pathParameters;
            return this;
        }

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

        public Builder setQueries(@NonNull Query... queries) {
            this.queries = queries;
            return this;
        }

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

        public Builder setHttpRequestBuilder(@NonNull Consumer<HttpRequest.Builder> httpRequestBuilderConsumer) {
            this.httpRequestBuilderConsumer = httpRequestBuilderConsumer;
            return this;
        }

        public Builder setBodyHandler(@NonNull HttpResponse.BodyHandler<?> bodyHandler) {
            this.bodyHandler = bodyHandler;
            return this;
        }
    }
}
