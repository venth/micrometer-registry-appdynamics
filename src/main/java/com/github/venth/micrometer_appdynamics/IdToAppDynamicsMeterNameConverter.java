package com.github.venth.micrometer_appdynamics;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.micrometer.core.instrument.Meter;

class IdToAppDynamicsMeterNameConverter implements MeterNameConverter {

    @Override
    public String apply(Meter.Id id) {
        return Stream
                .concat(
                        id.getTags()
                                .stream()
                                .map(tag -> tag.getKey() + "|" + tag.getValue()),
                        Stream.of(id.getName()))
                .collect(Collectors.joining("|"));
    }
}
