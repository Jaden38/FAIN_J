package fr.cnam.ddst.controller.rest.api;

import fr.cnam.ddst.controller.rest.model.ContractType;
import fr.cnam.ddst.controller.rest.model.LibraryType;
import fr.cnam.ddst.controller.rest.model.StarterKitType;
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

import java.util.*;

/**
 * Contrôleur REST pour la génération de projets et la gestion des fonctionnalités.
 * <p>
 * Fournit des endpoints pour:
 * - La génération de projets starter kit
 * - La génération de contrats (OPENAPI, AVRO)
 * - La génération de bibliothèques
 * - La récupération des types et fonctionnalités disponibles
 */
@Slf4j
@RestController
public class ProjectGenerationController implements DefaultApi {

    private final TonicProjectGenerationService tonicProjectGenerationService;
    private final FeatureValidationService validationService;
    private final TonicFeaturesService tonicFeaturesService;

    /**
     * Construit un nouveau contrôleur avec les services requis.
     *
     * @param tonicProjectGenerationService Service de génération de projets TONIC
     * @param validationService Service de validation des fonctionnalités
     * @param tonicFeaturesService Service de gestion des fonctionnalités TONIC
     */
    public ProjectGenerationController(
            TonicProjectGenerationService tonicProjectGenerationService,
            FeatureValidationService validationService,
            TonicFeaturesService tonicFeaturesService) {
        this.tonicProjectGenerationService = tonicProjectGenerationService;
        this.validationService = validationService;
        this.tonicFeaturesService = tonicFeaturesService;
    }

    @Override
    public ResponseEntity<List<ContractType>> getContractTypes() {
        try {
            log.info("Retrieving available contract types");
            log.warn("Contract types OPENAPI, AVRO, and SOAP are listed but not yet implemented");
            return ResponseEntity.ok(Arrays.asList(ContractType.values()));
        } catch (Exception e) {
            log.error("Error retrieving contract types", e);
            throw new ServiceException(
                    CommonProblemType.ERREUR_INATTENDUE,
                    e,
                    "Error retrieving contract types"
            );
        }
    }

    @Override
    public ResponseEntity<List<LibraryType>> getLibraryTypes() {
        try {
            log.info("Retrieving available library types");
            log.warn("Library types JAVA and NODE are listed but not yet implemented");
            return ResponseEntity.ok(Arrays.asList(LibraryType.values()));
        } catch (Exception e) {
            log.error("Error retrieving library types", e);
            throw new ServiceException(
                    CommonProblemType.ERREUR_INATTENDUE,
                    e,
                    "Error retrieving library types"
            );
        }
    }

    @Override
    public ResponseEntity<List<String>> getStarterKitFeatures(StarterKitType starterKitType) {
        if (starterKitType == null) {
            log.warn("Received null type parameter");
            throw new ClientException(
                    CommonProblemType.DONNEES_INVALIDES,
                    "Starter kit type cannot be null"
            );
        }

        try {
            if (starterKitType == StarterKitType.TONIC) {
                List<String> features = tonicFeaturesService.getAvailableFeatures();
                return ResponseEntity.ok(features);
            }

            log.warn("Features are only available for TONIC components. Received: {}", starterKitType);
            throw new ClientException(
                    CommonProblemType.DONNEES_INVALIDES,
                    "Features are only available for TONIC components"
            );

        } catch (Exception e) {
            log.error("Error getting features for type {}", starterKitType, e);
            throw new ServiceException(
                    CommonProblemType.ERREUR_INATTENDUE,
                    e,
                    "Error getting features for type " + starterKitType
            );
        }
    }

    @Override
    public ResponseEntity<List<StarterKitType>> getStarterKitTypes() {
        try {
            log.info("Retrieving available starter kit types");
            log.warn("Starter kit types HUMAN and STUMP are listed but not yet implemented");
            return ResponseEntity.ok(Arrays.asList(StarterKitType.values()));
        } catch (Exception e) {
            log.error("Error retrieving starter kit types", e);
            throw new ServiceException(
                    CommonProblemType.ERREUR_INATTENDUE,
                    e,
                    "Error retrieving starter kit types"
            );
        }
    }

    @Override
    public ResponseEntity<Resource> instanciateStarterKit(
            StarterKitType starterKitType,
            String componentName,
            String groupId,
            String artifactId,
            List<String> features) {
        return internalInstanciateStarterKit(starterKitType, componentName, groupId, artifactId, features, true);
    }

    @Override
    public ResponseEntity<Resource> instanciateLibrary(
            LibraryType type,
            String componentName,
            String groupId,
            String artifactId) {
        log.warn("Library generation not yet implemented for type: {}", type);
        throw new ServiceException(
                CommonProblemType.ERREUR_INATTENDUE,
                "Library generation not yet implemented"
        );
    }

    @Override
    public ResponseEntity<Resource> instanciateContract(
            ContractType type,
            String componentName,
            String groupId,
            String artifactId) {
        log.info("Received request to generate {} contract", type);

        if (type == ContractType.OPENAPI || type == ContractType.AVRO) {
            String contractFeature = type == ContractType.OPENAPI ? "toni-contract-openapi" : "toni-contract-avro";
            return internalInstanciateStarterKit(
                    StarterKitType.TONIC,
                    componentName,
                    groupId,
                    artifactId,
                    Collections.singletonList(contractFeature),
                    false
            );
        }

        log.warn("Contract generation not yet implemented for type: {}", type);
        throw new ServiceException(
                CommonProblemType.ERREUR_INATTENDUE,
                "Contract generation not yet implemented for type: " + type
        );
    }

    /**
     * Méthode interne de génération de projets.
     *
     * @param starterKitType Type de starter kit
     * @param componentName Nom du composant
     * @param groupId GroupId Maven
     * @param artifactId ArtifactId Maven
     * @param features Liste des fonctionnalités
     * @param validateContractFeatures Si true, vérifie que les fonctionnalités de contrat ne sont pas présentes
     * @return Archive ZIP contenant le projet généré
     * @throws ClientException si les paramètres sont invalides
     * @throws ServiceException en cas d'erreur de génération
     */
    private ResponseEntity<Resource> internalInstanciateStarterKit(
            StarterKitType starterKitType,
            String componentName,
            String groupId,
            String artifactId,
            List<String> features,
            boolean validateContractFeatures) {

        log.info("Received request to generate {} starter kit with features: {}", starterKitType, features);

        try {
            if (starterKitType == null || componentName == null) {
                Map<String, Object> details = new HashMap<>();
                details.put("type", starterKitType == null ? "missing" : "present");
                details.put("componentName", componentName == null ? "missing" : "present");

                throw new ClientException(
                        CommonProblemType.HTTP_PARAMETRE_MANQUANT,
                        "Required parameters missing",
                        details
                );
            }

            String finalGroupId = groupId != null ? groupId : "fr.cnam.default";
            String finalArtifactId = artifactId != null ? artifactId : componentName;

            if (starterKitType == StarterKitType.TONIC) {
                if (validateContractFeatures && features != null && features.stream()
                        .anyMatch(feature -> feature.startsWith("toni-contract-"))) {
                    log.warn("Contract features are not allowed in starter kit generation. Use /contracts endpoint instead.");
                    throw new ClientException(
                            CommonProblemType.DONNEES_INVALIDES,
                            "Contract features are not allowed in starter kit generation. Use /contracts endpoint instead."
                    );
                }

                List<String> validatedFeatures = validationService.validateFeatures(
                        starterKitType,
                        features,
                        !validateContractFeatures
                );
                return tonicProjectGenerationService.getProjectZip(
                        validatedFeatures,
                        finalGroupId,
                        finalArtifactId,
                        componentName,
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
            }

            log.warn("Starter kit generation not yet implemented for type: {}", starterKitType);
            throw new ServiceException(
                    CommonProblemType.ERREUR_INATTENDUE,
                    "Starter kit generation only implemented for TONIC"
            );

        } catch (ClientException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error processing request", e);
            throw new ServiceException(
                    CommonProblemType.ERREUR_INATTENDUE,
                    e,
                    "Error processing request for starter kit type: " + starterKitType
            );
        }
    }
}