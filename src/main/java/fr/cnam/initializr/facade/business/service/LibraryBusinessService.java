package fr.cnam.initializr.facade.business.service;

import fr.cnam.initializr.facade.business.model.ComponentArchive;
import fr.cnam.initializr.facade.business.model.LibraryRequest;
import fr.cnam.initializr.facade.business.port.LibraryProvider;
import fr.cnam.initializr.facade.controller.rest.model.ContractType;
import fr.cnam.initializr.facade.controller.rest.model.StarterKitType;
import fr.cnam.toni.starter.core.exceptions.CommonProblemType;
import fr.cnam.toni.starter.core.exceptions.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LibraryBusinessService {
    private final LibraryProvider provider;

    public ComponentArchive generateLibrary(LibraryRequest request) {
        throw new ServiceException(CommonProblemType.RESSOURCE_NON_TROUVEE);
    }

    public List<ContractType> getAvailableLibraryStarterKits(StarterKitType type) {
        throw new ServiceException(CommonProblemType.RESSOURCE_NON_TROUVEE);
    }

    private void validateRequest(LibraryRequest request) {
        // Business validation logic
    }
}
