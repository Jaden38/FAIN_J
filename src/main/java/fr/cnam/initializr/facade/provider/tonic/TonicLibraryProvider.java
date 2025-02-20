package fr.cnam.initializr.facade.provider.tonic;

import fr.cnam.client.tonic.controller.rest.api.TonicProjectGenerationControllerApi;
import fr.cnam.initializr.facade.business.model.ComponentArchive;
import fr.cnam.initializr.facade.business.model.LibraryRequest;
import fr.cnam.initializr.facade.business.port.LibraryProvider;
import fr.cnam.initializr.facade.controller.rest.model.StarterKitType;
import fr.cnam.initializr.facade.provider.mapper.TonicMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TonicLibraryProvider implements LibraryProvider {

    @Override
    public ComponentArchive generateLibrary(LibraryRequest request) {
        return null;
    }

    @Override
    public List<StarterKitType> getAvailableLibraryStarterKits() {
        return List.of();
    }
}