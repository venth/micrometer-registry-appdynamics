package com.github.venth.micrometer_appdynamics;

import io.micrometer.core.ipc.http.HttpSender;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
class HttpMeterBatchSender implements MeterBatchSender {

    private final AppDynamicsRegistryConfig config;

    private final HttpSender httpSender;

    @Override
    public void send(PreparedMeterBatch meters) {
        try {
            httpSender
                    .post(config.uri())
                    .withJsonContent(meters.meters)
                    .send()
                    .onSuccess(response -> log.debug("successfully sent {} metrics to app dynamics agent", meters.batchSize))
                    .onError(response -> log.error("failed to send metrics to app dynamics agent: {}", response.body()));
        } catch (Throwable e) {
            log.error("failed to send metrics to app dynamics agent: {}", meters, e);
        }
    }
}
