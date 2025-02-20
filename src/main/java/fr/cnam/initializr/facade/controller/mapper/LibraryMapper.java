package fr.cnam.initializr.facade.controller.mapper;

import fr.cnam.initializr.facade.business.model.LibraryRequest;
import fr.cnam.initializr.facade.controller.rest.model.StarterKitType;
import org.springframework.stereotype.Component;

@Component
public class LibraryMapper {
    public LibraryRequest toBusiness(StarterKitType type, String productName, String codeApplicatif) {
        return new LibraryRequest(type, productName, codeApplicatif);
    }
}