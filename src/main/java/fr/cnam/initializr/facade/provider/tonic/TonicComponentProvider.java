package fr.cnam.initializr.facade.provider.tonic;

import fr.cnam.client.tonic.controller.rest.api.TonicProjectGenerationControllerApi;
import fr.cnam.client.tonic.controller.rest.model.ProjectRequest;
import fr.cnam.initializr.facade.business.model.ComponentArchive;
import fr.cnam.initializr.facade.business.model.Component;
import fr.cnam.initializr.facade.business.model.StarterKitBusiness;
import fr.cnam.initializr.facade.business.port.ComponentProvider;
import fr.cnam.initializr.facade.controller.rest.model.StarterKitType;
import fr.cnam.initializr.facade.provider.mapper.TonicMapper;
import fr.cnam.initializr.facade.provider.service.TonicFeaturesService;
import fr.cnam.toni.starter.core.exceptions.CommonProblemType;
import fr.cnam.toni.starter.core.exceptions.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.Collections;
import java.util.List;

@org.springframework.stereotype.Component
@RequiredArgsConstructor
public class TonicComponentProvider implements ComponentProvider {

    private final TonicProjectGenerationControllerApi tonicApi;
    private final TonicMapper mapper;
    private final TonicFeaturesService featuresService;

    @Override
    public ComponentArchive generateComponent(Component businessRequest) {
        ProjectRequest clientRequest = mapper.toClientRequest(businessRequest);

        try {
            RestClient.ResponseSpec responseSpec = tonicApi.getProjectZipWithResponseSpec(
                    clientRequest.getDependencies(),
                    clientRequest.getGroupId(),
                    clientRequest.getArtifactId(),
                    clientRequest.getName(),
                    clientRequest.getType(),
                    clientRequest.getDescription(),
                    clientRequest.getVersion(),
                    clientRequest.getBootVersion(),
                    clientRequest.getPackaging(),
                    clientRequest.getApplicationName(),
                    clientRequest.getLanguage(),
                    clientRequest.getPackageName(),
                    clientRequest.getJavaVersion(),
                    clientRequest.getBaseDir()
            );

            byte[] content = responseSpec.body(byte[].class);

            return mapper.toBusinessArchive(content);

        } catch (RestClientResponseException e) {
            throw new ServiceException(CommonProblemType.ERREUR_INATTENDUE, e);
        }
    }

    @Override
    public List<StarterKitBusiness> getAvailableComponents() {
        return Collections.singletonList(StarterKitBusiness.TONIC);
    }

    @Override
    public List<String> getComponentFeatures(StarterKitType type) {
        if (type != StarterKitType.TONIC) {
            return Collections.emptyList();
        }
        return featuresService.getAvailableFeatures();
    }
}