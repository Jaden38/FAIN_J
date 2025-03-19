package fr.cnam.initializr.facade.controller.mapper;

import fr.cnam.initializr.facade.business.model.Contract;
import fr.cnam.initializr.facade.controller.rest.model.ContractType;
import fr.cnam.initializr.facade.controller.rest.model.StarterKitType;
import org.springframework.stereotype.Component;

@Component
public class ContractMapper {
    public Contract toBusiness(StarterKitType type, ContractType contractType, String productName, String codeApplicatif) {
        return new Contract(type, contractType, productName, codeApplicatif);
    }
}