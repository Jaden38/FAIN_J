package fr.cnam.ddst.service;

import fr.cnam.ddst.config.InitializerProperties;
import fr.cnam.ddst.service.tonic.TonicFeaturesService;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service de validation des fonctionnalités demandées pour les instanciateurs.
 * <p>
 * Ce service vérifie que les fonctionnalités demandées lors de la création d'un projet
 * sont valides et disponibles pour le type d'instanciateur spécifié.
 */
@Service
public class FeatureValidationService {

    private final InitializerProperties properties;
    private final TonicFeaturesService tonicFeaturesService;

    /**
     * Construit un nouveau service de validation avec les propriétés d'initialisation spécifiées.
     *
     * @param properties Les propriétés de configuration des instanciateurs
     */
    public FeatureValidationService(InitializerProperties properties, TonicFeaturesService tonicFeaturesService) {
        this.properties = properties;
        this.tonicFeaturesService = tonicFeaturesService;
    }

    /**
     * Valide une liste de fonctionnalités pour un type d'instanciateur donné.
     *
     * @param type Le type d'instanciateur (TONIC, STUMP, HUMAN)
     * @param requestedFeatures La liste des fonctionnalités à valider
     * @return Un objet ValidationResult contenant le résultat de la validation
     */
    public ValidationResult validateFeatures(String type, List<String> requestedFeatures) {
        if (requestedFeatures == null || requestedFeatures.isEmpty()) {
            return ValidationResult.valid(Collections.emptyList());
        }

        List<String> availableFeatures;
        if ("tonic".equalsIgnoreCase(type)) {
            availableFeatures = tonicFeaturesService.getAvailableFeatures();
        } else {
            availableFeatures = properties.getFeaturesForType(type);
        }

        if (availableFeatures == null || availableFeatures.isEmpty()) {
            return ValidationResult.invalid(
                    List.of("Component type " + type + " is not supported or no features available")
            );
        }

        List<String> invalidFeatures = requestedFeatures.stream()
                .filter(feature -> !availableFeatures.contains(feature))
                .toList();

        if (!invalidFeatures.isEmpty()) {
            return ValidationResult.invalid(
                    invalidFeatures.stream()
                            .map(feature -> "Feature '" + feature + "' is not available")
                            .collect(Collectors.toList())
            );
        }

        return ValidationResult.valid(requestedFeatures);
    }

    /**
     * Classe interne représentant le résultat d'une validation de fonctionnalités.
     */
    @Getter
    public static class ValidationResult {
        private final boolean valid;
        private final List<String> errors;
        private final List<String> validatedFeatures;

        /**
         * Construit un nouveau résultat de validation.
         *
         * @param valid Indique si la validation est réussie
         * @param errors Liste des erreurs de validation
         * @param validatedFeatures Liste des fonctionnalités validées
         */
        private ValidationResult(boolean valid, List<String> errors, List<String> validatedFeatures) {
            this.valid = valid;
            this.errors = errors;
            this.validatedFeatures = validatedFeatures;
        }

        /**
         * Crée un résultat de validation réussi.
         *
         * @param validatedFeatures Liste des fonctionnalités validées
         * @return Un nouveau ValidationResult pour une validation réussie
         */
        public static ValidationResult valid(List<String> validatedFeatures) {
            return new ValidationResult(true, Collections.emptyList(), validatedFeatures);
        }

        /**
         * Crée un résultat de validation échoué.
         *
         * @param errors Liste des erreurs de validation
         * @return Un nouveau ValidationResult pour une validation échouée
         */
        public static ValidationResult invalid(List<String> errors) {
            return new ValidationResult(false, errors, Collections.emptyList());
        }
    }
}