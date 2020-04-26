package com.github.venth.micrometer_appdynamics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.FunctionTimer;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.TimeGauge;
import io.micrometer.core.instrument.Timer;

class AppDynamicsMeterConverter implements MeterConverter {

    @Override
    public AppDynamicsMeter apply(Meter meter) {
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

    private AppDynamicsMeter convertMeter(Gauge meter) {
        return AppDynamicsMeter.empty();
    }

    private AppDynamicsMeter convertMeter(Counter meter) {
        return AppDynamicsMeter.empty();
    }

    private AppDynamicsMeter convertMeter(Timer meter) {
        return AppDynamicsMeter.empty();
    }

    private AppDynamicsMeter convertMeter(DistributionSummary meter) {
        return AppDynamicsMeter.empty();
    }

    private AppDynamicsMeter convertMeter(LongTaskTimer meter) {
        return AppDynamicsMeter.empty();
    }

    private AppDynamicsMeter convertMeter(TimeGauge meter) {
        return AppDynamicsMeter.empty();
    }

    private AppDynamicsMeter convertMeter(FunctionCounter meter) {
        return AppDynamicsMeter.empty();
    }

    private AppDynamicsMeter convertMeter(FunctionTimer meter) {
        return AppDynamicsMeter.empty();
    }

    private AppDynamicsMeter convertMeter(Meter meter) {
        return AppDynamicsMeter.empty();
    }
}
