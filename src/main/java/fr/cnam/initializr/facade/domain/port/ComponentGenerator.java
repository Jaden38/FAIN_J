package fr.cnam.initializr.facade.domain.port;

import fr.cnam.initializr.facade.controller.rest.model.StarterKitType;
import fr.cnam.initializr.facade.domain.model.Component;
import org.springframework.core.io.Resource;

import java.util.List;

public interface ComponentGenerator {
    Resource generateComponent(Component component);
    List<StarterKitType> getAvailableComponents();
    List<String> getComponentFeatures(StarterKitType type);

}

