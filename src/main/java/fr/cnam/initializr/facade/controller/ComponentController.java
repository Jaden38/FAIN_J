package fr.cnam.initializr.facade.controller;

import fr.cnam.initializr.facade.business.ComponentArchive;
import fr.cnam.initializr.facade.business.ComponentBusinessService;
import fr.cnam.initializr.facade.business.ComponentRequest;
import fr.cnam.initializr.facade.business.StarterKitBusiness;
import fr.cnam.initializr.facade.controller.rest.api.ComponentsApi;
import fr.cnam.initializr.facade.controller.rest.model.StarterKitType;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ComponentController implements ComponentsApi {
    private final ComponentBusinessService componentService;
    private final ComponentMapper mapper;

    @Override
    public ResponseEntity<List<StarterKitType>> getAllComponents() {
        List<StarterKitBusiness> components = componentService.getAvailableComponents();
        return ResponseEntity.ok(mapper.toApiModel(components));
    }

    @Override
    public ResponseEntity<Resource> instantiateStarterKit(StarterKitType starterKit,
                                                          String productName,
                                                          String codeApplicatif,
                                                          List<String> features) {
        ComponentRequest businessRequest = mapper.toBusiness(starterKit, productName, codeApplicatif, features);

        ComponentArchive archive = componentService.generateComponent(businessRequest);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(archive.getContent());
    }
}