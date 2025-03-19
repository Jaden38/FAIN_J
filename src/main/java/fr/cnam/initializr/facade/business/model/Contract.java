package fr.cnam.initializr.facade.business.model;

import fr.cnam.initializr.facade.controller.rest.model.ContractType;
import fr.cnam.initializr.facade.controller.rest.model.StarterKitType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Contract {
    private StarterKitType type;
    private ContractType contractType;
    private String productName;
    private String codeApplicatif;

    public Contract(StarterKitType type, ContractType contractType, String productName, String codeApplicatif) {
        this.type = type;
        this.contractType = contractType;
        this.productName = productName;
        this.codeApplicatif = codeApplicatif;
    }
}