package com.github.venth.micrometer_appdynamics

import io.micrometer.core.instrument.Meter
import io.micrometer.core.instrument.Tags
import spock.lang.Specification
import spock.lang.Subject

class JsonEscapingMeterNameConverterTest extends Specification {

    def "escapes json special characters"() {
        given:
            def idWithJsonCharacters = new Meter.Id(
                    'someMeter',
                    Tags.empty(),
                    'someBaseUnit',
                    'someDescription',
                    Meter.Type.COUNTER)
            _ * decoratedConverter.apply(idWithJsonCharacters) >> '"'
        when:
            def escaped = converter.apply(idWithJsonCharacters)
        then:
            '\\"' == escaped
    }

    MeterNameConverter decoratedConverter = Mock()

    @Subject
    JsonEscapingMeterNameConverter converter = new JsonEscapingMeterNameConverter(decoratedConverter)
}
