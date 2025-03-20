package fr.cnam.initializr.facade.business.port;

import fr.cnam.initializr.facade.business.model.*;

import java.util.List;

public interface TonicProvider {
    Instance generateComponent(Component request);
    List<String> getComponentFeatures();

    Instance generateContract(Contract request);
    List<String> getAvailableContracts();
}
