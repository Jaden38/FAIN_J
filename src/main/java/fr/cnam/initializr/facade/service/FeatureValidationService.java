package fr.cnam.initializr.facade.service;

import fr.cnam.initializr.facade.controller.rest.model.StarterKitType;
import fr.cnam.initializr.facade.service.tonic.TonicFeaturesService;
import fr.cnam.toni.starter.core.exceptions.ClientException;
import fr.cnam.toni.starter.core.exceptions.CommonProblemType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service de validation des fonctionnalités demandées pour les instanciateurs.
 * <p>
 * Ce service vérifie que les fonctionnalités demandées lors de la création d'un projet
 * sont valides et disponibles pour le type d'instanciateur spécifié. Il gère également
 * la validation des fonctionnalités de contrat qui ne sont autorisées que dans certains contextes.
 */
@Slf4j
@Service
public class FeatureValidationService {

    private final TonicFeaturesService tonicFeaturesService;

    /**
     * Construit un nouveau service de validation.
     *
     * @param tonicFeaturesService Service de gestion des fonctionnalités TONIC
     */
    public FeatureValidationService(TonicFeaturesService tonicFeaturesService) {
        this.tonicFeaturesService = tonicFeaturesService;
    }

    /**
     * Valide une liste de fonctionnalités pour un type d'instanciateur donné.
     *
     * @param type Le type d'instanciateur à valider
     * @param requestedFeatures La liste des fonctionnalités à valider
     * @param allowContractFeatures Si true, permet la validation des fonctionnalités de contrat
     * @return La liste des fonctionnalités validées
     * @throws ClientException si le type est invalide ou si des fonctionnalités ne sont pas disponibles
     */
    public List<String> validateFeatures(StarterKitType type, List<String> requestedFeatures, boolean allowContractFeatures) {
        if (type == null) {
            log.warn("Component type cannot be null");
            throw new ClientException(
                    CommonProblemType.DONNEES_INVALIDES,
                    "Component type cannot be null"
            );
        }

        if (type != StarterKitType.TONIC) {
            log.warn("Invalid component type: {}. Only TONIC is currently supported", type);
            throw new ClientException(
                    CommonProblemType.DONNEES_INVALIDES,
                    "Invalid component type: " + type + ". Only TONIC is currently supported"
            );
        }

        if (requestedFeatures == null || requestedFeatures.isEmpty()) {
            log.info("No features requested for type: {}, returning empty list", type);
            return Collections.emptyList();
        }

        if (!allowContractFeatures) {
            boolean hasContractFeatures = requestedFeatures.stream()
                    .anyMatch(feature -> feature.startsWith("toni-contract-"));

            if (hasContractFeatures) {
                log.warn("Contract features detected when not allowed: {}", requestedFeatures);
                throw new ClientException(
                        CommonProblemType.DONNEES_INVALIDES,
                        "Contract features are not allowed in this context"
                );
            }
        }

        List<String> availableFeatures = tonicFeaturesService.getAvailableFeatures();
        if (availableFeatures == null || availableFeatures.isEmpty()) {
            log.warn("No features available for component type: {}", type);
            throw new ClientException(
                    CommonProblemType.DONNEES_INVALIDES,
                    "No features available for component type: " + type
            );
        }

        List<String> invalidFeatures = requestedFeatures.stream()
                .filter(feature -> !availableFeatures.contains(feature))
                .toList();

        if (!invalidFeatures.isEmpty()) {
            log.warn("Invalid features requested for type {}: {}", type, invalidFeatures);
            log.debug("Available features are: {}", availableFeatures);

            Map<String, Object> details = new HashMap<>();
            details.put("invalidFeatures", invalidFeatures);
            details.put("availableFeatures", availableFeatures);

            throw new ClientException(
                    CommonProblemType.DONNEES_INVALIDES,
                    "One or more requested features are not available",
                    details
            );
        }

        log.info("Successfully validated {} features for type {}", requestedFeatures.size(), type);
        log.debug("Validated features: {}", requestedFeatures);
        return requestedFeatures;
    }
}