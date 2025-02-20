package fr.cnam.initializr.facade.business.port;

import fr.cnam.initializr.facade.business.model.*;
import fr.cnam.initializr.facade.controller.rest.model.ContractType;
import fr.cnam.initializr.facade.controller.rest.model.StarterKitType;
import java.util.List;

public interface ProjectGenerationPort {
    Project generateProject(ProjectGenerationRequest request);
    List<String> getAvailableFeatures(StarterKitType type);
    List<StarterKitType> getAvailableStarters();
    Project generateContract(ContractType format, ProjectGenerationRequest request);
}