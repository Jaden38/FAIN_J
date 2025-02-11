package fr.cnam.ddst.controller.rest.api;

import fr.cnam.ddst.config.InitializerProperties;
import fr.cnam.ddst.service.FeatureValidationService;
import fr.cnam.ddst.service.tonic.TonicFeaturesService;
import fr.cnam.ddst.service.tonic.TonicProjectGenerationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Implémentation du délégué de l'API V1 qui gère les requêtes REST pour la génération de projets
 * et la récupération des fonctionnalités disponibles.
 * <p>
 * Cette classe fournit l'implémentation concrète des endpoints de l'API définis dans l'interface V1ApiDelegate.
 * Elle coordonne les interactions entre les différents services pour la validation des features
 * et la génération des projets.
 *
 * @see fr.cnam.ddst.controller.rest.api.V1ApiDelegate
 */
@Slf4j
@Service
public class V1ApiDelegateImpl implements V1ApiDelegate {

    private final TonicProjectGenerationService tonicProjectGenerationService;
    private final InitializerProperties properties;
    private final FeatureValidationService validationService;
    private final TonicFeaturesService tonicFeaturesService;

    /**
     * Construit une nouvelle instance du délégué de l'API V1.
     *
     * @param tonicProjectGenerationService Service responsable de la génération des projets TONIC
     * @param properties Configuration des propriétés des instanciateurs
     * @param validationService Service de validation des fonctionnalités
     */
    public V1ApiDelegateImpl(TonicProjectGenerationService tonicProjectGenerationService, InitializerProperties properties,
                             FeatureValidationService validationService, TonicFeaturesService tonicFeaturesService) {
        this.tonicProjectGenerationService = tonicProjectGenerationService;
        this.properties = properties;
        this.validationService = validationService;
        this.tonicFeaturesService = tonicFeaturesService;
    }

    /**
     * Récupère la liste des fonctionnalités disponibles pour un type de composant spécifié.
     *
     * @param typeDeComposant Le type de composant (TONIC, STUMP, HUMAN)
     * @return ResponseEntity contenant la liste des fonctionnalités si le type est valide,
     *         ou une réponse 400 Bad Request si le type est null ou non supporté
     */
    @Override
    public ResponseEntity<List<String>> getFeatures(String typeDeComposant) {
        if (typeDeComposant == null) {
            log.warn("Received null typeDeComposant parameter");
            return ResponseEntity.badRequest().build();
        }

        try {
            if ("tonic".equalsIgnoreCase(typeDeComposant)) {
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

    /**
     * Génère un projet selon les paramètres spécifiés et retourne une archive ZIP contenant les sources.
     * <p>
     * Cette méthode :
     * 1. Valide les features demandées pour le type de composant
     * 2. Applique des valeurs par défaut pour groupId et artifactId si non spécifiés
     * 3. Délègue la génération au service approprié selon le type de composant
     *
     * @param typeDeComposant Type du composant à générer (TONIC, STUMP, HUMAN)
     * @param nomDuComposant Nom du composant à générer
     * @param groupId ID du groupe Maven (optionnel, valeur par défaut : fr.cnam.default)
     * @param artifactId ID de l'artefact Maven (optionnel, valeur par défaut : nomDuComposant)
     * @param features Liste des fonctionnalités à inclure dans le projet
     * @return ResponseEntity contenant :
     *         - La ressource ZIP du projet généré (200 OK)
     *         - 400 Bad Request si les paramètres ou features sont invalides
     *         - 500 Internal Server Error en cas d'erreur lors de la génération
     */
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

//                case "HUMAN":
//                    // TODO: Implement HUMAN project generation service
//                    log.info("Generating HUMAN project. GroupId: {}, ArtifactId: {}", finalGroupId, finalArtifactId);
//                    return humanProjectGenerationService.generateProject(
//                            nomDuComposant,
//                            finalGroupId,
//                            finalArtifactId,
//                            features
//                    );
//
//                case "STUMP":
//                    // TODO: Implement STUMP project generation service
//                    log.info("Generating STUMP project. GroupId: {}, ArtifactId: {}", finalGroupId, finalArtifactId);
//                    return stumpProjectGenerationService.generateProject(
//                            nomDuComposant,
//                            finalGroupId,
//                            finalArtifactId,
//                            features
//                    );

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