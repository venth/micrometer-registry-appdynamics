package com.github.venth.micrometer_appdynamics

import java.util.stream.Stream

import io.micrometer.core.instrument.Meter
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class AppDynamicsMeterRegistryMeterBatchTest extends Specification {

    @Unroll
    def "prepares json batch using #meters"() {
        given:
            def batch = AppDynamicsMeterRegistry.MeterBatch.of(meters)

        and:
            _ * meterConverter.apply(_) >> { Stream.of(convertedMeter) }

        when:
            def prepared = batch.prepareUsing(meterConverter)

        then:
            prepared.meters == json

        and:
            prepared.batchSize == batchSize

        where:
            meters                            || json                                                | batchSize
            []                                || '[]'                                                | 0
            [someMeter]                       || "[$convertedMeter]"                                 | 1
            [someMeter, someMeter]            || "[$convertedMeter,$convertedMeter]"                 | 2
            [someMeter, someMeter, someMeter] || "[$convertedMeter,$convertedMeter,$convertedMeter]" | 3
    }

    private MeterConverter meterConverter = Mock()

    @Shared
    private Meter someMeter = Mock()

    @Shared
    private AppDynamicsMeter convertedMeter =
            AppDynamicsMeter.of("someMeter", AggregationType.AVERAGE, 0L)
}
