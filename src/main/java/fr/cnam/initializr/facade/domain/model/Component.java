package fr.cnam.initializr.facade.domain.model;

import fr.cnam.initializr.facade.controller.rest.model.StarterKitType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Component {
    private StarterKitType type;
    private String productName;
    private String codeApplicatif;
    private List<String> features;

    public Component(StarterKitType type, String productName, String codeApplicatif, List<String> features) {
    }
}
