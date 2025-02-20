package fr.cnam.initializr.facade.adapter.rest;

import fr.cnam.initializr.facade.application.service.ComponentService;
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
    private final
    ComponentService componentService;

    @Override
    public ResponseEntity<List<StarterKitType>> getAllComponents() {
        return ResponseEntity.ok(componentService.getAvailableComponents());
    }

    @Override
    public ResponseEntity<List<String>> getComponentFeatures(StarterKitType starterKit) {
        return ResponseEntity.ok(componentService.getComponentFeatures(starterKit));
    }

    @Override
    public ResponseEntity<Resource> instantiateStarterKit(StarterKitType starterKit,
                                                          String productName,
                                                          String codeApplicatif,
                                                          List<String> features) {
        Resource resource = componentService.generateComponent(starterKit, productName, codeApplicatif, features);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}