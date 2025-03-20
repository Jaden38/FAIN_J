package fr.cnam.initializr.facade.business.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Component {
    @NotNull
    private StarterKit starterKit;
    @NotBlank
    private String productName;
    @NotBlank
    private String codeApplicatif;
    private List<String> features;

    public Component(StarterKit starterKit, String productName, String codeApplicatif, List<String> features) {
        this.starterKit = starterKit;
        this.productName = productName;
        this.codeApplicatif = codeApplicatif;
        this.features = features;
    }
}