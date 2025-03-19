package fr.cnam.initializr.facade.business.service;

import fr.cnam.initializr.facade.business.model.ComponentArchive;
import fr.cnam.initializr.facade.business.model.ContractRequest;
import fr.cnam.initializr.facade.business.port.ContractProvider;
import fr.cnam.initializr.facade.controller.rest.model.ContractType;
import fr.cnam.initializr.facade.controller.rest.model.StarterKitType;
import fr.cnam.toni.starter.core.exceptions.ClientException;
import fr.cnam.toni.starter.core.exceptions.CommonProblemType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContractBusinessService {
    private final ContractProvider provider;
    private final MetricBusinessService metricBusinessService;
    private final ValidationService validationService;

    public ComponentArchive generateContract(ContractRequest request) {
        validateRequest(request);
        ComponentArchive archive = provider.generateContract(request);
        metricBusinessService.recordContractGeneration(request);
        return archive;
    }

    public List<ContractType> getAvailableContracts(StarterKitType type) {
        return provider.getAvailableContracts(type);
    }

    private void validateRequest(ContractRequest request) {
        validationService.validateStarterKitType(request.getType());
        validationService.validateProductName(request.getProductName());
        validationService.validateCodeApplicatif(request.getCodeApplicatif());
        validateContractType(request.getContractType());
    }

    private void validateContractType(ContractType contractType) {
        if (contractType == null) {
            log.warn("Contract type cannot be null");
            throw new ClientException(
                    CommonProblemType.DONNEES_INVALIDES_MSG_AVEC_PROBLEMES,
                    "Contract type cannot be null"
            );
        }

        List<ContractType> availableContractTypes = getAvailableContracts(StarterKitType.TONIC);
        if (!availableContractTypes.contains(contractType)) {
            log.warn("Invalid contract type: {}. Available contract types are: {}", contractType, availableContractTypes);
            throw new ClientException(
                    CommonProblemType.DONNEES_INVALIDES_MSG_AVEC_PROBLEMES,
                    "Invalid contract type: " + contractType + ". Available contract types are: " + availableContractTypes
            );
        }
    }
}