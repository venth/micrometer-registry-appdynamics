package com.github.venth.micrometer_appdynamics;

import java.util.concurrent.TimeUnit;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.step.StepMeterRegistry;
import io.micrometer.core.instrument.step.StepRegistryConfig;
import io.micrometer.core.instrument.util.MeterPartition;

public class AppDynamicsMeterRegistry extends StepMeterRegistry {

    private final StepRegistryConfig config;

    private final MetricPublisher metricPublisher;

    public AppDynamicsMeterRegistry(StepRegistryConfig config, Clock clock, MetricPublisher metricPublisher) {
        super(config, clock);
        this.config = config;
        this.metricPublisher = metricPublisher;
    }

    @Override
    protected void publish() {
        MeterPartition
                .partition(this, config.batchSize())
                .forEach(metricPublisher::publish);
    }

    @Override
    protected TimeUnit getBaseTimeUnit() {
        return TimeUnit.MILLISECONDS;
    }
}
