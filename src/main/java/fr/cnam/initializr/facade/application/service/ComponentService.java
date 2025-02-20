package fr.cnam.initializr.facade.application.service;

import fr.cnam.initializr.facade.controller.rest.model.StarterKitType;
import fr.cnam.initializr.facade.domain.model.Component;
import fr.cnam.initializr.facade.domain.port.ComponentGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ComponentService {
    private final ComponentGenerator componentGenerator;



    public Resource generateComponent(StarterKitType type, String productName, String codeApplicatif, List<String> features) {
        Component component = new Component(type, productName, codeApplicatif, features);
        return componentGenerator.generateComponent(component);
    }

    public List<StarterKitType> getAvailableComponents() {
        return componentGenerator.getAvailableComponents();
    }

    public List<String> getComponentFeatures(StarterKitType type) {
        return componentGenerator.getComponentFeatures(type);
    }
}