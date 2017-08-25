package com.car2go.endpoint2mock2;


import org.junit.Test;

import java.util.HashSet;

import com.car2go.mock.FakeRegistry;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RegistryTest {

    Registry testee = new Registry();

    @Test
    public void inRegistry_Yes_PartialMatch() throws Exception {
        // Given
        FakeRegistry.setRegistry(new HashSet<>(asList(
                "/path3/path4",
                "/path1/path2"
        )));

        // When
        boolean result = testee.isInRegistry("http://example.com/path1/path2/path3/path4");

        // Then
        assertTrue(result);
    }

    @Test
    public void inRegistry_Yes_PathParameters() throws Exception {
        // Given
        FakeRegistry.setRegistry(new HashSet<>(asList(
                "/path3/{parameterA}/{parameterB}",
                "/path1/path2"
        )));

        // When
        boolean result = testee.isInRegistry("http://example.com/path1/path3/a/b");

        // Then
        assertTrue(result);
    }

    @Test
    public void inRegistry_Yes_QueryParameters() throws Exception {
        // Given
        FakeRegistry.setRegistry(new HashSet<>(asList(
                "/path3/path4",
                "/path1/path2"
        )));

        // When
        boolean result = testee.isInRegistry("http://example.com/path3/path4?someArg=a&otherArg=b");

        // Then
        assertTrue(result);
    }

    @Test
    public void inRegistry_No() throws Exception {
        // Given
        FakeRegistry.setRegistry(new HashSet<>(asList(
                "/path3/path4",
                "/path1/path2"
        )));

        // When
        boolean result = testee.isInRegistry("http://example.com/path3/something");

        // Then
        assertFalse(result);
    }
}

