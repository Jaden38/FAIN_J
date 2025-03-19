package fr.cnam.initializr.facade.business.port;

import fr.cnam.initializr.facade.business.model.Metric;

public interface MetricProvider {
    void recordMetric(Metric moduleMetric);
}