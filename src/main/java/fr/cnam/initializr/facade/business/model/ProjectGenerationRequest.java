package fr.cnam.initializr.facade.business.model;

import fr.cnam.initializr.facade.controller.rest.model.StarterKitType;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ProjectGenerationRequest {
    private String productName;
    private String applicationCode;
    private List<String> features;
    private StarterKitType starterType;
    private String groupId;
    private String artifactId;
}