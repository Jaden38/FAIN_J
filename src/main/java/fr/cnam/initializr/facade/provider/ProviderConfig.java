package fr.cnam.initializr.facade.provider;

import fr.cnam.client.tonic.controller.rest.api.TonicProjectGenerationControllerApi;
import fr.cnam.client.tonic.controller.rest.invoker.ApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProviderConfig {

    @Value("${initializer.tonic.url}")
    private String tonicUrl;

    @Bean
    public TonicProjectGenerationControllerApi tonicApi() {
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(tonicUrl);
        return new TonicProjectGenerationControllerApi(apiClient);
    }
}