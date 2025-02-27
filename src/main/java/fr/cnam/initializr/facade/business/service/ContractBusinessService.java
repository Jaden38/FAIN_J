package fr.cnam.initializr.facade.business.service;

import fr.cnam.initializr.facade.business.model.ComponentArchive;
import fr.cnam.initializr.facade.business.model.ContractRequest;
import fr.cnam.initializr.facade.business.port.ContractProvider;
import fr.cnam.initializr.facade.controller.rest.model.ContractType;
import fr.cnam.initializr.facade.controller.rest.model.StarterKitType;
import fr.cnam.toni.starter.core.exceptions.CommonProblemType;
import fr.cnam.toni.starter.core.exceptions.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContractBusinessService {
    private final ContractProvider provider;
    private final MetricBusinessService metricBusinessService;

    public ComponentArchive generateContract(ContractRequest request) {
        validateRequest(request);
        try {
            ComponentArchive archive = provider.generateContract(request);
            metricBusinessService.recordContractGeneration(request);
            return archive;
        } catch (Exception e) {
            throw new ServiceException(CommonProblemType.ERREUR_INATTENDUE, e);
        }
    }

    public List<ContractType> getAvailableContracts(StarterKitType type) {
        return provider.getAvailableContracts(type);
    }

    private void validateRequest(ContractRequest request) {
        // Business validation logic
    }
}