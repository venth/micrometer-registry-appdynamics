package com.github.venth.micrometer_appdynamics;

import java.util.Arrays;
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
import io.micrometer.core.instrument.distribution.HistogramSupport;
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
                this::convertHistogram,
                this::convertHistogram,
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

    private Stream<AppDynamicsMeter> convertHistogram(HistogramSupport meter) {
        val meterName = meterNameConverter.apply(meter.getId());
        val snapshot = meter.takeSnapshot();

        Stream<AppDynamicsMeter> common = Stream.of(
                observationOf(meterName + "__MEAN", snapshot.mean(), 1),
                observationOf(meterName + "__MEAN__100", snapshot.mean(), 100),
                observationOf(meterName + "__TOTAL", snapshot.total(), 1),
                observationOf(meterName + "__TOTAL__100", snapshot.total(), 100),
                observationOf(meterName + "__MAX", snapshot.max(), 1),
                observationOf(meterName + "__MAX__100", snapshot.max(), 100));

        Stream<AppDynamicsMeter> percentiles = Arrays
                .stream(snapshot.percentileValues())
                .flatMap(percentile -> {
                    val percentileName = meterName + "__" + Math.round(percentile.percentile() * 100) + "th";
                    return Stream.of(
                            observationOf(percentileName, percentile.value(), 1),
                            observationOf(percentileName + "__100", percentile.value(), 100));
                });

        return Stream.concat(common, percentiles);
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

    private AppDynamicsMeter observationOf(String meterName, double value, int multiplier) {
        return AppDynamicsMeter.of(
                meterName,
                AggregationType.OBSERVATION,
                Math.round(value * multiplier));
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
