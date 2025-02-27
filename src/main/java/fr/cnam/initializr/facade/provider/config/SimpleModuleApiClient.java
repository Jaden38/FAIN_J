package fr.cnam.initializr.facade.provider.config;

import fr.cnam.initializr.facade.client.metric.controller.rest.api.ModuleApi;
import fr.cnam.initializr.facade.client.metric.controller.rest.model.ModuleResource;
import fr.cnam.initializr.facade.client.metric.controller.rest.model.ResponseOkAvecModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

@Slf4j
@RequiredArgsConstructor
public class SimpleModuleApiClient implements ModuleApi {
    private final RestClient restClient;

    @Override
    public ResponseEntity<ResponseOkAvecModule> putModule(ModuleResource moduleResource) {
        return restClient.put()
                .uri("/modules")
                .body(moduleResource)
                .retrieve()
                .toEntity(ResponseOkAvecModule.class);
    }
}