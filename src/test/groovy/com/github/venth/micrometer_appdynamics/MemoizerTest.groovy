package com.github.venth.micrometer_appdynamics

import java.util.function.Function

import spock.lang.Specification

class MemoizerTest extends Specification {

    def "calls original function to store and return not yet memoized value"() {
        given:
            def input = 10
        and:
            def momoized = Memoizer.of(heavyFunction)
        when:
            momoized.apply(input)
        then:
            1 * heavyFunction.apply(input) >> 10
    }

    def "returns memoized value on the second call for the same input"() {
        given:
            def input = 10
        and:
            def momoized = Memoizer.of(heavyFunction)
        and:
            momoized.apply(input)
        when:
            momoized.apply(input)
        then:
            1 * heavyFunction.apply(input) >> 10
    }

    Function<Integer, Integer> heavyFunction = Mock()
}
