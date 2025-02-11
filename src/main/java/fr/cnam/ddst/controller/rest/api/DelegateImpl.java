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

/**
 * Implémentation des délégués de l'API qui gère les requêtes REST pour la génération de projets
 * et la récupération des fonctionnalités disponibles.
 * <p>
 * Cette classe fournit l'implémentation concrète des endpoints de l'API définis dans les interfaces
 * InstanciateApiDelegate et FeaturesApiDelegate. Elle coordonne les interactions entre les différents
 * services pour la validation des features et la génération des projets.
 */
@Slf4j
@Service
public class DelegateImpl implements InstanciateApiDelegate, FeaturesApiDelegate {

    private final TonicProjectGenerationService tonicProjectGenerationService;
    private final InitializerProperties properties;
    private final FeatureValidationService validationService;
    private final TonicFeaturesService tonicFeaturesService;

    /**
     * Construit une nouvelle instance du délégué de l'API.
     *
     * @param tonicProjectGenerationService Service responsable de la génération des projets TONIC
     * @param properties                    Configuration des propriétés des instanciateurs
     * @param validationService             Service de validation des fonctionnalités
     * @param tonicFeaturesService          Service de gestion des fonctionnalités TONIC
     */
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

    /**
     * Implémentation de la méthode getRequest commune aux deux interfaces.
     * Résout le conflit de méthodes par défaut entre les interfaces.
     *
     * @return Optional contenant la requête web native si disponible
     */
    @Override
    public Optional<NativeWebRequest> getRequest() {
        return InstanciateApiDelegate.super.getRequest();
    }

    /**
     * Récupère la liste des fonctionnalités disponibles pour un type de composant spécifié.
     * Actuellement, seul le type TONIC dispose de fonctionnalités.
     *
     * @param typeDeComposant Le type de composant (TONIC)
     * @return ResponseEntity contenant :
     *         - La liste des fonctionnalités si le type est TONIC (200 OK)
     *         - 400 Bad Request si le type est null ou différent de TONIC
     *         - 500 Internal Server Error en cas d'erreur lors du traitement
     */
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

            log.warn("Features are only available for TONIC components. Received: {}", typeDeComposant);
            return ResponseEntity.badRequest().build();

        } catch (Exception e) {
            log.error("Error getting features for type {}", typeDeComposant, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Génère un projet selon les paramètres spécifiés et retourne une archive ZIP contenant les sources.
     * <p>
     * Cette méthode effectue les opérations suivantes :
     * <ul>
     *   <li>Valide les features demandées pour le type de composant</li>
     *   <li>Applique des valeurs par défaut pour groupId et artifactId si non spécifiés</li>
     *   <li>Délègue la génération au service approprié selon le type de composant</li>
     * </ul>
     *
     * @param typeDeComposant Type du composant à générer (TONIC, STUMP, HUMAN)
     * @param nomDuComposant  Nom du composant à générer
     * @param groupId         ID du groupe Maven (optionnel, valeur par défaut : fr.cnam.default)
     * @param artifactId      ID de l'artefact Maven (optionnel, valeur par défaut : nomDuComposant)
     * @param features        Liste des fonctionnalités à inclure dans le projet
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