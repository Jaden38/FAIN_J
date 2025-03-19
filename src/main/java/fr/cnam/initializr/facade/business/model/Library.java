package fr.cnam.initializr.facade.business.model;

import fr.cnam.initializr.facade.controller.rest.model.StarterKitType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Library {
    private StarterKitType type;
    private String productName;
    private String codeApplicatif;

    public Library(StarterKitType type, String productName, String codeApplicatif) {
        this.type = type;
        this.productName = productName;
        this.codeApplicatif = codeApplicatif;
    }
}