package com.car2go.endpoint2mock2;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Client which either forwards request to a original client or if request is annotated with
 * {@link MockedEndpoint} calls the mock endpoint (assuming that it is hosted somewhere).
 */
public class MockableClient extends OkHttpClient {

    private final Registry registry;
    private final OkHttpClient realClient;
    private final ReplaceEndpointClient mockedClient;
    private final boolean shouldMock;

    MockableClient(Registry registry,
                   OkHttpClient realClient,
                   ReplaceEndpointClient mockedClient,
                   boolean shouldMock) {
        this.registry = registry;
        this.realClient = realClient;
        this.mockedClient = mockedClient;
        this.shouldMock = shouldMock;
    }

    @Override
    public Call newCall(Request request) {
        if (shouldMockRequest(request)) {
            return mockedClient.newCall(request);
        } else {
            return realClient.newCall(request);
        }
    }

    private boolean shouldMockRequest(Request request) {
        return registry.isInRegistry(request.url().toString()) && shouldMock;
    }

    /**
     * Creates builder for {@link MockableClient} with mocked base url.
     *
     * @param mockedBaseUrl base URL of the endpoint which will be used instead of real base URL for mocked
     *                      requests.
     */
    public static MockableClient.Builder baseUrl(String mockedBaseUrl) {
        return new Builder(mockedBaseUrl);
    }

    /**
     * Builder for {@link MockableClient}.
     */
    public static class Builder {
        private final String mockedBaseUrl;
        private OkHttpClient realClient;
        private boolean shouldMock;

        private Builder(String mockedBaseUrl) {
            this.mockedBaseUrl = mockedBaseUrl;
        }

        /**
         * @param realClient the client that will be used for non-mocked requests.
         */
        public Builder realClient(OkHttpClient realClient) {
            this.realClient = realClient;
            return this;
        }

        /**
         * There might be cases when you do not always want to mock requests even if they are
         * annotated with {@link MockedEndpoint}. This method allows you to decide whether such
         * requests should be mocked or not.
         *
         * @param shouldMock {@code true} if request should be mocked and {@code false} if it should not.
         */
        public Builder shouldMock(boolean shouldMock) {
            this.shouldMock = shouldMock;
            return this;
        }

        /**
         * @return new {@link MockableClient}.
         */
        public OkHttpClient build() {
            if (realClient == null) {
                realClient = new OkHttpClient();
            }

            return new MockableClient(
                    new Registry(),
                    realClient,
                    new ReplaceEndpointClient(mockedBaseUrl, realClient),
                    shouldMock
            );
        }
    }
}
