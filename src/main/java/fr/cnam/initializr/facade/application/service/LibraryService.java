package fr.cnam.initializr.facade.application.service;

import fr.cnam.initializr.facade.controller.rest.model.StarterKitType;
import fr.cnam.initializr.facade.domain.port.LibraryGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LibraryService {
    private final LibraryGenerator libraryGenerator;

    public Resource generateLibrary(StarterKitType type, String productName, String codeApplicatif) {
        return libraryGenerator.generateLibrary(type, productName, codeApplicatif);
    }

    public List<StarterKitType> getAvailableLibraryStarterKits() {
        return libraryGenerator.getAvailableLibraryStarterKits();
    }
}