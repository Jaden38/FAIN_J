package fr.cnam.initializr.facade.controller.mapper;

import fr.cnam.initializr.facade.business.model.ComponentRequest;
import fr.cnam.initializr.facade.business.model.StarterKitBusiness;
import fr.cnam.initializr.facade.controller.rest.model.StarterKitType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ComponentMapper {
    public ComponentRequest toBusiness(StarterKitType type, String productName, String codeApplicatif, List<String> features) {
        return new ComponentRequest(
                StarterKitType.valueOf(type.name()),
                productName,
                codeApplicatif,
                features
        );
    }

    public List<StarterKitType> toApiModel(List<StarterKitBusiness> businessTypes) {
        return businessTypes.stream()
                .map(type -> StarterKitType.valueOf(type.name()))
                .collect(Collectors.toList());
    }
}