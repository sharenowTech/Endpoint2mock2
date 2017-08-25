package com.car2go.mock;

import java.util.HashSet;
import java.util.Set;

/**
 * Example of generated fake registry. Used only for tests.
 */
@SuppressWarnings("unchecked")
public class FakeRegistry {
    private static final Set registry = new HashSet();

    public static void setRegistry(Set<String> registry) {
        FakeRegistry.registry.clear();
        FakeRegistry.registry.addAll(registry);
    }

    public static Set getMockedEndpoints() {
        return registry;
    }

}
