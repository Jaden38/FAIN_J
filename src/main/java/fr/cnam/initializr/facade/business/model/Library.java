package fr.cnam.initializr.facade.business.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Library {
    private StarterKit starterKit;
    private String productName;
    private String codeApplicatif;

    public Library(StarterKit starterKit, String productName, String codeApplicatif) {
        this.starterKit = starterKit;
        this.productName = productName;
        this.codeApplicatif = codeApplicatif;
    }
}