package com.github.venth.micrometer_appdynamics

import io.micrometer.core.instrument.Meter
import io.micrometer.core.instrument.Tag
import io.micrometer.core.instrument.Tags
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

class IdToAppDynamicsMeterNameConverterTest extends Specification {

    @Unroll
    def """converts #meterName and #tags to #appDynamicsMeterName
           ignoring base unit, description and meter type"""() {
        given:
            def someBaseUnit = 'someBaseUnit'
            def someDescription = 'someDescription'

        and:
            def meterTags = Tags.of(tags)

            Meter.Type.values().each { someMeterType ->
                when:
                    def result = converter.apply(new Meter.Id(
                            meterName,
                            meterTags,
                            someBaseUnit,
                            someDescription,
                            someMeterType))
                then:
                    assert appDynamicsMeterName == result

            }
        where:
            meterName   | tags                  || appDynamicsMeterName
            'meterName' | []                    || "meterName"
            'meterName' | [TAG_1]               || "${TAG_1.key}|${TAG_1.value}|meterName"
            'meterName' | [TAG_1, TAG_2]        || "${TAG_1.key}|${TAG_1.value}|${TAG_2.key}|${TAG_2.value}|meterName"
            'meterName' | [TAG_1, TAG_2, TAG_3] || "${TAG_1.key}|${TAG_1.value}|${TAG_2.key}|${TAG_2.value}|${TAG_3.key}|${TAG_3.value}|meterName"
    }

    @Unroll
    def """converts #meterName and #tags to #appDynamicsMeterName
           sorting tags alphabetically"""() {
        given:
            def someBaseUnit = 'someBaseUnit'
            def someDescription = 'someDescription'

        and:
            def meterTags = Tags.of(tags)

            Meter.Type.values().each { someMeterType ->
                when:
                    def result = converter.apply(new Meter.Id(
                            meterName,
                            meterTags,
                            someBaseUnit,
                            someDescription,
                            someMeterType))
                then:
                    assert appDynamicsMeterName == result

            }
        where:
            meterName   | tags                  || appDynamicsMeterName
            'meterName' | [TAG_2, TAG_1]        || "${TAG_1.key}|${TAG_1.value}|${TAG_2.key}|${TAG_2.value}|meterName"
            'meterName' | [TAG_3, TAG_2, TAG_1] || "${TAG_1.key}|${TAG_1.value}|${TAG_2.key}|${TAG_2.value}|${TAG_3.key}|${TAG_3.value}|meterName"
    }

    private static Tag TAG_1 = tag('tag_1')

    private static Tag TAG_2 = tag('tag_2')

    private static Tag TAG_3 = tag('tag_3')

    private static Tag tag(String name) {
        Tag.of(name, "$name-value")
    }

    @Subject
    IdToAppDynamicsMeterNameConverter converter = new IdToAppDynamicsMeterNameConverter()
}
