package fr.cnam.initializr.facade.business.port;

import fr.cnam.initializr.facade.business.model.Component;
import fr.cnam.initializr.facade.business.model.Contract;
import fr.cnam.initializr.facade.business.model.Instance;

import java.util.List;

public interface TonicProvider {
    Instance generateComponent(Component request);
    List<String> getComponentFeatures();

    Instance generateContract(Contract request);
    List<String> getAvailableContracts();
}
