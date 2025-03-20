package fr.cnam.initializr.facade.controller.mapper;

import fr.cnam.initializr.facade.business.model.StarterKit;
import fr.cnam.initializr.facade.controller.rest.model.StarterKitType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class StarterKitMapper {

    public StarterKit toBusiness(StarterKitType starterKitType) {
        return switch (starterKitType) {
            case TONIC -> StarterKit.TONIC;
            case HUMAN -> StarterKit.HUMAN;
            case STUMP -> StarterKit.STUMP;
        };
    }

    public List<StarterKitType> toApiModel(List<StarterKit> starterKits) {
        return starterKits.stream()
                .map(starterKit -> StarterKitType.valueOf(starterKit.name()))
                .collect(Collectors.toList());
    }
}