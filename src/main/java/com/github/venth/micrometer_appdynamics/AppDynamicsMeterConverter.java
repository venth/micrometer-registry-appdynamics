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
                this::convert,
                this::convert,
                this::convert,
                this::convert,
                this::convert,
                this::convert,
                this::convert,
                this::convert,
                this::convert);
    }

    private AppDynamicsMeter convert(Gauge meter) {
        return AppDynamicsMeter.empty();
    }

    private AppDynamicsMeter convert(Counter meter) {
        return AppDynamicsMeter.empty();
    }

    private AppDynamicsMeter convert(Timer meter) {
        return AppDynamicsMeter.empty();
    }

    private AppDynamicsMeter convert(DistributionSummary meter) {
        return AppDynamicsMeter.empty();
    }

    private AppDynamicsMeter convert(LongTaskTimer meter) {
        return AppDynamicsMeter.empty();
    }

    private AppDynamicsMeter convert(TimeGauge meter) {
        return AppDynamicsMeter.empty();
    }

    private AppDynamicsMeter convert(FunctionCounter meter) {
        return AppDynamicsMeter.empty();
    }

    private AppDynamicsMeter convert(FunctionTimer meter) {
        return AppDynamicsMeter.empty();
    }

    private AppDynamicsMeter convert(Meter meter) {
        return AppDynamicsMeter.empty();
    }
}
