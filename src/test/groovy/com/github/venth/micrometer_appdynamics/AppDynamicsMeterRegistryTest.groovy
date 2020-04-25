package com.github.venth.micrometer_appdynamics

import java.util.concurrent.ThreadFactory
import java.util.stream.IntStream

import io.micrometer.core.instrument.MockClock
import io.micrometer.core.ipc.http.HttpSender
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import static java.time.Duration.ofMillis

class AppDynamicsMeterRegistryTest extends Specification {

    MeterConverter metricConverter = Mock() {
        _ * apply(_) >> AppDynamicsMeter.of("someMeter", AggregationType.AVERAGE, 0L)
    }

    AppDynamicsRegistryConfig config = Mock() {
        step() >> ofMillis(10)
    }

    MockClock clock = new MockClock()

    ThreadFactory threadFactory = Mock()

    HttpSender httpSender = Mock()

    @Subject
    AppDynamicsMeterRegistry registry = new AppDynamicsMeterRegistry(config,
            clock,
            metricConverter,
            threadFactory,
            httpSender)

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
            sentBatches * httpSender.post(_)

        where:
            registeredMetrics | batchSize || sentBatches
            0                 | 10        || 0
            1                 | 10        || 1
            1                 | 1         || 1
            2                 | 1         || 2
            100               | 10        || 10
    }
}
