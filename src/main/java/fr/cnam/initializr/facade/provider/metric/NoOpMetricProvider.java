package fr.cnam.initializr.facade.provider.metric;

import fr.cnam.initializr.facade.business.model.Metric;
import fr.cnam.initializr.facade.business.port.MetricProvider;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NoOpMetricProvider implements MetricProvider {

    @Override
    public void recordMetric(Metric metric) {
        log.debug("No-op metric provider, metric not recorded: {}", metric);
    }
}