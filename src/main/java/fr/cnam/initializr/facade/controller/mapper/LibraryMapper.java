package fr.cnam.initializr.facade.controller.mapper;

import fr.cnam.initializr.facade.business.model.Library;
import fr.cnam.initializr.facade.controller.rest.model.StarterKitType;
import org.springframework.stereotype.Component;

@Component
public class LibraryMapper {
    public Library toBusiness(StarterKitType type, String productName, String codeApplicatif) {
        return new Library(type, productName, codeApplicatif);
    }
}