package com.github.venth.micrometer_appdynamics

import java.util.function.Function

import io.micrometer.core.instrument.Meter
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

class AppDynamicsNamingConventionTest extends Specification {

    Function<String, String> escaper = { "${it}.sanitized".toString() }

    @Subject
    AppDynamicsNamingConvention namingConvention = new AppDynamicsNamingConvention(escaper)

    @Unroll
    def "sanitizes meter name for some base unit and #counterType"() {
        given:
            def someMeterName = 'some meter name'
        when:
            def sanitized = namingConvention.name(someMeterName, counterType as Meter.Type, 'someBaseUnit')
        then:
            sanitized == "${someMeterName}.sanitized"

        where:
            counterType << Meter.Type.values()
    }

    @Unroll
    def "sanitizes meter name for #counterType"() {
        given:
            def someMeterName = 'some meter name'
        when:
            def sanitized = namingConvention.name(someMeterName, counterType as Meter.Type)
        then:
            sanitized == "${someMeterName}.sanitized"

        where:
            counterType << Meter.Type.values()
    }

    def "sanitizes tag key"() {
        given:
            def someTagKey = 'some tag name'
        when:
            def sanitized = namingConvention.tagKey(someTagKey)
        then:
            sanitized == "${someTagKey}.sanitized"
    }

    def "sanitizes tag value"() {
        given:
            def someTagValue = 'some tag value'
        when:
            def sanitized = namingConvention.tagValue(someTagValue)
        then:
            sanitized == "${someTagValue}.sanitized"
    }

    def "uses as default json escaper for sanitization"() {
        when:
            def sanitized = new AppDynamicsNamingConvention().tagValue('"')
        then:
            sanitized == '\\"'
    }
}
