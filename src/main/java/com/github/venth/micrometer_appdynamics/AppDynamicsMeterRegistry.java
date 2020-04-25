package com.github.venth.micrometer_appdynamics;

import java.util.List;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.step.StepMeterRegistry;
import io.micrometer.core.instrument.util.MeterPartition;
import io.micrometer.core.ipc.http.HttpSender;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

@Slf4j
public class AppDynamicsMeterRegistry extends StepMeterRegistry {

    private final AppDynamicsRegistryConfig config;

    private final MeterConverter meterConverter;

    private final HttpSender httpSender;

    public AppDynamicsMeterRegistry(AppDynamicsRegistryConfig config,
                                    Clock clock,
                                    MeterConverter meterConverter,
                                    ThreadFactory threadFactory,
                                    HttpSender httpSender) {
        super(config, clock);
        this.config = config;
        this.meterConverter = meterConverter;
        this.httpSender = httpSender;

        start(threadFactory);
    }

    @Override
    protected void publish() {
        val batchSize = config.batchSize();
        MeterPartition
                .partition(this, batchSize)
                .stream()
                .map(MeterBatch::of)
                .map(meterBatch -> meterBatch.prepareUsing(meterConverter))
                .forEach(meters -> sendMetrics(config.uri(), meters));
    }

    @Override
    protected TimeUnit getBaseTimeUnit() {
        return TimeUnit.MILLISECONDS;
    }

    private void sendMetrics(String agentLocation, PreparedMeterBatch meters) {
        try {
            httpSender
                    .post(agentLocation)
                    .withJsonContent(meters.meters)
                    .send()
                    .onSuccess(response -> log.debug("successfully sent {} metrics to app dynamics agent", meters.batchSize))
                    .onError(response -> log.error("failed to send metrics to app dynamics agent: {}", response.body()));
        } catch (Throwable e) {
            log.error("failed to send metrics to app dynamics agent: {}", meters, e);
        }
    }

    @AllArgsConstructor(staticName = "of")
    static class MeterBatch {

        public final List<Meter> meters;

        PreparedMeterBatch prepareUsing(MeterConverter meterConverter) {
            val prepared = "[" +
                    meters
                            .stream()
                            .map(meterConverter)
                            .map(AppDynamicsMeter::toString)
                            .collect(Collectors.joining(",")) +
                    "]";
            return PreparedMeterBatch.of(prepared, meters.size());
        }
    }

    @ToString
    @AllArgsConstructor(staticName = "of")
    static class PreparedMeterBatch {

        public final String meters;

        public final long batchSize;
    }
}
