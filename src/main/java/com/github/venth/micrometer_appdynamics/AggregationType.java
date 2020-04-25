package com.github.venth.micrometer_appdynamics;

enum AggregationType {
    // used for empty metrics
    UNKNOWN,
    // The average of all one-minute data points when adding it to the 10-minute or 60-minute granularity table
    AVERAGE,
    // The sum of all one-minute data points when adding it to the 10-minute or 60-minute granularity table
    SUM,
    // Last reported one-minute data point in that 10-minute or 60-minute interval
    OBSERVATION
}
