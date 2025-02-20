package fr.cnam.initializr.facade.business;

import java.util.List;

public interface ComponentProvider {
    ComponentArchive generateComponent(ComponentRequest request);

    List<StarterKitBusiness> getAvailableComponents();
}