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
import lombok.val;

@AllArgsConstructor
class AppDynamicsMeterConverter implements MeterConverter {

    private static final String MULTIPLIER_SUFFIX = "__100";

    private static final int MULTIPLIER = 100;

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
        val meterName = meterNameConverter.apply(meter.getId());
        return Stream.of(
                observationOf(meter, meterName, 1),
                observationOf(meter, meterName + MULTIPLIER_SUFFIX, MULTIPLIER));
    }

    private Stream<AppDynamicsMeter> convertMeter(Counter meter) {
        val meterName = meterNameConverter.apply(meter.getId());
        return Stream.of(
                observationOf(meter, meterName, 1),
                observationOf(meter, meterName + MULTIPLIER_SUFFIX, MULTIPLIER)
        );
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

    private AppDynamicsMeter observationOf(Gauge meter, String meterName, int multiplier) {
        return AppDynamicsMeter.of(
                meterName,
                AggregationType.OBSERVATION,
                Math.round(meter.value() * multiplier));
    }

    private AppDynamicsMeter observationOf(Counter meter, String meterName, int multiplier) {
        return AppDynamicsMeter.of(
                meterName,
                AggregationType.OBSERVATION,
                Math.round(meter.count() * multiplier));
    }
}
