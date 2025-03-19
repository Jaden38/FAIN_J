package fr.cnam.initializr.facade.business.port;

import fr.cnam.initializr.facade.business.model.ComponentArchive;
import fr.cnam.initializr.facade.business.model.Contract;
import fr.cnam.initializr.facade.controller.rest.model.ContractType;
import fr.cnam.initializr.facade.controller.rest.model.StarterKitType;

import java.util.List;

public interface ContractProvider {
    ComponentArchive generateContract(Contract request);

    List<ContractType> getAvailableContracts(StarterKitType type);
}