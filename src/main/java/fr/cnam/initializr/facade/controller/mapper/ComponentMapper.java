package fr.cnam.initializr.facade.controller.mapper;

import fr.cnam.initializr.facade.business.model.Component;
import fr.cnam.initializr.facade.controller.rest.model.StarterKitType;
import lombok.AllArgsConstructor;

import java.util.List;

@org.springframework.stereotype.Component
@AllArgsConstructor
public class ComponentMapper {
    StarterKitMapper starterKitMapper;

    public Component toBusiness(StarterKitType type, String productName, String codeApplicatif, List<String> features) {
        return new Component(
                starterKitMapper.toBusiness(type),
                productName,
                codeApplicatif,
                features
        );
    }

}