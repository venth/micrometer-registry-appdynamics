package com.github.venth.micrometer_appdynamics

import io.micrometer.core.instrument.Meter
import io.micrometer.core.instrument.Tags
import io.micrometer.core.instrument.distribution.CountAtBucket
import io.micrometer.core.instrument.distribution.HistogramSnapshot
import io.micrometer.core.instrument.distribution.ValueAtPercentile
import io.micrometer.core.instrument.noop.NoopCounter
import io.micrometer.core.instrument.noop.NoopGauge
import io.micrometer.core.instrument.noop.NoopTimer
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

    def "converts timer observation AppDynamics meter types"() {
        given:
            def timer = timerMeter()

        and:
            _ * meterNameConverter.apply(timer.getId()) >> 'some'

        when:
            def converted = converter
                    .apply(timer)
                    .collect { it.aggregatorType }
        then:
            converted.size() > 0
        and:
            converted.each {
                assert OBSERVATION == it
            }
    }

    def "adds to each timer metric name multiplier value"() {
        given:
            def someTimeMeter = 'someTimeMeter'
            def timer = timerMeter(withName(someTimeMeter))
            _ * meterNameConverter.apply(timer.getId()) >> someTimeMeter

        when:
            def converted = converter
                    .apply(timer)
                    .collect { it.metricName }
                    .findAll { it.endsWith('__100') }
        then:
            converted.size() > 0

        and:
            converted?.each {
                assert it.endsWith('__100')
            }
    }

    def "emits timer: [mean, max time, total time]"() {
        given:
            def timerName = 'timerName'
            def timer = timerMeter(withName(timerName))
            _ * meterNameConverter.apply(timer.getId()) >> timerName

        and:
            _ * timer.takeSnapshot() >> histogramSnapshot()

        and:
            def statistics = [
                    '__MEAN',
                    '__MAX',
                    '__TOTAL']

        when:
            def converted = converter
                    .apply(timer)
                    .collect { it.metricName }
        then:
            converted.size() > 0

        and:
            statistics.each { statistic ->
                assert converted.findAll { it.contains(statistic) }.size() == 2
            }
    }

    def "emits timer percentiles multiplied and not multiplied"() {
        given:
            def timerName = 'timerName'
            def timer = timerMeter(withName(timerName))
            _ * meterNameConverter.apply(timer.getId()) >> timerName

        and:
            def percentiles = new ValueAtPercentile[]{
                    percentileOf(0.5d, 50),
                    percentileOf(0.95d, 95),
                    percentileOf(0.3d, 30),
            }

            _ * timer.takeSnapshot() >> histogramSnapshot(withPercentiles(percentiles))

        when:
            def converted = converter
                    .apply(timer)
                    .collect { it.metricName }
        then:
            converted.size() > 0

        and:
            percentiles.each { percentile ->
                assert converted.findAll {
                    it.contains("__${Math.round(percentile.percentile() * 100)}th")
                }.size() == 2
            }
    }

    @Unroll
    def """multiplies each timer statistic value: #measured by 100 and round arithmetically to: #emittedMultiplied and
           round arithmetically to: #emittedOriginally"""() {
        given:
            def timer = timerMeter(withCounterValue(measured))

        and:
            _ * meterNameConverter.apply(timer.getId()) >> 'some'

        and:
            def histogramSnapshot = histogramSnapshot(
                    withTotal(measured),
                    withMax(measured),
                    withMean(measured),
                    withPercentiles(percentileOf(0.5d, measured), percentileOf(0.95d, measured)))
            _ * timer.takeSnapshot() >> histogramSnapshot

        when:
            def convertedMultiplied = converter
                    .apply(timer)
                    .findAll { it.metricName.endsWith('__100') }
                    .collect { it.value }
                    .collect { it.intValue() }

            def convertedOriginally = converter
                    .apply(timer)
                    .findAll { !it.metricName.endsWith('__100') }
                    .collect { it.value }
                    .collect { it.intValue() }
        then:
            verifyAll {
                convertedMultiplied.every {
                    it == emittedMultiplied
                }
                convertedOriginally.every {
                    it == emittedOriginally
                }
            }

        where:
            measured || emittedMultiplied | emittedOriginally
            100.01d  || 10001             | 100
            0.011d   || 1                 | 0
            0.014d   || 1                 | 0
            0.015d   || 2                 | 0
            0.016d   || 2                 | 0
            0d       || 0                 | 0
            1d       || 100               | 1
            0.6d     || 60                | 1
    }

    private static withPercentiles(ValueAtPercentile... percentiles) {
        { snapshot ->
            new HistogramSnapshot(
                    snapshot.count(),
                    snapshot.total(),
                    snapshot.max(),
                    percentiles,
                    snapshot.histogramCounts(),
                    snapshot::outputSummary)
        }
    }

    private static withTotal(double total) {
        { snapshot ->
            new HistogramSnapshot(
                    snapshot.count(),
                    total,
                    snapshot.max(),
                    snapshot.percentileValues(),
                    snapshot.histogramCounts(),
                    snapshot::outputSummary)
        }
    }

    private static withMax(double max) {
        { snapshot ->
            new HistogramSnapshot(
                    snapshot.count(),
                    snapshot.total(),
                    max,
                    snapshot.percentileValues(),
                    snapshot.histogramCounts(),
                    snapshot::outputSummary)
        }
    }

    private static withMean(double mean) {
        { snapshot ->
            new HistogramSnapshot(
                    1,
                    snapshot.total(),
                    snapshot.max(),
                    snapshot.percentileValues(),
                    snapshot.histogramCounts(),
                    snapshot::outputSummary)
        }
    }

    private static HistogramSnapshot histogramSnapshot(Closure<HistogramSnapshot>... behaviors) {
        def snapshot = new HistogramSnapshot(
                0l,
                0d,
                0d,
                new ValueAtPercentile[0],
                new CountAtBucket[0],
                null)
        behaviors.each {
            snapshot = it.call(snapshot)
        }

        snapshot
    }

    private static ValueAtPercentile percentileOf(double percentile, double value) {
        new ValueAtPercentile(percentile, value)
    }

    private io.micrometer.core.instrument.Timer timerMeter(Closure<? extends Meter>... behaviors) {
        meter(NoopTimer, Meter.Type.COUNTER, behaviors) as io.micrometer.core.instrument.Timer
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
            newMeter(meter.class, meter.getId().withName(name))
        }
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
