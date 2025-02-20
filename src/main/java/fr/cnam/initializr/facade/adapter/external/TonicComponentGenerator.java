package fr.cnam.initializr.facade.adapter.external;

import fr.cnam.client.tonic.controller.rest.api.TonicProjectGenerationControllerApi;
import fr.cnam.client.tonic.controller.rest.model.TonicDependenciesResponse;
import fr.cnam.initializr.facade.controller.rest.model.StarterKitType;
import fr.cnam.initializr.facade.domain.port.ComponentGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TonicComponentGenerator implements ComponentGenerator {
    private final TonicProjectGenerationControllerApi tonicApi;

    @Override
    public Resource generateComponent(fr.cnam.initializr.facade.domain.model.Component component) {

        String groupId = "fr.cnam." + component.getProductName().toLowerCase();
        String artifactId = component.getCodeApplicatif().replaceAll("[-_]","").toLowerCase();
        File file = tonicApi.getProjectZip(
                component.getFeatures(),
                groupId,
                artifactId,
                component.getProductName(),
                null,
                null,
                null,
                null,
                null,
                component.getCodeApplicatif(),
                null,
                null,
                null,
                null
        );
        return new FileSystemResource(file);
    }

    @Override
    public List<StarterKitType> getAvailableComponents() {
        return Collections.singletonList(StarterKitType.TONIC);
    }

    @Override
    public List<String> getComponentFeatures(StarterKitType type) {
        TonicDependenciesResponse deps = tonicApi.getDependencies(null);
        assert deps.getDependencies() != null;
        return new ArrayList<>(deps.getDependencies().keySet());
    }
}