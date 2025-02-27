package fr.cnam.initializr.facade.provider.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.cnam.initializr.facade.client.metric.controller.rest.api.ModuleApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClient;

@Configuration
@Slf4j
public class MetricClientConfig {

    @Value("${initializer.metric.url:#{null}}")
    private String metricServiceUrl;

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public ModuleApi moduleApi() {
        if (metricServiceUrl == null) {
            log.warn("Metric service URL is not configured. Using no-op implementation for ModuleApi.");
            return new ModuleApi() {};
        }

        log.info("Configuring ModuleApi with metric service URL: {}", metricServiceUrl);

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);

        RestClient restClient = RestClient.builder()
                .baseUrl(metricServiceUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .requestInterceptor((request, body, execution) -> {
                    log.debug("Sending request to: {} {}", request.getMethod(), request.getURI());
                    log.debug("Request headers: {}", request.getHeaders());
                    log.debug("Request body: {}", new String(body));
                    return execution.execute(request, body);
                })
                .messageConverters(converters -> converters.add(0, converter)) // Add our custom converter with priority
                .build();
        return new SimpleModuleApiClient(restClient);
    }
}