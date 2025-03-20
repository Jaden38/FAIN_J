package fr.cnam.initializr.facade.controller.mapper;

import fr.cnam.initializr.facade.business.model.Library;
import fr.cnam.initializr.facade.controller.rest.model.StarterKitType;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class LibraryMapper {
    StarterKitMapper starterKitMapper;

    public Library toBusiness(StarterKitType starterKitType, String productName, String codeApplicatif) {
        return new Library(starterKitMapper.toBusiness(starterKitType),
                productName,
                codeApplicatif);
    }
}