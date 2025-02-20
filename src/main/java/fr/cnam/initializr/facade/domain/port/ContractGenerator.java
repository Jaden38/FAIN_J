package fr.cnam.initializr.facade.domain.port;

import fr.cnam.initializr.facade.controller.rest.model.ContractType;
import fr.cnam.initializr.facade.controller.rest.model.StarterKitType;
import org.springframework.core.io.Resource;

import java.util.List;

public interface ContractGenerator {
    Resource generateContract(StarterKitType type, ContractType contractType, String productName, String codeApplicatif);
    List<ContractType> getAvailableContracts(StarterKitType type);
}
