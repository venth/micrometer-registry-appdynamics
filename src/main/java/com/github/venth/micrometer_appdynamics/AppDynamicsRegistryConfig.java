package com.github.venth.micrometer_appdynamics;

import io.micrometer.core.instrument.step.StepRegistryConfig;

public interface AppDynamicsRegistryConfig extends StepRegistryConfig {

    @Override
    default String prefix() {
        return "appdynamics";
    }

    /**
     * @return The URI location of the Standalone Machine Agent HTTP Listener.
     * More information: https://docs.appdynamics.com/display/PRO45/Standalone+Machine+Agent+HTTP+Listener
     */
    default String uri() {
        String v = get(prefix() + ".uri");
        return v == null ? "http://localhost:8293" : v;
    }
}
