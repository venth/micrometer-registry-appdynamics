package com.github.venth.micrometer_appdynamics


import io.micrometer.core.instrument.Meter
import io.micrometer.core.instrument.Tags
import io.micrometer.core.instrument.noop.NoopCounter
import io.micrometer.core.instrument.noop.NoopGauge
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import static com.github.venth.micrometer_appdynamics.AggregationType.OBSERVATION

class AppDynamicsMeterConverterTest extends Specification {

    def "converts gauge observation AppDynamics meter types"() {
        given:
            def gauge = gaugeMeter()

        and:
            _ * meterNameConverter.apply(gauge.getId()) >> 'some'

        when:
            def converted = converter
                    .apply(gauge)
                    .collect { it.aggregatorType }
        then:
            [OBSERVATION, OBSERVATION] == converted
    }

    def "adds to gauge metric name multiplier value"() {
        given:
            def floatMeter = 'floatMeter'
            def gauge = gaugeMeter(withName(floatMeter))
            _ * meterNameConverter.apply(gauge.getId()) >> floatMeter

        when:
            def converted = converter
                    .apply(gauge)
                    .collect { it.metricName }
        then:
            [floatMeter, "${floatMeter}__100".toString()] == converted
    }

    @Unroll
    def "multiplies gauge value: #measured by 100 and round arithmetically to: #emitted"() {
        given:
            def gauge = gaugeMeter(withValue(measured))

        and:
            _ * meterNameConverter.apply(gauge.getId()) >> 'some'

        when:
            def converted = converter
                    .apply(gauge)
                    .collect { it.value }
                    .collect { it.intValue() }
        then:
            [Math.round(measured).intValue(), emitted] == converted

        where:
            measured || emitted
            100.01d  || 10001
            0.011d   || 1
            0.014d   || 1
            0.015d   || 2
            0.016d   || 2
            0d       || 0
            1d       || 100
    }

    def "converts counter observation AppDynamics meter types"() {
        given:
            def counter = counterMeter()

        and:
            _ * meterNameConverter.apply(counter.getId()) >> 'some'

        when:
            def converted = converter
                    .apply(counter)
                    .collect { it.aggregatorType }
        then:
            [OBSERVATION, OBSERVATION] == converted
    }

    def "adds to counter metric name multiplier value"() {
        given:
            def floatMeter = 'floatMeter'
            def counter = counterMeter(withName(floatMeter))
            _ * meterNameConverter.apply(counter.getId()) >> floatMeter

        when:
            def converted = converter
                    .apply(counter)
                    .collect { it.metricName }
        then:
            [floatMeter, "${floatMeter}__100".toString()] == converted
    }

    @Unroll
    def "multiplies counter value: #measured by 100 and round arithmetically to: #emitted"() {
        given:
            def counter = counterMeter(withCounterValue(measured))

        and:
            _ * meterNameConverter.apply(counter.getId()) >> 'some'

        when:
            def converted = converter
                    .apply(counter)
                    .collect { it.value }
                    .collect { it.intValue() }
        then:
            [Math.round(measured).intValue(), emitted] == converted

        where:
            measured || emitted
            100.01d  || 10001
            0.011d   || 1
            0.014d   || 1
            0.015d   || 2
            0.016d   || 2
            0d       || 0
            1d       || 100
    }

    private counterMeter(Closure<? extends Meter>... behaviors) {
        meter(NoopCounter, Meter.Type.COUNTER, behaviors)
    }

    private gaugeMeter(Closure<? extends Meter>... behaviors) {
        meter(NoopGauge, Meter.Type.GAUGE, behaviors)
    }

    private meter(Class<? extends Meter> meterClazz, Meter.Type type, Closure<? extends Meter>... behaviors) {
        def id = new Meter.Id(
                meterClazz.getSimpleName(),
                Tags.empty(),
                'someBaseUnit',
                'someDescription',
                type)
        def meter = Spy(newMeter(meterClazz, id))

        behaviors?.each {
            it.call(meter)
        }

        meter
    }

    private static Closure<? extends Meter> withName(String name) {
        return { meter -> //noinspection GroovyAssignabilityCheck
            newMeter(meter.class, meter.getId().withName(name)) }
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    private Closure<? extends Meter> withValue(double value) {
        { meter ->
            _ * meter.value() >> value
        }
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    private Closure<? extends Meter> withCounterValue(double value) {
        { meter ->
            _ * meter.count() >> value
        }
    }

    private static newMeter(Class<? extends Meter> meterClazz, Meter.Id meterId) {
        meterClazz
                .getDeclaredConstructor(Meter.Id)
                .newInstance(meterId)
    }


    MeterNameConverter meterNameConverter = Mock()

    @Subject
    AppDynamicsMeterConverter converter = new AppDynamicsMeterConverter(meterNameConverter)
}
