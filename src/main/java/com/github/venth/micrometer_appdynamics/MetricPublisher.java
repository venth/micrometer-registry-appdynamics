package com.github.venth.micrometer_appdynamics;

import java.util.List;

import io.micrometer.core.instrument.Meter;

interface MetricPublisher {

    void publish(List<Meter> meters);
}
