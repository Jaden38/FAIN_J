package fr.cnam.initializr.facade.controller.mapper;

import fr.cnam.initializr.facade.business.model.ContractRequest;
import fr.cnam.initializr.facade.controller.rest.model.ContractType;
import fr.cnam.initializr.facade.controller.rest.model.StarterKitType;
import org.springframework.stereotype.Component;

@Component
public class ContractMapper {
    public ContractRequest toBusiness(StarterKitType type, ContractType contractType, String productName, String codeApplicatif) {
        return new ContractRequest(type, contractType, productName, codeApplicatif);
    }
}