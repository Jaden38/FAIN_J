package fr.cnam.ddst.controller.rest.api;

import fr.cnam.ddst.controller.rest.model.ComponentType;
import fr.cnam.ddst.service.FeatureValidationService;
import fr.cnam.ddst.service.tonic.TonicFeaturesService;
import fr.cnam.ddst.service.tonic.TonicProjectGenerationService;
import fr.cnam.toni.starter.core.exceptions.ClientException;
import fr.cnam.toni.starter.core.exceptions.CommonProblemType;
import fr.cnam.toni.starter.core.exceptions.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implémentation des délégués de l'API qui gère les requêtes REST pour la génération de projets
 * et la récupération des fonctionnalités disponibles.
 * <p>
 * Cette classe fournit l'implémentation concrète des endpoints de l'API définis dans les interfaces
 * InstanciateApiDelegate et FeaturesApiDelegate. Elle coordonne les interactions entre les différents
 * services pour la validation des features et la génération des projets.
 */
@Slf4j
@RestController
public class ProjectGenerationController implements DefaultApi {

    private final TonicProjectGenerationService tonicProjectGenerationService;
    private final FeatureValidationService validationService;
    private final TonicFeaturesService tonicFeaturesService;

    /**
     * Construit une nouvelle instance du délégué de l'API.
     *
     * @param tonicProjectGenerationService Service responsable de la génération des projets TONIC
     * @param validationService             Service de validation des fonctionnalités
     * @param tonicFeaturesService          Service de gestion des fonctionnalités TONIC
     */
    public ProjectGenerationController(
            TonicProjectGenerationService tonicProjectGenerationService,
            FeatureValidationService validationService,
            TonicFeaturesService tonicFeaturesService) {
        this.tonicProjectGenerationService = tonicProjectGenerationService;
        this.validationService = validationService;
        this.tonicFeaturesService = tonicFeaturesService;
    }

    /**
     * Récupère la liste des types de composants disponibles dans le système.
     * <p>
     * Cette méthode retourne la liste exhaustive des types de composants actuellement
     * supportés par l'API (TONIC, HUMAN). Les types sont définis dans l'énumération
     * {@link ComponentType}.
     *
     * @return ResponseEntity contenant la liste des types de composants disponibles (200 OK)
     * @throws ServiceException avec CommonProblemType.ERREUR_INATTENDUE en cas d'erreur
     *         lors de la récupération des types (500 Internal Server Error)
     */
    @Override
    public ResponseEntity<List<ComponentType>> getComponentTypes() {
        try {
            log.info("Retrieving available component types");
            List<ComponentType> componentTypes = Arrays.asList(ComponentType.values());
            return ResponseEntity.ok(componentTypes);
        } catch (Exception e) {
            log.error("Error retrieving component types", e);
            throw new ServiceException(
                    CommonProblemType.ERREUR_INATTENDUE,
                    e,
                    "Error retrieving component types"
            );
        }
    }

    /**
     * Récupère la liste des fonctionnalités disponibles pour un type de composant spécifié.
     * Actuellement, seul le type TONIC dispose de fonctionnalités.
     *
     * @param typeDeComposant Le type de composant (TONIC)
     * @return ResponseEntity contenant la liste des fonctionnalités si le type est TONIC (200 OK)
     * @throws ClientException avec CommonProblemType.DONNEES_INVALIDES si le type est null ou différent de TONIC (400 Bad Request)
     * @throws ServiceException avec CommonProblemType.ERREUR_INATTENDUE en cas d'erreur lors du traitement (500 Internal Server Error)
     */
    @Override
    public ResponseEntity<List<String>> getFeatures(ComponentType typeDeComposant) {
        if (typeDeComposant == null) {
            log.warn("Received null typeDeComposant parameter");
            throw new ClientException(
                    CommonProblemType.DONNEES_INVALIDES,
                    "Type de composant cannot be null"
            );
        }

        try {
            if (typeDeComposant == ComponentType.TONIC) {
                List<String> features = tonicFeaturesService.getAvailableFeatures();
                return ResponseEntity.ok(features);
            }

            log.warn("Features are only available for TONIC components. Received: {}", typeDeComposant);
            throw new ClientException(
                    CommonProblemType.DONNEES_INVALIDES,
                    "Features are only available for TONIC components"
            );

        } catch (Exception e) {
            log.error("Error getting features for type {}", typeDeComposant, e);
            throw new ServiceException(
                    CommonProblemType.ERREUR_INATTENDUE,
                    e,
                    "Error getting features for type " + typeDeComposant
            );
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
     * @return ResponseEntity contenant la ressource ZIP du projet généré (200 OK)
     * @throws ClientException avec CommonProblemType.HTTP_PARAMETRE_MANQUANT si des paramètres requis sont manquants (400 Bad Request)
     * @throws ClientException avec CommonProblemType.DONNEES_INVALIDES si les features sont invalides ou le type non supporté (400 Bad Request)
     * @throws ServiceException avec CommonProblemType.ERREUR_INATTENDUE en cas d'erreur lors de la génération (500 Internal Server Error)
     */
    @Override
    public ResponseEntity<Resource> getSourceZip(
            ComponentType typeDeComposant,
            String nomDuComposant,
            String groupId,
            String artifactId,
            List<String> features) {

        log.info("Received request to generate {} project with features: {}", typeDeComposant, features);

        try {
            if (typeDeComposant == null || nomDuComposant == null) {
                Map<String, Object> details = new HashMap<>();
                details.put("typeDeComposant", typeDeComposant == null ? "missing" : "present");
                details.put("nomDuComposant", nomDuComposant == null ? "missing" : "present");

                throw new ClientException(
                        CommonProblemType.HTTP_PARAMETRE_MANQUANT,
                        "Required parameters missing",
                        details
                );
            }

            String finalGroupId = groupId != null ? groupId : "fr.cnam.default";
            String finalArtifactId = artifactId != null ? artifactId : nomDuComposant;

            List<String> validatedFeatures = validationService.validateFeatures(
                    typeDeComposant.toString().toLowerCase(),
                    features
            );

            switch (typeDeComposant) {
                case TONIC:
                    return tonicProjectGenerationService.getProjectZip(
                            validatedFeatures,
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
                case HUMAN:
                    // TODO: Implement HUMAN project generation service
                    log.info("Generating HUMAN project. GroupId: {}, ArtifactId: {}", finalGroupId, finalArtifactId);
                    log.error("HUMAN project generation not yet implemented");
                    throw new ServiceException(
                            CommonProblemType.ERREUR_INATTENDUE,
                            "HUMAN project generation not yet implemented"
                    );
                default:
                    log.warn("Unsupported component type: {}", typeDeComposant);
                    throw new ClientException(
                            CommonProblemType.DONNEES_INVALIDES,
                            "Unsupported component type: " + typeDeComposant
                    );
            }
        } catch (ClientException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error processing request", e);
            throw new ServiceException(
                    CommonProblemType.ERREUR_INATTENDUE,
                    e,
                    "Error processing request for component type: " + typeDeComposant
            );
        }
    }
}