package fr.cnam.initializr.facade.controller.rest;

import fr.cnam.initializr.facade.business.model.ComponentArchive;
import fr.cnam.initializr.facade.business.service.ComponentBusinessService;
import fr.cnam.initializr.facade.business.model.ComponentRequest;
import fr.cnam.initializr.facade.business.model.StarterKitBusiness;
import fr.cnam.initializr.facade.controller.mapper.ComponentMapper;
import fr.cnam.initializr.facade.controller.rest.api.ComponentsApi;
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

        String filename = String.format("%s-%s.zip", productName, codeApplicatif);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/zip"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(archive.getContent());
    }

    @Override
    public ResponseEntity<List<String>> getComponentFeatures(StarterKitType starterKit) {
        List<String> features = componentService.getComponentFeatures(starterKit);
        return ResponseEntity.ok(features);
    }
}