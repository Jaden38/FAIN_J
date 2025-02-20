package fr.cnam.initializr.facade.adapter.rest;

import fr.cnam.initializr.facade.application.service.LibraryService;
import fr.cnam.initializr.facade.controller.rest.api.LibrariesApi;
import fr.cnam.initializr.facade.controller.rest.model.StarterKitType;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class LibraryController implements LibrariesApi {
    private final LibraryService libraryService;

    @Override
    public ResponseEntity<Resource> createLibrary(StarterKitType starterKit,
                                                  String productName,
                                                  String codeApplicatif) {
        Resource resource = libraryService.generateLibrary(starterKit, productName, codeApplicatif);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @Override
    public ResponseEntity<List<StarterKitType>> getAvailableLibraryStarterKits() {
        return ResponseEntity.ok(libraryService.getAvailableLibraryStarterKits());
    }
}