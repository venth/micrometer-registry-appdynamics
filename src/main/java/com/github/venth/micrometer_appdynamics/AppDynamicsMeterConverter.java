package com.github.venth.micrometer_appdynamics;

import java.util.stream.Stream;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.FunctionTimer;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.TimeGauge;
import io.micrometer.core.instrument.Timer;
import lombok.AllArgsConstructor;

@AllArgsConstructor
class AppDynamicsMeterConverter implements MeterConverter {

    private final MeterNameConverter meterNameConverter;

    @Override
    public Stream<AppDynamicsMeter> apply(Meter meter) {
        return meter.match(
                this::convertMeter,
                this::convertMeter,
                this::convertMeter,
                this::convertMeter,
                this::convertMeter,
                this::convertMeter,
                this::convertMeter,
                this::convertMeter,
                this::convertMeter);
    }

    private Stream<AppDynamicsMeter> convertMeter(Gauge meter) {
        return Stream.of(AppDynamicsMeter.of(meterNameConverter.apply(
                meter.getId()),
                AggregationType.AVERAGE,
                Double.valueOf(meter.value()).longValue()));
    }

    private Stream<AppDynamicsMeter> convertMeter(Counter meter) {
        return Stream.empty();
    }

    private Stream<AppDynamicsMeter> convertMeter(Timer meter) {
        return Stream.empty();
    }

    private Stream<AppDynamicsMeter> convertMeter(DistributionSummary meter) {
        return Stream.empty();
    }

    private Stream<AppDynamicsMeter> convertMeter(LongTaskTimer meter) {
        return Stream.empty();
    }

    private Stream<AppDynamicsMeter> convertMeter(TimeGauge meter) {
        return Stream.empty();
    }

    private Stream<AppDynamicsMeter> convertMeter(FunctionCounter meter) {
        return Stream.empty();
    }

    private Stream<AppDynamicsMeter> convertMeter(FunctionTimer meter) {
        return Stream.empty();
    }

    private Stream<AppDynamicsMeter> convertMeter(Meter meter) {
        return Stream.empty();
    }
}
