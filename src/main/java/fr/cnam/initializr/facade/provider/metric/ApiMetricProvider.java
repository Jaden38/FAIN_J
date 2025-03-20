package fr.cnam.initializr.facade.provider.metric;

import fr.cnam.initializr.facade.business.model.Metric;
import fr.cnam.initializr.facade.business.port.MetricProvider;
import fr.cnam.initializr.facade.client.metric.controller.rest.api.ModuleApi;
import fr.cnam.initializr.facade.client.metric.controller.rest.model.ModuleResource;
import fr.cnam.initializr.facade.provider.mapper.MetricMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ApiMetricProvider implements MetricProvider {

    private final ModuleApi moduleApi;
    private final MetricMapper metricMapper;

    @Override
    public void recordMetric(Metric metric) {
        ModuleResource moduleResource = metricMapper.toModuleResource(metric);
        moduleApi.putModule(moduleResource);
    }
}