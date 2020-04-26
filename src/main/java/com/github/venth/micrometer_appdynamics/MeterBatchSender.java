package com.github.venth.micrometer_appdynamics;

interface MeterBatchSender {

    void send(PreparedMeterBatch meterBatch);
}
