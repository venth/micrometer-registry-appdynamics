package com.github.venth.micrometer_appdynamics;

import java.util.function.Function;
import java.util.stream.Stream;

import io.micrometer.core.instrument.Meter;

interface MeterConverter extends Function<Meter, Stream<AppDynamicsMeter>> {

}
