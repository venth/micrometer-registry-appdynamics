package com.github.venth.micrometer_appdynamics;

import lombok.AllArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor(staticName = "of")
class PreparedMeterBatch {

    public final String meters;

    public final long batchSize;
}