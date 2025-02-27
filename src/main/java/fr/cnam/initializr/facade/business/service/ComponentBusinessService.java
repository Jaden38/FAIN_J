package fr.cnam.initializr.facade.business.service;

import fr.cnam.initializr.facade.business.model.ComponentArchive;
import fr.cnam.initializr.facade.business.model.ComponentRequest;
import fr.cnam.initializr.facade.business.model.StarterKitBusiness;
import fr.cnam.initializr.facade.business.port.ComponentProvider;
import fr.cnam.initializr.facade.controller.rest.model.StarterKitType;
import fr.cnam.toni.starter.core.exceptions.ClientException;
import fr.cnam.toni.starter.core.exceptions.CommonProblemType;
import fr.cnam.toni.starter.core.exceptions.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComponentBusinessService {
    private final ComponentProvider provider;
    private final MetricBusinessService metricBusinessService;

    public ComponentArchive generateComponent(ComponentRequest request) {
        validateRequest(request);
        validateFeatures(request);
        try {
            ComponentArchive archive = provider.generateComponent(request);
            metricBusinessService.recordComponentGeneration(request);
            return archive;
        } catch (Exception e) {
            throw new ServiceException(CommonProblemType.ERREUR_INATTENDUE, e);
        }
    }

    public List<StarterKitBusiness> getAvailableComponents() {
        try {
            return provider.getAvailableComponents();
        } catch (Exception e) {
            throw new ServiceException(CommonProblemType.ERREUR_INATTENDUE, e);
        }
    }

    public List<String> getComponentFeatures(StarterKitType type) {
        validateStarterKitType(type);
        try {
            return provider.getComponentFeatures(type);
        } catch (Exception e) {
            throw new ServiceException(CommonProblemType.ERREUR_INATTENDUE, e);
        }
    }

    private void validateRequest(ComponentRequest request) {
        validateStarterKitType(request.getType());

        if (request.getProductName() == null || request.getProductName().trim().isEmpty()) {
            log.warn("Product name cannot be empty");
            throw new ClientException(
                    CommonProblemType.DONNEES_INVALIDES_MSG_AVEC_PROBLEMES,
                    "Product name cannot be empty"
            );
        }
        if (request.getCodeApplicatif() == null || request.getCodeApplicatif().trim().isEmpty()) {
            log.warn("Code applicatif cannot be empty");
            throw new ClientException(
                    CommonProblemType.DONNEES_INVALIDES_MSG_AVEC_PROBLEMES,
                    "Code applicatif cannot be empty"
            );
        }
    }

    private void validateStarterKitType(StarterKitType type) {
        if (type == null) {
            log.warn("Starter kit type cannot be null");
            throw new ClientException(
                    CommonProblemType.DONNEES_INVALIDES_MSG_AVEC_PROBLEMES,
                    "Starter kit type cannot be null"
            );
        }

        if (type != StarterKitType.TONIC) {
            log.warn("Invalid starter kit type: {}. Only TONIC is currently supported", type);
            throw new ClientException(
                    CommonProblemType.DONNEES_INVALIDES_MSG_AVEC_PROBLEMES,
                    "Invalid starter kit type: " + type + ". Only TONIC is currently supported"
            );
        }
    }

    private void validateFeatures(ComponentRequest request) {
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