package com.car2go.endpoint2mock2;

import java.lang.reflect.Method;
import java.util.Set;

import static java.util.Collections.emptySet;

/**
 * Checks whether URL should be mocked or not.
 */
class Registry {

    private final Class<?> generatedMocksRegistryClass;
    private final Method getMockedEndpointsMethod;

    public Registry() {
        generatedMocksRegistryClass = loadMocksRegistryClass();
        getMockedEndpointsMethod = loadIsMockedMethod(generatedMocksRegistryClass);
    }

    private static Method loadIsMockedMethod(Class<?> registryClass) {
        if (registryClass == null) {
            return null;
        }

        try {
            return registryClass.getDeclaredMethod("getMockedEndpoints");
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private static Class<?> loadMocksRegistryClass() {
        try {
            return Class.forName("com.car2go.mock.FakeRegistry");
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * @return {@code true} if given URL is in registry. {@code false} if it is not.
     */
    public boolean isInRegistry(String url) {
        if (getMockedEndpointsMethod == null) {
            return false;
        }

        Set<String> mockedEndpoints = getMockedEndpoints();

        for (String endpoint : mockedEndpoints) {
            if (endpointMatches(endpoint, url)) {
                return true;
            }
        }

        return false;
    }

    private static boolean endpointMatches(String mockedEndpoint, String url) {
        String regEx = toRegEx(mockedEndpoint);

        return url.matches(".*" + regEx + queryArguments());
    }

    private static String queryArguments() {
        return "(\\?.*)?";
    }

    private static String toRegEx(String mockedEndpoint) {
        return replacePathParameters(mockedEndpoint);
    }

    private static String replacePathParameters(String mockedEndpoint) {
        return mockedEndpoint.replaceAll("\\{.*\\}", ".*");
    }

    @SuppressWarnings("unchecked")
    private Set<String> getMockedEndpoints() {
        try {
            return (Set<String>) getMockedEndpointsMethod.invoke(generatedMocksRegistryClass);
        } catch (Exception e) {
            return emptySet();
        }
    }

}
