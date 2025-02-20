package fr.cnam.initializr.facade.provider;

import fr.cnam.client.tonic.controller.rest.api.TonicProjectGenerationControllerApi;
import fr.cnam.client.tonic.controller.rest.model.ProjectRequest;
import fr.cnam.initializr.facade.business.ComponentArchive;
import fr.cnam.initializr.facade.business.ComponentProvider;
import fr.cnam.initializr.facade.business.ComponentRequest;
import fr.cnam.initializr.facade.business.StarterKitBusiness;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TonicComponentProvider implements ComponentProvider {
    private final TonicProjectGenerationControllerApi tonicApi;
    private final TonicMapper mapper;

    @Override
    public ComponentArchive generateComponent(ComponentRequest businessRequest) {
        ProjectRequest clientRequest = mapper.toClientRequest(businessRequest);

        File result = tonicApi.getProjectZip(
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

        return mapper.toBusinessArchive(result);
    }

    @Override
    public List<StarterKitBusiness> getAvailableComponents() {
        return Collections.singletonList(StarterKitBusiness.TONIC);
    }
}