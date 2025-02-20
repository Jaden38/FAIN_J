package fr.cnam.initializr.facade.business.port;

import fr.cnam.initializr.facade.business.model.ComponentArchive;
import fr.cnam.initializr.facade.business.model.ContractRequest;
import fr.cnam.initializr.facade.controller.rest.model.ContractType;
import fr.cnam.initializr.facade.controller.rest.model.StarterKitType;

import java.util.List;

public interface ContractProvider {
    ComponentArchive generateContract(ContractRequest request);

    List<ContractType> getAvailableContracts(StarterKitType type);
}