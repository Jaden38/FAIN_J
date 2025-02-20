package fr.cnam.initializr.facade.controller.rest;

import fr.cnam.initializr.facade.business.model.ProjectGenerationRequest;
import fr.cnam.initializr.facade.business.service.ProjectGenerationService;
import fr.cnam.initializr.facade.controller.mapper.ProjectControllerMapper;
import fr.cnam.initializr.facade.controller.rest.api.ComponentsApi;
import fr.cnam.initializr.facade.controller.rest.api.ContractsApi;
import fr.cnam.initializr.facade.controller.rest.api.LibrariesApi;
import fr.cnam.initializr.facade.controller.rest.model.ContractType;
import fr.cnam.initializr.facade.controller.rest.model.StarterKitType;
import fr.cnam.toni.starter.core.exceptions.ClientException;
import fr.cnam.toni.starter.core.exceptions.CommonProblemType;
import fr.cnam.toni.starter.core.exceptions.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
public class ProjectGenerationController implements ComponentsApi, ContractsApi, LibrariesApi {

    private final ProjectGenerationService projectService;
    private final ProjectControllerMapper mapper;

    public ProjectGenerationController(
            ProjectGenerationService projectService,
            ProjectControllerMapper mapper) {
        this.projectService = projectService;
        this.mapper = mapper;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return ComponentsApi.super.getRequest();
    }

    @Override
    public ResponseEntity<List<StarterKitType>> getAllComponents() {
        try {
            log.debug("Getting all available components");
            return ResponseEntity.ok(projectService.getAvailableStarters());
        } catch (Exception e) {
            log.error("Error getting available starters", e);
            throw new ServiceException(CommonProblemType.ERREUR_INATTENDUE);
        }
    }

    @Override
    public ResponseEntity<List<String>> getComponentFeatures(StarterKitType starterKit) {
        try {
            log.debug("Getting features for starter kit: {}", starterKit);
            if (starterKit != StarterKitType.TONIC) {
                throw new ClientException(CommonProblemType.DONNEES_INVALIDES);
            }
            return ResponseEntity.ok(projectService.getFeatures(starterKit));
        } catch (ClientException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error getting component features", e);
            throw new ServiceException(CommonProblemType.ERREUR_INATTENDUE);
        }
    }

    @Override
    public ResponseEntity<Resource> instantiateStarterKit(
            StarterKitType starterKit,
            String productName,
            String codeApplicatif,
            List<String> features) {
        try {
            log.debug("Generating project for starter kit: {}, product: {}, code: {}, features: {}",
                    starterKit, productName, codeApplicatif, features);

            if (starterKit != StarterKitType.TONIC) {
                throw new ClientException(CommonProblemType.DONNEES_INVALIDES);
            }

            ProjectGenerationRequest request = mapper.toBusinessRequest(
                    starterKit, productName, codeApplicatif, features);

            var project = projectService.generateProject(request);
            return mapper.toResponseEntity(project);
        } catch (ClientException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error generating project", e);
            throw new ServiceException(CommonProblemType.ERREUR_INATTENDUE);
        }
    }

    @Override
    public ResponseEntity<List<ContractType>> getAvailableContracts(StarterKitType starterKit) {
        try {
            log.debug("Getting available contracts for starter kit: {}", starterKit);
            if (starterKit != StarterKitType.TONIC) {
                throw new ClientException(CommonProblemType.DONNEES_INVALIDES);
            }
            return ResponseEntity.ok(List.of(ContractType.OPENAPI, ContractType.AVRO));
        } catch (ClientException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error getting available contracts", e);
            throw new ServiceException(CommonProblemType.ERREUR_INATTENDUE);
        }
    }

    @Override
    public ResponseEntity<Resource> createContract(
            StarterKitType starterKit,
            ContractType contractType,
            String productName,
            String codeApplicatif) {
        try {
            log.debug("Generating contract: {} for starter kit: {}, product: {}, code: {}",
                    contractType, starterKit, productName, codeApplicatif);

            if (starterKit != StarterKitType.TONIC) {
                throw new ClientException(CommonProblemType.DONNEES_INVALIDES);
            }

            ProjectGenerationRequest request = mapper.toBusinessRequest(
                    starterKit, productName, codeApplicatif, null);

            var project = projectService.generateContract(contractType, request);
            return mapper.toResponseEntity(project);
        } catch (ClientException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error generating contract", e);
            throw new ServiceException(CommonProblemType.ERREUR_INATTENDUE);
        }
    }

    @Override
    public ResponseEntity<List<StarterKitType>> getAvailableLibraryStarterKits() {
        return ResponseEntity.ok(List.of());
    }

    @Override
    public ResponseEntity<Resource> createLibrary(
            StarterKitType starterKit,
            String productName,
            String codeApplicatif) {
        throw new ServiceException(CommonProblemType.RESSOURCE_NON_TROUVEE);
    }
}