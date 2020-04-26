package com.github.venth.micrometer_appdynamics;

import java.time.Duration;

import io.micrometer.core.instrument.step.StepRegistryConfig;

public interface AppDynamicsRegistryConfig extends StepRegistryConfig {

    @Override
    default String prefix() {
        return "appdynamics";
    }

    /**
     * AppDynamics expects averages on all meters over 1 minut in a 10 or 60 minute window.
     * Reference: https://docs.appdynamics.com/display/PRO45/Standalone+Machine+Agent+HTTP+Listener
     * @return 10 minute step
     */
    @Override
    default Duration step() {
        String v = get(prefix() + ".step");
        return v == null ? Duration.ofMinutes(10) : Duration.parse(v);
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
