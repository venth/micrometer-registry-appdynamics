package com.github.venth.micrometer_appdynamics;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.util.StringEscapeUtils;
import lombok.AllArgsConstructor;

@AllArgsConstructor
class JsonEscapingMeterNameConverter implements MeterNameConverter {

    private final MeterNameConverter decorated;

    @Override
    public String apply(Meter.Id id) {
        return StringEscapeUtils.escapeJson(decorated.apply(id));
    }
}
