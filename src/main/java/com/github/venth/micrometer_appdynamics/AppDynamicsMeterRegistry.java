package com.github.venth.micrometer_appdynamics;

import java.util.List;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.step.StepMeterRegistry;
import io.micrometer.core.instrument.util.MeterPartition;
import lombok.AllArgsConstructor;
import lombok.val;

public class AppDynamicsMeterRegistry extends StepMeterRegistry {

    private final AppDynamicsRegistryConfig config;

    private final MeterConverter meterConverter;

    private final MeterBatchSender meterBatchSender;

    public AppDynamicsMeterRegistry(AppDynamicsRegistryConfig config,
                                    Clock clock,
                                    MeterConverter meterConverter,
                                    ThreadFactory threadFactory,
                                    MeterBatchSender meterBatchSender) {
        super(config, clock);
        this.config = config;
        this.meterConverter = meterConverter;
        this.meterBatchSender = meterBatchSender;

        start(threadFactory);
    }

    @Override
    protected void publish() {
        MeterPartition
                .partition(this, config.batchSize())
                .stream()
                .map(MeterBatch::of)
                .map(meterBatch -> meterBatch.prepareUsing(meterConverter))
                .forEach(this::sendMetrics);
    }

    @Override
    protected TimeUnit getBaseTimeUnit() {
        return TimeUnit.MILLISECONDS;
    }

    private void sendMetrics(PreparedMeterBatch meters) {
        meterBatchSender.send(meters);
    }

    @AllArgsConstructor(staticName = "of")
    static class MeterBatch {

        public final List<Meter> meters;

        PreparedMeterBatch prepareUsing(MeterConverter meterConverter) {
            val prepared = "[" +
                    meters
                            .stream()
                            .flatMap(meterConverter)
                            .map(AppDynamicsMeter::toString)
                            .collect(Collectors.joining(",")) +
                    "]";
            return PreparedMeterBatch.of(prepared, meters.size());
        }
    }
}
