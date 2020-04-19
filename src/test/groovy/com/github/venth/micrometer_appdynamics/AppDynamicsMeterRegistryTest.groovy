package com.github.venth.micrometer_appdynamics


import java.util.stream.IntStream

import io.micrometer.core.instrument.MockClock
import io.micrometer.core.instrument.step.StepRegistryConfig
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import static java.time.Duration.ofMillis

class AppDynamicsMeterRegistryTest extends Specification {

    MetricPublisher metricSender = Mock()

    StepRegistryConfig config = Mock() {
        step() >> ofMillis(10)
    }

    MockClock clock = new MockClock()

    @Subject
    AppDynamicsMeterRegistry registry = new AppDynamicsMeterRegistry(config, clock, metricSender)

    @Unroll
    def "sends #sentBatches metrics batches for batch size: #batchSize and registered metrics: #registeredMetrics"() {
        given:
            IntStream
                    .range(0, registeredMetrics)
                    .forEach {
                        registry.counter("counter-$it")
                    }

        and:
            config.batchSize() >> batchSize

        when:
            registry.publish()

        then:
            sentBatches * metricSender.publish(_)

        where:
            registeredMetrics | batchSize || sentBatches
            0                 | 10        || 0
            1                 | 10        || 1
            1                 | 1         || 1
            2                 | 1         || 2
            100               | 10        || 10
    }
}
