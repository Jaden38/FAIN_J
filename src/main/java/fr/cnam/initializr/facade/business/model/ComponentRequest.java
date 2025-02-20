package fr.cnam.initializr.facade.business.model;

import fr.cnam.initializr.facade.controller.rest.model.StarterKitType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ComponentRequest {
    private StarterKitType type;
    private String productName;
    private String codeApplicatif;
    private List<String> features;

    public ComponentRequest(StarterKitType type, String productName, String codeApplicatif, List<String> features) {
        this.type = type;
        this.productName = productName;
        this.codeApplicatif = codeApplicatif;
        this.features = features;
    }
}