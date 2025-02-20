package fr.cnam.initializr.facade.business.service;

import fr.cnam.initializr.facade.business.model.ComponentArchive;
import fr.cnam.initializr.facade.business.model.ContractRequest;
import fr.cnam.initializr.facade.business.port.ContractProvider;
import fr.cnam.initializr.facade.controller.rest.model.ContractType;
import fr.cnam.initializr.facade.controller.rest.model.StarterKitType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContractBusinessService {
    private final ContractProvider provider;

    public ComponentArchive generateContract(ContractRequest request) {
        validateRequest(request);
        return provider.generateContract(request);
    }

    public List<ContractType> getAvailableContracts(StarterKitType type) {
        return provider.getAvailableContracts(type);
    }

    private void validateRequest(ContractRequest request) {
        // Business validation logic
    }
}