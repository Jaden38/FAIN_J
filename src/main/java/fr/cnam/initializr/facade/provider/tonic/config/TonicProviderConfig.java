package fr.cnam.initializr.facade.provider.tonic.config;

import fr.cnam.client.tonic.controller.rest.api.TonicProjectGenerationControllerApi;
import fr.cnam.client.tonic.controller.rest.invoker.ApiClient;
import fr.cnam.initializr.facade.config.InitializerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TonicProviderConfig {

    @Bean
    public ApiClient tonicApiClient(InitializerConfig config) {
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(config.getTonicUrl());
        return apiClient;
    }

    @Bean
    public TonicProjectGenerationControllerApi tonicApi(ApiClient apiClient) {
        return new TonicProjectGenerationControllerApi(apiClient);
    }
}