package com.github.venth.micrometer_appdynamics;

import java.util.concurrent.TimeUnit;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.step.StepMeterRegistry;
import io.micrometer.core.instrument.step.StepRegistryConfig;

public class AppDynamicsMeterRegistry extends StepMeterRegistry {

    public AppDynamicsMeterRegistry(StepRegistryConfig config, Clock clock) {
        super(config, clock);
    }

    @Override
    protected void publish() {

    }

    @Override
    protected TimeUnit getBaseTimeUnit() {
        return TimeUnit.MILLISECONDS;
    }
}
