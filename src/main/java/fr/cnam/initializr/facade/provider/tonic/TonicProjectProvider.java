package fr.cnam.initializr.facade.provider.tonic;

import fr.cnam.client.tonic.controller.rest.api.TonicProjectGenerationControllerApi;
import fr.cnam.initializr.facade.business.exception.ProjectGenerationException;
import fr.cnam.initializr.facade.business.model.Project;
import fr.cnam.initializr.facade.business.model.ProjectGenerationRequest;
import fr.cnam.initializr.facade.business.port.ProjectGenerationPort;
import fr.cnam.initializr.facade.controller.rest.model.ContractType;
import fr.cnam.initializr.facade.controller.rest.model.StarterKitType;
import fr.cnam.initializr.facade.provider.tonic.mapper.TonicMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class TonicProjectProvider implements ProjectGenerationPort {

    private final TonicProjectGenerationControllerApi tonicApi;
    private final TonicMapper mapper;

    public TonicProjectProvider(TonicProjectGenerationControllerApi tonicApi, TonicMapper mapper) {
        this.tonicApi = tonicApi;
        this.mapper = mapper;
    }

    @Override
    public Project generateProject(ProjectGenerationRequest request) {
        try {
            ResponseEntity<Resource> response = tonicApi.getProjectZipWithHttpInfo(
                    request.getFeatures(),
                    request.getGroupId(),
                    request.getArtifactId(),
                    request.getApplicationCode(), // name
                    null, // type
                    null, // description
                    null, // version
                    null, // bootVersion
                    null, // packaging
                    null, // applicationName
                    null, // language
                    null, // packageName
                    null, // javaVersion
                    null  // baseDir
            );

            return mapper.toProject(response.getBody());

        } catch (RestClientException e) {
            log.error("Error generating project with TONIC", e);
            throw new ProjectGenerationException("GENERATION_ERROR",
                    "Error generating project: " + e.getMessage());
        }
    }

    @Override
    public List<String> getAvailableFeatures(StarterKitType type) {
        try {
            var response = tonicApi.getDependencies(null);
            return mapper.toFeaturesList(response);
        } catch (RestClientException e) {
            log.error("Error fetching features from TONIC", e);
            throw new ProjectGenerationException("FEATURES_ERROR",
                    "Error fetching features: " + e.getMessage());
        }
    }

    @Override
    public List<StarterKitType> getAvailableStarters() {
        return Arrays.asList(StarterKitType.TONIC);
    }

    @Override
    public Project generateContract(ContractType format, ProjectGenerationRequest request) {
        // Pour les contrats, on utilise des features spécifiques selon le format
        var contractFeature = switch (format) {
            case OPENAPI -> "toni-contract-openapi";
            case AVRO -> "toni-contract-avro";
            default -> throw new ProjectGenerationException("INVALID_FORMAT",
                    "Contract format not supported: " + format);
        };

        request.setFeatures(Arrays.asList(contractFeature));
        return generateProject(request);
    }
}