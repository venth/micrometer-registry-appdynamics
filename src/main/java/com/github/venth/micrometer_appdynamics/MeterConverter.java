package com.github.venth.micrometer_appdynamics;

import java.util.function.Function;

import io.micrometer.core.instrument.Meter;

interface MeterConverter extends Function<Meter, AppDynamicsMeter> {

    @Override
    AppDynamicsMeter apply(Meter meter);
}
