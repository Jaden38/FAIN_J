package fr.cnam.initializr.facade.business.port;

import fr.cnam.initializr.facade.business.model.ComponentArchive;
import fr.cnam.initializr.facade.business.model.Library;
import fr.cnam.initializr.facade.controller.rest.model.StarterKitType;

import java.util.List;

public interface LibraryProvider {
    ComponentArchive generateLibrary(Library request);
    List<StarterKitType> getAvailableLibraryStarterKits();
}