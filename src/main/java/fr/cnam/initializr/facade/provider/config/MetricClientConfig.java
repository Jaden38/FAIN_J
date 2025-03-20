package fr.cnam.initializr.facade.provider.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.cnam.initializr.facade.business.port.MetricProvider;
import fr.cnam.initializr.facade.client.metric.controller.rest.api.ModuleApi;
import fr.cnam.initializr.facade.client.metric.controller.rest.invoker.ApiClient;
import fr.cnam.initializr.facade.provider.mapper.MetricMapper;
import fr.cnam.initializr.facade.provider.metric.ApiMetricProvider;
import fr.cnam.initializr.facade.provider.metric.NoOpMetricProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.DateFormat;

@Configuration
@Slf4j
public class MetricClientConfig {

    @Value("${initializer.metric.url}")
    private String metricServiceUrl;
    private ObjectMapper objectMapper;

    @Bean
    @ConditionalOnProperty(name = "initializer.metric.mock", havingValue = "false", matchIfMissing = true)
    public ModuleApi moduleApi(ObjectMapper objectMapper) {
        log.info("Configuring ModuleApi with metric service URL: {}", metricServiceUrl);

        DateFormat dateFormat = ApiClient.createDefaultDateFormat();
        ApiClient apiClient = new ApiClient(objectMapper, dateFormat);
        apiClient.setBasePath(metricServiceUrl);

        ModuleApi moduleApi = new ModuleApi(apiClient);
        log.debug("ModuleApi configured with baseUrl: {}", apiClient.getBasePath());

        return moduleApi;
    }

    @Bean
    @ConditionalOnProperty(name = "initializer.metric.mock", havingValue = "false", matchIfMissing = true)
    public MetricProvider metricProvider(ModuleApi moduleApi, MetricMapper metricMapper) {
        return new ApiMetricProvider(moduleApi, metricMapper);
    }


    @Bean
    @ConditionalOnProperty(name = "initializer.metric.mock", havingValue = "true")
    public MetricProvider noopMetricProvider() {
        log.warn("Metric service URL is not configured. Using no-op implementation for MetricProvider.");
        return new NoOpMetricProvider();
    }
}