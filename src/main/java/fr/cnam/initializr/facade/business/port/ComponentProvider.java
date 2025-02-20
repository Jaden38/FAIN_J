package fr.cnam.initializr.facade.business.port;

import fr.cnam.initializr.facade.business.model.ComponentArchive;
import fr.cnam.initializr.facade.business.model.ComponentRequest;
import fr.cnam.initializr.facade.business.model.StarterKitBusiness;
import fr.cnam.initializr.facade.controller.rest.model.StarterKitType;
import java.util.List;

public interface ComponentProvider {
    ComponentArchive generateComponent(ComponentRequest request);
    List<StarterKitBusiness> getAvailableComponents();
    List<String> getComponentFeatures(StarterKitType type);
}