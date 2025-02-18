package fr.cnam.initializr.facade.controller.rest.api;

import fr.cnam.initializr.facade.config.InitializerConfig;
import fr.cnam.initializr.facade.controller.rest.model.ContractType;
import fr.cnam.initializr.facade.controller.rest.model.StarterKitType;
import fr.cnam.initializr.facade.service.FeatureValidationService;
import fr.cnam.initializr.facade.service.tonic.TonicFeaturesService;
import fr.cnam.initializr.facade.service.tonic.TonicProjectGenerationService;
import fr.cnam.toni.starter.core.exceptions.ClientException;
import fr.cnam.toni.starter.core.exceptions.CommonProblemType;
import fr.cnam.toni.starter.core.exceptions.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.*;

/**
 * Contrôleur REST pour la génération de projets et la gestion des composants.
 * <p>
 * Ce contrôleur gère les endpoints REST pour :
 * <ul>
 *   <li>La génération de projets à partir de kits de démarrage (starter kits)</li>
 *   <li>La création de contrats d'API (OpenAPI, AVRO)</li>
 *   <li>La gestion des bibliothèques</li>
 * </ul>
 * Il implémente les interfaces ComponentsApi, ContractsApi et LibrariesApi pour
 * fournir ces fonctionnalités.
 */
@Slf4j
@RestController
@Validated
public class ProjectGenerationController implements ComponentsApi, ContractsApi, LibrariesApi {

    private final TonicProjectGenerationService tonicProjectGenerationService;
    private final FeatureValidationService validationService;
    private final TonicFeaturesService tonicFeaturesService;
    private final InitializerConfig initializerConfig;

    /**
     * Construit un nouveau contrôleur de génération de projets.
     *
     * @param tonicProjectGenerationService Service de génération de projets TONIC
     * @param validationService Service de validation des fonctionnalités
     * @param tonicFeaturesService Service de gestion des fonctionnalités TONIC
     * @param initializerConfig Configuration de l'initialisateur
     */
    public ProjectGenerationController(
            TonicProjectGenerationService tonicProjectGenerationService,
            FeatureValidationService validationService,
            TonicFeaturesService tonicFeaturesService,
            InitializerConfig initializerConfig) {
        this.tonicProjectGenerationService = tonicProjectGenerationService;
        this.validationService = validationService;
        this.tonicFeaturesService = tonicFeaturesService;
        this.initializerConfig = initializerConfig;
    }


    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * Récupère la liste de tous les types de composants disponibles.
     *
     * @return ResponseEntity contenant la liste des types de starter kits
     */
    @Override
    public ResponseEntity<List<StarterKitType>> getAllComponents() {
        return ResponseEntity.ok(Arrays.asList(StarterKitType.values()));
    }

    /**
     * Récupère la liste des fonctionnalités disponibles pour un type de starter kit.
     *
     * @param starterKit Le type de starter kit
     * @return ResponseEntity contenant la liste des fonctionnalités disponibles
     * @throws ClientException si le type de starter kit n'est pas supporté
     */
    @Override
    public ResponseEntity<List<String>> getComponentFeatures(StarterKitType starterKit) {
        if (starterKit == StarterKitType.TONIC) {
            List<String> features = tonicFeaturesService.getAvailableFeatures();
            return ResponseEntity.ok(features);
        }
        throw new ClientException(CommonProblemType.DONNEES_INVALIDES);
    }

    /**
     * Génère un projet à partir d'un starter kit avec les fonctionnalités spécifiées.
     *
     * @param starterKit Le type de starter kit à utiliser
     * @param codeApplicatif Le code applicatif du projet
     * @param features Liste des fonctionnalités à inclure
     * @return ResponseEntity contenant l'archive du projet généré
     */
    @Override
    public ResponseEntity<Resource> instantiateStarterKit(
            StarterKitType starterKit,
            String codeApplicatif,
            List<String> features) {
        return internalInstanciateStarterKit(starterKit, codeApplicatif, features, true);
    }

    /**
     * Récupère la liste des types de contrats disponibles pour un starter kit.
     *
     * @param starterKit Le type de starter kit
     * @return ResponseEntity contenant la liste des types de contrats disponibles
     * @throws ClientException si le type de starter kit n'est pas supporté
     */
    @Override
    public ResponseEntity<List<ContractType>> getAvailableContracts(StarterKitType starterKit) {
        if (starterKit == StarterKitType.TONIC) {
            return ResponseEntity.ok(Arrays.asList(ContractType.values()));
        }
        throw new ClientException(CommonProblemType.DONNEES_INVALIDES);
    }


    /**
     * Crée un contrat pour un projet donné.
     *
     * @param starterKit Le type de starter kit
     * @param contractType Le type de contrat à générer
     * @param codeApplicatif Le code applicatif du projet
     * @return ResponseEntity contenant l'archive du contrat généré
     * @throws ServiceException si la génération échoue
     */
    @Override
    public ResponseEntity<Resource> createContract(
            StarterKitType starterKit,
            ContractType contractType,
            String codeApplicatif) {

        log.info("Received request to generate {} contract for {} starter kit", contractType, starterKit);

        if (starterKit == StarterKitType.TONIC &&
                (contractType == ContractType.OPENAPI || contractType == ContractType.AVRO)) {
            String contractFeature = contractType == ContractType.OPENAPI ?
                    "toni-contract-openapi" : "toni-contract-avro";
            return internalInstanciateStarterKit(
                    starterKit,
                    codeApplicatif,
                    Collections.singletonList(contractFeature),
                    false
            );
        }

        throw new ServiceException(CommonProblemType.ERREUR_INATTENDUE);
    }

    /**
     * Récupère le type de starter kit disponible pour la création de bibliothèques.
     *
     * @return ResponseEntity contenant le type de starter kit pour les bibliothèques
     */
    @Override
    public ResponseEntity<StarterKitType> getAvailableLibraryStarterKits() {
        return ResponseEntity.ok(StarterKitType.TONIC);
    }

    /**
     * Crée une nouvelle bibliothèque.
     * Cette fonctionnalité n'est pas encore implémentée.
     *
     * @param starterKit Le type de starter kit
     * @param codeApplicatif Le code applicatif de la bibliothèque
     * @return ResponseEntity contenant l'archive de la bibliothèque générée
     * @throws ServiceException car la fonctionnalité n'est pas implémentée
     */
    @Override
    public ResponseEntity<Resource> createLibrary(
            StarterKitType starterKit,
            String codeApplicatif) {
        throw new ServiceException(CommonProblemType.ERREUR_INATTENDUE);
    }


    /**
     * Méthode interne pour la génération de projets.
     * <p>
     * Cette méthode gère la logique commune de génération de projets pour les
     * différents types de requêtes (projet standard, contrat, bibliothèque).
     *
     * @param starterKitType Le type de starter kit
     * @param codeApplicatif Le code applicatif du projet
     * @param features Liste des fonctionnalités à inclure
     * @param validateContractFeatures Si true, vérifie que les fonctionnalités de contrat ne sont pas incluses
     * @return ResponseEntity contenant l'archive du projet généré
     * @throws ClientException si les paramètres sont invalides
     * @throws ServiceException si la génération échoue
     */
    private ResponseEntity<Resource> internalInstanciateStarterKit(
            StarterKitType starterKitType,
            String codeApplicatif,
            List<String> features,
            boolean validateContractFeatures) {

        log.info("Received request to generate {} starter kit with features: {}", starterKitType, features);

        if (starterKitType == StarterKitType.TONIC) {

            String groupId = initializerConfig.getTonicDefaultGroupId();
            String artifactId = codeApplicatif.replace("_", "-").toLowerCase();

            if (validateContractFeatures && features != null && features.stream()
                    .anyMatch(feature -> feature.startsWith("toni-contract-"))) {
                throw new ClientException(CommonProblemType.DONNEES_INVALIDES);
            }

            List<String> validatedFeatures = validationService.validateFeatures(
                    starterKitType,
                    features,
                    !validateContractFeatures
            );

            return tonicProjectGenerationService.getProjectZip(
                    validatedFeatures,
                    groupId,
                    artifactId,
                    codeApplicatif,
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

        throw new ServiceException(CommonProblemType.ERREUR_INATTENDUE);
    }
}