package com.car2go.endpoint2mock2;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Client which replaces base endpoint with another one forwards req.
 */
public class ReplaceEndpointClient extends OkHttpClient {
    private final String newEndpoint;
    private final OkHttpClient realClient;

    ReplaceEndpointClient(String newEndpoint,
                          OkHttpClient realClient) {
        this.newEndpoint = newEndpoint;
        this.realClient = realClient;
    }

    @Override
    public Call newCall(Request request) {
        return realClient.newCall(
                withReplacedEndpoint(request)
        );
    }

    private Request withReplacedEndpoint(Request originalRequest) {
        return originalRequest
                .newBuilder()
                .url(replaceEndpoint(originalRequest.url().toString()))
                .build();
    }

    private String replaceEndpoint(String originalRequestUrl) {
        HttpUrl url = HttpUrl.parse(originalRequestUrl);
        HttpUrl newEndpointUrl = HttpUrl.parse(newEndpoint);

        if(url == null){
            throw new IllegalArgumentException("Base url is not well formatted HTTP or HTTPS");
        }

        if(newEndpointUrl == null){
            throw new IllegalArgumentException("Mocked base url is not well formatted HTTP or HTTPS");
        }

        return url.newBuilder()
                .host(newEndpointUrl.host())
                .build()
                .toString();
    }
}
