package fr.cnam.initializr.facade.provider.metric;

import fr.cnam.initializr.facade.business.model.ModuleMetric;
import fr.cnam.initializr.facade.business.port.MetricProvider;
import fr.cnam.initializr.facade.client.metric.controller.rest.api.ModuleApi;
import fr.cnam.initializr.facade.client.metric.controller.rest.model.ModuleResource;
import fr.cnam.initializr.facade.provider.mapper.MetricMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiMetricProvider implements MetricProvider {

    private final ModuleApi moduleApi;
    private final MetricMapper metricMapper;

    @Override
    public void recordMetric(ModuleMetric moduleMetric) {
        try {
            ModuleResource moduleResource = metricMapper.toModuleResource(moduleMetric);
            log.debug("Sending module resource to metrics service: {}", moduleResource);
            moduleApi.putModule(moduleResource);
        } catch (Exception e) {
            log.error("Failed to record metric", e);
        }
    }
}