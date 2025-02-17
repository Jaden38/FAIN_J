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

@Slf4j
@RestController
@Validated
public class ProjectGenerationController implements ComponentsApi, ContractsApi, LibrariesApi {

    private final TonicProjectGenerationService tonicProjectGenerationService;
    private final FeatureValidationService validationService;
    private final TonicFeaturesService tonicFeaturesService;
    private final InitializerConfig initializerConfig;

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

    @Override
    public ResponseEntity<List<StarterKitType>> getAllComponents() {
        return ResponseEntity.ok(Arrays.asList(StarterKitType.values()));
    }

    @Override
    public ResponseEntity<List<String>> getComponentFeatures(StarterKitType starterKit) {
        if (starterKit == StarterKitType.TONIC) {
            List<String> features = tonicFeaturesService.getAvailableFeatures();
            return ResponseEntity.ok(features);
        }
        throw new ClientException(CommonProblemType.DONNEES_INVALIDES);
    }

    @Override
    public ResponseEntity<Resource> instantiateStarterKit(
            StarterKitType starterKit,
            String codeApplicatif,
            List<String> features) {
        return internalInstanciateStarterKit(starterKit, codeApplicatif, features, true);
    }

    @Override
    public ResponseEntity<List<ContractType>> getAvailableContracts(StarterKitType starterKit) {
        if (starterKit == StarterKitType.TONIC) {
            return ResponseEntity.ok(Arrays.asList(ContractType.values()));
        }
        throw new ClientException(CommonProblemType.DONNEES_INVALIDES);
    }

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

    @Override
    public ResponseEntity<StarterKitType> getAvailableLibraryStarterKits() {
        return ResponseEntity.ok(StarterKitType.TONIC);
    }

    @Override
    public ResponseEntity<Resource> createLibrary(
            StarterKitType starterKit,
            String codeApplicatif) {
        throw new ServiceException(CommonProblemType.ERREUR_INATTENDUE);
    }

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