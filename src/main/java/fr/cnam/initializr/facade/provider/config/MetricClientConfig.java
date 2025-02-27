package fr.cnam.initializr.facade.provider.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.cnam.initializr.facade.business.port.MetricProvider;
import fr.cnam.initializr.facade.client.metric.controller.rest.api.ModuleApi;
import fr.cnam.initializr.facade.client.metric.controller.rest.invoker.ApiClient;
import fr.cnam.initializr.facade.client.metric.controller.rest.model.ModuleResource;
import fr.cnam.initializr.facade.client.metric.controller.rest.model.ResponseOkAvecModule;
import fr.cnam.initializr.facade.provider.mapper.MetricMapper;
import fr.cnam.initializr.facade.provider.metric.ApiMetricProvider;
import fr.cnam.initializr.facade.provider.metric.NoOpMetricProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.DateFormat;

@Configuration
@Slf4j
public class MetricClientConfig {

    private final String metricServiceUrl;
    private final ObjectMapper objectMapper;

    public MetricClientConfig(
            @Value("${initializer.metric.url:#{null}}") String metricServiceUrl,
            ObjectMapper objectMapper) {
        this.metricServiceUrl = metricServiceUrl;
        this.objectMapper = objectMapper;
    }

    @Bean
    public ModuleApi moduleApi() {
        if (metricServiceUrl == null) {
            log.warn("Metric service URL is not configured. Using no-op implementation for ModuleApi.");
            return new ModuleApi() {
                @Override
                public ResponseOkAvecModule putModule(ModuleResource moduleResource) {
                    return new ResponseOkAvecModule();
                }
            };
        }

        log.info("Configuring ModuleApi with metric service URL: {}", metricServiceUrl);

        DateFormat dateFormat = ApiClient.createDefaultDateFormat();
        ApiClient apiClient = new ApiClient(objectMapper, dateFormat);
        apiClient.setBasePath(metricServiceUrl);

        ModuleApi moduleApi = new ModuleApi(apiClient);
        log.debug("ModuleApi configured with baseUrl: {}", apiClient.getBasePath());

        return moduleApi;
    }

    @Bean
    public MetricProvider metricProvider(ModuleApi moduleApi, MetricMapper metricMapper) {
        if (metricServiceUrl == null) {
            log.warn("Metric service URL is not configured. Using no-op implementation for MetricProvider.");
            return new NoOpMetricProvider();
        }
        return new ApiMetricProvider(moduleApi, metricMapper);
    }
}