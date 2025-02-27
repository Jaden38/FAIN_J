package fr.cnam.initializr.facade.business.port;

import fr.cnam.initializr.facade.business.model.ModuleMetric;

public interface MetricProvider {
    void recordMetric(ModuleMetric moduleMetric);
}