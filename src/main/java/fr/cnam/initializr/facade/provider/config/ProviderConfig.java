package fr.cnam.initializr.facade.provider.config;

import fr.cnam.client.tonic.controller.rest.api.TonicProjectGenerationControllerApi;
import fr.cnam.client.tonic.controller.rest.invoker.ApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.web.client.RestClient;

import java.util.Arrays;

@Configuration
public class ProviderConfig {

    @Value("${initializer.tonic.url}")
    private String tonicUrl;

    @Bean
    public TonicProjectGenerationControllerApi tonicApi() {
        ByteArrayHttpMessageConverter byteArrayConverter = new ByteArrayHttpMessageConverter();
        byteArrayConverter.setSupportedMediaTypes(Arrays.asList(
                MediaType.APPLICATION_OCTET_STREAM,
                MediaType.parseMediaType("application/zip")
        ));

        RestClient restClient = RestClient.builder()
                .baseUrl(tonicUrl)
                .messageConverters(converters -> {
                    converters.add(0, byteArrayConverter);
                })
                .build();

        ApiClient apiClient = new ApiClient(restClient);
        apiClient.setBasePath(tonicUrl);

        return new TonicProjectGenerationControllerApi(apiClient);
    }
}