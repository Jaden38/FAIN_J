package fr.cnam.initializr.facade.controller.rest;

import fr.cnam.initializr.facade.business.model.ComponentArchive;
import fr.cnam.initializr.facade.business.model.ContractRequest;
import fr.cnam.initializr.facade.business.service.LibraryBusinessService;
import fr.cnam.initializr.facade.controller.mapper.LibraryMapper;
import fr.cnam.initializr.facade.controller.rest.api.LibrariesApi;
import fr.cnam.initializr.facade.controller.rest.model.ContractType;
import fr.cnam.initializr.facade.controller.rest.model.StarterKitType;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class LibraryController implements LibrariesApi {
    private final LibraryBusinessService libraryService;
    private final LibraryMapper mapper;


    @Override
    public ResponseEntity<Resource> createLibrary(StarterKitType starterKit, String productName, String codeApplicatif) {
        return LibrariesApi.super.createLibrary(starterKit, productName, codeApplicatif);
    }

    @Override
    public ResponseEntity<List<StarterKitType>> getAvailableLibraryStarterKits() {
        return LibrariesApi.super.getAvailableLibraryStarterKits();
    }
}