package fr.cnam.initializr.facade.application.service;

import fr.cnam.initializr.facade.controller.rest.model.ContractType;
import fr.cnam.initializr.facade.controller.rest.model.StarterKitType;
import fr.cnam.initializr.facade.domain.port.ContractGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContractService {
    private final ContractGenerator contractGenerator;

    public Resource generateContract(StarterKitType type, ContractType contractType, String productName, String codeApplicatif) {
        return contractGenerator.generateContract(type, contractType, productName, codeApplicatif);
    }

    public List<ContractType> getAvailableContracts(StarterKitType type) {
        return contractGenerator.getAvailableContracts(type);
    }
}