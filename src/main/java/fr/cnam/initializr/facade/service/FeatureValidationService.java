package fr.cnam.initializr.facade.service;

import fr.cnam.initializr.facade.controller.rest.model.StarterKitType;
import fr.cnam.initializr.facade.service.tonic.TonicFeaturesService;
import fr.cnam.toni.starter.core.exceptions.ClientException;
import fr.cnam.toni.starter.core.exceptions.CommonProblemType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

import static fr.cnam.initializr.facade.service.tonic.TonicFeaturesService.CONTRACT_FEATURES;

/**
 * Service de validation des fonctionnalités demandées pour les instanciateurs.
 * <p>
 * Ce service vérifie que les fonctionnalités demandées lors de la création d'un projet
 * sont valides et disponibles pour le type d'instanciateur spécifié. Il gère également
 * la validation des fonctionnalités de contrat qui ne sont autorisées que dans certains contextes.
 * </p>
 * <p>
 * Le service maintient des structures de données internes pour optimiser les performances
 * en réutilisant les collections plutôt qu'en les recréant à chaque validation.
 * </p>
 */
@Slf4j
@Service
public class FeatureValidationService {

    private final TonicFeaturesService tonicFeaturesService;
    private final List<String> availableFeatures;
    private final Map<String, Object> errorDetails;

    /**
     * Construit un nouveau service de validation des fonctionnalités.
     * Initialise les structures de données internes pour une meilleure performance.
     *
     * @param tonicFeaturesService Service de gestion des fonctionnalités TONIC
     */
    public FeatureValidationService(TonicFeaturesService tonicFeaturesService) {
        this.tonicFeaturesService = tonicFeaturesService;
        this.availableFeatures = new ArrayList<>();
        this.errorDetails = new HashMap<>();
    }

    /**
     * Valide une liste de fonctionnalités pour un type d'instanciateur donné.
     * <p>
     * Cette méthode effectue plusieurs validations :
     * <ul>
     *     <li>Vérifie que le type d'instanciateur est valide</li>
     *     <li>Vérifie que les fonctionnalités de contrat sont autorisées si présentes</li>
     *     <li>Vérifie que toutes les fonctionnalités demandées sont disponibles</li>
     * </ul>
     * </p>
     *
     * @param type                  Le type d'instanciateur à valider
     * @param requestedFeatures     La liste des fonctionnalités à valider
     * @param allowContractFeatures Si true, permet la validation des fonctionnalités de contrat
     * @return La liste des fonctionnalités validées
     * @throws ClientException si le type est invalide, si des fonctionnalités ne sont pas disponibles,
     *                        ou si des fonctionnalités de contrat sont demandées alors qu'elles ne sont pas autorisées
     */
    public List<String> validateFeatures(StarterKitType type, List<String> requestedFeatures, boolean allowContractFeatures) {
        validateType(type);

        if (requestedFeatures == null || requestedFeatures.isEmpty()) {
            log.info("No features requested for type: {}, returning empty list", type);
            return Collections.emptyList();
        }

        validateContractFeatures(requestedFeatures, allowContractFeatures);
        initializeAvailableFeatures(allowContractFeatures);
        validateRequestedFeatures(type, requestedFeatures);

        log.info("Successfully validated {} features for type {}", requestedFeatures.size(), type);
        log.debug("Validated features: {}", requestedFeatures);
        return requestedFeatures;
    }

    /**
     * Valide le type d'instanciateur.
     * <p>
     * Vérifie que le type n'est pas null et qu'il est supporté (actuellement seul TONIC est supporté).
     * </p>
     *
     * @param type Le type d'instanciateur à valider
     * @throws ClientException si le type est null ou non supporté
     */
    private void validateType(StarterKitType type) {
        if (type == null) {
            log.warn("Component type cannot be null");
            throw new ClientException(
                    CommonProblemType.DONNEES_INVALIDES_MSG_AVEC_PROBLEMES,
                    "Component type cannot be null"
            );
        }

        if (type != StarterKitType.TONIC) {
            log.warn("Invalid component type: {}. Only TONIC is currently supported", type);
            throw new ClientException(
                    CommonProblemType.DONNEES_INVALIDES_MSG_AVEC_PROBLEMES,
                    "Invalid component type: " + type + ". Only TONIC is currently supported"
            );
        }
    }

    /**
     * Valide les fonctionnalités de contrat.
     * <p>
     * Vérifie qu'aucune fonctionnalité de contrat n'est présente si elles ne sont pas autorisées.
     * Les fonctionnalités de contrat sont identifiées par le préfixe "toni-contract-".
     * </p>
     *
     * @param requestedFeatures     Liste des fonctionnalités demandées
     * @param allowContractFeatures Indique si les fonctionnalités de contrat sont autorisées
     * @throws ClientException si des fonctionnalités de contrat sont détectées alors qu'elles ne sont pas autorisées
     */
    private void validateContractFeatures(List<String> requestedFeatures, boolean allowContractFeatures) {
        if (!allowContractFeatures && requestedFeatures.stream().anyMatch(feature -> feature.startsWith("toni-contract-"))) {
            log.warn("Contract features detected when not allowed: {}", requestedFeatures);
            throw new ClientException(
                    CommonProblemType.DONNEES_INVALIDES_MSG_AVEC_PROBLEMES,
                    "Contract features are not allowed in this context"
            );
        }
    }

    /**
     * Initialise la liste des fonctionnalités disponibles.
     * <p>
     * Charge les fonctionnalités disponibles depuis le service TONIC et ajoute les fonctionnalités
     * de contrat si elles sont autorisées. La liste est réinitialisée à chaque appel pour
     * garantir sa cohérence.
     * </p>
     *
     * @param allowContractFeatures Indique si les fonctionnalités de contrat doivent être incluses
     * @throws ClientException si aucune fonctionnalité n'est disponible
     */
    private void initializeAvailableFeatures(boolean allowContractFeatures) {
        availableFeatures.clear();
        availableFeatures.addAll(tonicFeaturesService.getAvailableFeatures());

        if (allowContractFeatures) {
            availableFeatures.addAll(CONTRACT_FEATURES);
        }

        if (availableFeatures.isEmpty()) {
            log.warn("No features available for component type: TONIC");
            throw new ClientException(
                    CommonProblemType.DONNEES_INVALIDES_MSG_AVEC_PROBLEMES,
                    "No features available for component type: TONIC"
            );
        }
    }

    /**
     * Valide que toutes les fonctionnalités demandées sont disponibles.
     * <p>
     * Compare la liste des fonctionnalités demandées avec la liste des fonctionnalités disponibles
     * et lève une exception si des fonctionnalités invalides sont détectées. Les détails des
     * fonctionnalités invalides sont inclus dans le message d'erreur.
     * </p>
     *
     * @param type Le type d'instanciateur
     * @param requestedFeatures Liste des fonctionnalités à valider
     * @throws ClientException si des fonctionnalités demandées ne sont pas disponibles
     */
    private void validateRequestedFeatures(StarterKitType type, List<String> requestedFeatures) {
        List<String> invalidFeatures = requestedFeatures.stream()
                .filter(feature -> !availableFeatures.contains(feature))
                .toList();

        if (!invalidFeatures.isEmpty()) {
            log.warn("Invalid features requested for type {}: {}", type, invalidFeatures);
            log.debug("Available features are: {}", availableFeatures);

            errorDetails.clear();
            errorDetails.put("invalidFeatures", invalidFeatures);
            errorDetails.put("availableFeatures", availableFeatures);

            throw new ClientException(
                    CommonProblemType.DONNEES_INVALIDES_MSG_AVEC_PROBLEMES,
                    errorDetails.toString()
            );
        }
    }
}