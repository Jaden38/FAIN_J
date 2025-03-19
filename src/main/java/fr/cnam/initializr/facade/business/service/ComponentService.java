package fr.cnam.initializr.facade.business.service;

import fr.cnam.initializr.facade.business.model.ComponentArchive;
import fr.cnam.initializr.facade.business.model.Component;
import fr.cnam.initializr.facade.business.model.StarterKitBusiness;
import fr.cnam.initializr.facade.business.port.ComponentProvider;
import fr.cnam.initializr.facade.controller.rest.model.StarterKitType;
import fr.cnam.toni.starter.core.exceptions.ClientException;
import fr.cnam.toni.starter.core.exceptions.CommonProblemType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComponentService {
    private final ComponentProvider provider;
    private final MetricService metricService;
    private final ValidationService validationService;

    public ComponentArchive generateComponent(Component request) {
        validateRequest(request);
        validateFeatures(request);
        ComponentArchive archive = provider.generateComponent(request);
        metricService.recordComponentGeneration(request);
        return archive;
    }

    public List<StarterKitBusiness> getAvailableComponents() {
        return provider.getAvailableComponents();
    }

    public List<String> getComponentFeatures(StarterKitType type) {
        validationService.validateStarterKitType(type);
        return provider.getComponentFeatures(type);
    }

    private void validateRequest(Component request) {
        validationService.validateStarterKitType(request.getType());
        validationService.validateProductName(request.getProductName());
        validationService.validateCodeApplicatif(request.getCodeApplicatif());
    }

    private void validateFeatures(Component request) {
        List<String> requestedFeatures = request.getFeatures();
        if (requestedFeatures == null || requestedFeatures.isEmpty()) {
            log.info("No features requested for type: {}, skipping validation", request.getType());
            return;
        }

        List<String> availableFeatures = getComponentFeatures(request.getType());
        if (availableFeatures.isEmpty()) {
            log.warn("No features available for starter kit type: {}", request.getType());
            throw new ClientException(
                    CommonProblemType.DONNEES_INVALIDES_MSG_AVEC_PROBLEMES,
                    "No features available for starter kit type: " + request.getType()
            );
        }

        List<String> invalidFeatures = requestedFeatures.stream()
                .filter(feature -> !availableFeatures.contains(feature))
                .toList();

        if (!invalidFeatures.isEmpty()) {
            log.warn("Invalid features requested for type {}: {}", request.getType(), invalidFeatures);
            log.debug("Available features are: {}", availableFeatures);

            throw new ClientException(
                    CommonProblemType.DONNEES_INVALIDES_MSG_AVEC_PROBLEMES,
                    String.format("%s. Available features are: %s",
                            invalidFeatures, availableFeatures)
            );
        }

        log.info("Successfully validated {} features for type {}", requestedFeatures.size(), request.getType());
        log.debug("Validated features: {}", requestedFeatures);
    }
}