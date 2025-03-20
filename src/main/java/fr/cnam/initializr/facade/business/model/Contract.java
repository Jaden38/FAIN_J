package fr.cnam.initializr.facade.business.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Contract {
    private StarterKit starterKit;
    @NotBlank
    private String contractType;
    @NotBlank
    private String productName;
    @NotBlank
    private String codeApplicatif;

    public Contract(StarterKit starterKit, String contractType, String productName, String codeApplicatif) {
        this.starterKit = starterKit;
        this.contractType = contractType;
        this.productName = productName;
        this.codeApplicatif = codeApplicatif;
    }
}