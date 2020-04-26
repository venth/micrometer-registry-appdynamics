package com.github.venth.micrometer_appdynamics

import java.util.stream.Collectors

import io.micrometer.core.instrument.Meter
import io.micrometer.core.instrument.Tags
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

class AppDynamicsMeterConverterTest extends Specification {

    @Unroll
    def "converts #meterClass to AppDynamics Meter"() {
        given:
            def meter = someMeter(this.class.forName("io.micrometer.core.instrument.noop.Noop$meterClass".toString()))
        when:
            def converted = converter
                    .apply(meter)
                    .map({ it.metricName })
                    .collect(Collectors.toList())
        then:
            [ "appdynamics|${meterClass.toUpperCase()}".toString() ] == converted
        where:
            meterClass << [
                    'Gauge', 'Counter', 'Timer', 'DistributionSummary', 'LongTaskTimer',
                    'TimeGauge', 'FunctionCounter', 'FunctionTimer', 'Meter']

    }

    void setup() {
        1 * meterNameConverter.apply(_) >> { args ->
            "appdynamics|${args[0].getType().name()}"
        }
    }

    private static someMeter(Class<? extends Meter> meterClazz) {
        def id = new Meter.Id(
                meterClazz.getSimpleName(),
                Tags.empty(),
                'someBaseUnit',
                'someDescription',
                Meter.Type.GAUGE)
        meterClazz
                .getDeclaredConstructor(Meter.Id)
                .newInstance(id)
    }

    MeterNameConverter meterNameConverter = Mock()

    @Subject
    AppDynamicsMeterConverter converter = new AppDynamicsMeterConverter(meterNameConverter)
}
