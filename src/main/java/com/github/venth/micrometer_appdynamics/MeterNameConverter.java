package com.github.venth.micrometer_appdynamics;

import java.util.function.Function;

import io.micrometer.core.instrument.Meter;

interface MeterNameConverter extends Function<Meter.Id, String> {

}
