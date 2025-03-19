package fr.cnam.initializr.facade.controller.rest;

import fr.cnam.initializr.facade.business.service.LibraryService;
import fr.cnam.initializr.facade.controller.mapper.LibraryMapper;
import fr.cnam.initializr.facade.controller.rest.api.LibrariesApi;
import fr.cnam.initializr.facade.controller.rest.model.StarterKitType;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class LibraryController implements LibrariesApi {
    private final LibraryService libraryService;
    private final LibraryMapper mapper;

    @Override
    public ResponseEntity<Resource> createLibrary(StarterKitType starterKit, String productName, String codeApplicatif) {
        // Not implemented
        return ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<List<StarterKitType>> getAvailableLibraryStarterKits() {
        // Not implemented
        return ResponseEntity.notFound().build();
    }
}