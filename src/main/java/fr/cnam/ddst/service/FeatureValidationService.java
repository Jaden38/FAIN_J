package fr.cnam.ddst.service;

import fr.cnam.ddst.controller.rest.model.StarterKitType;
import fr.cnam.ddst.service.tonic.TonicFeaturesService;
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
 * sont valides et disponibles pour le type d'instanciateur spécifié.
 */
@Slf4j
@Service
public class FeatureValidationService {

    private final TonicFeaturesService tonicFeaturesService;

    /**
     * Construit un nouveau service de validation avec les propriétés d'initialisation spécifiées.
     *
     * @param tonicFeaturesService Service de gestion des fonctionnalités TONIC
     */
    public FeatureValidationService(TonicFeaturesService tonicFeaturesService) {
        this.tonicFeaturesService = tonicFeaturesService;
    }

    /**
     * Valide une liste de fonctionnalités pour un type d'instanciateur donné.
     *
     * @param type Le type d'instanciateur (StarterKitType.TONIC, StarterKitType.HUMAN, StarterKitType.STUMP)
     * @param requestedFeatures La liste des fonctionnalités à valider. Si null ou vide,
     *                         retourne une liste vide.
     * @return La liste des fonctionnalités validées si toutes sont valides
     * @throws ClientException avec CommonProblemType.DONNEES_INVALIDES si :
     *         - Le type de composant est null
     *         - Le type de composant n'est pas TONIC
     *         - Aucune fonctionnalité n'est disponible pour ce type
     *         - Une ou plusieurs fonctionnalités demandées ne sont pas disponibles
     */
    public List<String> validateFeatures(StarterKitType type, List<String> requestedFeatures) {
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