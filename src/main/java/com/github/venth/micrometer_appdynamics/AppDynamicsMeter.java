package com.github.venth.micrometer_appdynamics;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
class AppDynamicsMeter {

    static AppDynamicsMeter empty() {
        return AppDynamicsMeter.of("unknown", AggregationType.UNKNOWN, 0L);
    }

    private final String metricName;

    private final AggregationType aggregatorType;

    private final Long value;

    @Override
    public String toString() {
        return "{\"metricName\": \"" + metricName + "\", \"aggregatorType\": \"" + aggregatorType.name() + "\"," +
                "\"value\": " + value + "}";
    }
}
