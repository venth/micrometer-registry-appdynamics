package com.github.venth.micrometer_appdynamics;

import java.util.function.Function;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.config.NamingConvention;
import io.micrometer.core.instrument.util.StringEscapeUtils;
import lombok.AllArgsConstructor;

@AllArgsConstructor
class AppDynamicsNamingConvention implements NamingConvention {

    private final Function<String, String> jsonEscaper;

    AppDynamicsNamingConvention() {
        this(StringEscapeUtils::escapeJson);
    }

    @Override
    public String name(String name, Meter.Type type, String baseUnit) {
        return jsonEscaper.apply(name);
    }

    @Override
    public String tagKey(String key) {
        return jsonEscaper.apply(key);
    }

    @Override
    public String tagValue(String value) {
        return jsonEscaper.apply(value);
    }
}
