package fr.cnam.initializr.facade.business.service;

import fr.cnam.initializr.facade.business.model.Instance;
import fr.cnam.initializr.facade.business.model.Library;
import fr.cnam.initializr.facade.business.model.StarterKit;
import fr.cnam.initializr.facade.business.port.TonicProvider;
import fr.cnam.initializr.facade.controller.rest.model.StarterKitType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LibraryService {
    private final TonicProvider tonicProvider;

    public Instance generateLibrary(Library library) {
        throw new UnsupportedOperationException();
    }

    public List<StarterKitType> getAvailableLibraryStarterKits(StarterKit starterKit) {
        throw new UnsupportedOperationException();
    }
}
