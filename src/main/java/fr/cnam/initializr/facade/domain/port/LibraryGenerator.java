package fr.cnam.initializr.facade.domain.port;

import fr.cnam.initializr.facade.controller.rest.model.StarterKitType;
import org.springframework.core.io.Resource;

import java.util.List;

public interface LibraryGenerator {
    Resource generateLibrary(StarterKitType type, String productName, String codeApplicatif);
    List<StarterKitType> getAvailableLibraryStarterKits();
}
