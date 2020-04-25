package com.github.venth.micrometer_appdynamics;

import java.util.List;
import java.util.function.Function;

import io.micrometer.core.instrument.Meter;

@FunctionalInterface
interface MetricPublisher extends Function<List<Meter>, List<AppDynamicsMeter>> {

    List<AppDynamicsMeter> apply(List<Meter> meters);
}
