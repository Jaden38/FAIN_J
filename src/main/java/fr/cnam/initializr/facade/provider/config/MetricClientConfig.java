package fr.cnam.initializr.facade.provider.config;

import fr.cnam.initializr.facade.client.metric.controller.rest.api.ModuleApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class MetricClientConfig {

    @Value("${initializer.metric.url:#{null}}")
    private String metricServiceUrl;

    @Bean
    public ModuleApi moduleApi() {
        if (metricServiceUrl == null) {
            return new ModuleApi() {};
        }

        RestClient restClient = RestClient.builder()
                .baseUrl(metricServiceUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        RestClientAdapter adapter = RestClientAdapter.create(restClient);

        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter)
                .build();

        return factory.createClient(ModuleApi.class);
    }
}