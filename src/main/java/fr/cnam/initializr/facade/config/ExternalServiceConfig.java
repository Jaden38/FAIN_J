package fr.cnam.initializr.facade.config;

import fr.cnam.client.tonic.controller.rest.api.TonicProjectGenerationControllerApi;
import fr.cnam.client.tonic.controller.rest.invoker.ApiClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExternalServiceConfig {
    @Bean
    public TonicProjectGenerationControllerApi tonicApi() {
        ApiClient apiClient = new ApiClient();
        // Configure base URL and other settings
        return new TonicProjectGenerationControllerApi(apiClient);
    }
}