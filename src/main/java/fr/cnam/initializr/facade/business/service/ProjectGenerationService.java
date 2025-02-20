package fr.cnam.initializr.facade.business.service;

import fr.cnam.initializr.facade.business.model.*;
import fr.cnam.initializr.facade.business.port.ProjectGenerationPort;
import fr.cnam.toni.starter.core.exceptions.ClientException;
import fr.cnam.toni.starter.core.exceptions.CommonProblemType;
import fr.cnam.initializr.facade.controller.rest.model.ContractType;
import fr.cnam.initializr.facade.controller.rest.model.StarterKitType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.util.List;

@Service
public class ProjectGenerationService {
    private final ProjectGenerationPort projectGenerationPort;

    public ProjectGenerationService(ProjectGenerationPort projectGenerationPort) {
        this.projectGenerationPort = projectGenerationPort;
    }

    public Project generateProject(ProjectGenerationRequest request) {
        validateProjectRequest(request);
        return projectGenerationPort.generateProject(request);
    }

    public List<String> getFeatures(StarterKitType type) {
        if (type == null) {
            throw new ClientException(CommonProblemType.DONNEES_INVALIDES);
        }
        return projectGenerationPort.getAvailableFeatures(type);
    }

    public Project generateContract(ContractType format, ProjectGenerationRequest request) {
        validateContractRequest(format, request);
        return projectGenerationPort.generateContract(format, request);
    }

    public List<StarterKitType> getAvailableStarters() {
        return projectGenerationPort.getAvailableStarters();
    }

    private void validateProjectRequest(ProjectGenerationRequest request) {
        if (request == null) {
            throw new ClientException(CommonProblemType.DONNEES_INVALIDES);
        }

        // Utilisation de Map pour passer des détails d'erreur
        if (!StringUtils.hasText(request.getProductName())) {
            throw new ClientException(
                    CommonProblemType.DONNEES_INVALIDES_MSG_AVEC_PROBLEMES,
                    "Le nom du produit est obligatoire"
            );
        }

        if (!StringUtils.hasText(request.getApplicationCode())) {
            throw new ClientException(
                    CommonProblemType.DONNEES_INVALIDES_MSG_AVEC_PROBLEMES,
                    "Le code applicatif est obligatoire"
            );
        }

        if (request.getStarterType() == null) {
            throw new ClientException(
                    CommonProblemType.DONNEES_INVALIDES_MSG_AVEC_PROBLEMES,
                    "Le type de starter est obligatoire"
            );
        }
    }

    private void validateContractRequest(ContractType format, ProjectGenerationRequest request) {
        if (format == null) {
            throw new ClientException(
                    CommonProblemType.DONNEES_INVALIDES_MSG_AVEC_PROBLEMES,
                    "Le format du contrat est obligatoire"
            );
        }
        validateProjectRequest(request);
    }
}