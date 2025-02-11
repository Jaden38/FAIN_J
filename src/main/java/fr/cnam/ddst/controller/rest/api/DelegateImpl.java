package fr.cnam.ddst.controller.rest.api;

import fr.cnam.ddst.config.InitializerProperties;
import fr.cnam.ddst.service.FeatureValidationService;
import fr.cnam.ddst.service.tonic.TonicFeaturesService;
import fr.cnam.ddst.service.tonic.TonicProjectGenerationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class DelegateImpl implements InstanciateApiDelegate, FeaturesApiDelegate {

    private final TonicProjectGenerationService tonicProjectGenerationService;
    private final InitializerProperties properties;
    private final FeatureValidationService validationService;
    private final TonicFeaturesService tonicFeaturesService;

    public DelegateImpl(
            TonicProjectGenerationService tonicProjectGenerationService,
            InitializerProperties properties,
            FeatureValidationService validationService,
            TonicFeaturesService tonicFeaturesService) {
        this.tonicProjectGenerationService = tonicProjectGenerationService;
        this.properties = properties;
        this.validationService = validationService;
        this.tonicFeaturesService = tonicFeaturesService;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return InstanciateApiDelegate.super.getRequest();
    }

    @Override
    public ResponseEntity<List<String>> getFeatures(String typeDeComposant) {
        if (typeDeComposant == null) {
            log.warn("Received null typeDeComposant parameter");
            return ResponseEntity.badRequest().build();
        }

        try {
            if ("TONIC".equalsIgnoreCase(typeDeComposant)) {
                List<String> features = tonicFeaturesService.getAvailableFeatures();
                return ResponseEntity.ok(features);
            }

            List<String> features = properties.getFeaturesForType(typeDeComposant.toLowerCase());
            if (features != null) {
                return ResponseEntity.ok(features);
            }
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error getting features for type {}", typeDeComposant, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    public ResponseEntity<Resource> getSourceZip(
            String typeDeComposant,
            String nomDuComposant,
            String groupId,
            String artifactId,
            List<String> features) {

        log.info("Received request to generate {} project with features: {}", typeDeComposant, features);

        try {
            String finalGroupId = groupId != null ? groupId : "fr.cnam.default";
            String finalArtifactId = artifactId != null ? artifactId : nomDuComposant;

            FeatureValidationService.ValidationResult validationResult =
                    validationService.validateFeatures(typeDeComposant.toLowerCase(), features);

            if (!validationResult.isValid()) {
                log.warn("Invalid features requested: {}", validationResult.getErrors());
                return ResponseEntity.badRequest().build();
            }

            switch (typeDeComposant.toUpperCase()) {
                case "TONIC":
                    return tonicProjectGenerationService.getProjectZip(
                            features,
                            finalGroupId,
                            finalArtifactId,
                            nomDuComposant,
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
                case "HUMAN":
                    // TODO: Implement HUMAN project generation service
                    log.info("Generating HUMAN project. GroupId: {}, ArtifactId: {}", finalGroupId, finalArtifactId);
                default:
                    log.warn("Unsupported component type: {}", typeDeComposant);
                    return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            log.error("Error processing request", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}