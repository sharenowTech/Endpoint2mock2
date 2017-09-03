package com.car2go.endpoint2mock2;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import okhttp3.OkHttpClient;
import okhttp3.Request;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MockableClientTest {

    static final Request REQUEST =  new Request.Builder()
            .url("http://example.com")
            .build();

    @Mock
    Registry registry;
    @Mock
    OkHttpClient realClient;
    @Mock
    ReplaceEndpointClient mockClient;

    MockableClient testee;


    @Test
    public void notInRegistry_CallRealEndpoint() {
        // Given
        givenShouldMock(true);
        givenEndpointInRegistry(false);

        // When
        testee.newCall(REQUEST);

        // Then
        verify(realClient).newCall(REQUEST);
        verify(mockClient, never()).newCall(any(Request.class));
    }

    @Test
    public void inRegistryAndShouldMock_CallMockedEndpoint() {
        // Given
        givenShouldMock(true);
        givenEndpointInRegistry(true);

        // When
        testee.newCall(REQUEST);

        // Then
        verify(mockClient).newCall(REQUEST);
        verify(realClient, never()).newCall(any(Request.class));
    }

    @Test
    public void inRegistryButShouldNotMock_CallRealEndpoint() {
        // Given
        givenShouldMock(true);
        givenEndpointInRegistry(false);

        // When
        testee.newCall(REQUEST);

        // Then
        verify(realClient).newCall(REQUEST);
        verify(mockClient, never()).newCall(any(Request.class));
    }

    private void givenEndpointInRegistry(boolean result) {
        given(registry.isInRegistry(anyString()))
                .willReturn(result);
    }

    private void givenShouldMock(boolean shouldMock) {
        testee = new MockableClient(
                registry,
                realClient,
                mockClient,
                shouldMock
        );
    }
}