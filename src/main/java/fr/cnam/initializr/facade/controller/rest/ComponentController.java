package fr.cnam.initializr.facade.controller.rest;

import fr.cnam.initializr.facade.business.model.Instance;
import fr.cnam.initializr.facade.business.model.Component;
import fr.cnam.initializr.facade.business.model.StarterKit;
import fr.cnam.initializr.facade.business.service.ComponentService;
import fr.cnam.initializr.facade.controller.mapper.ComponentMapper;
import fr.cnam.initializr.facade.controller.mapper.StarterKitMapper;
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
    private final ComponentService componentService;
    private final ComponentMapper mapper;
    private final StarterKitMapper starterKitMapper;

    @Override
    public ResponseEntity<List<StarterKitType>> getAllComponents() {
        List<StarterKit> components = componentService.getAvailableStarterKits();

        return ResponseEntity.ok(starterKitMapper.toApiModel(components));
    }

    @Override
    public ResponseEntity<Resource> instantiateStarterKit(StarterKitType starterKit,
                                                          String productName,
                                                          String codeApplicatif,
                                                          List<String> features) {
        Component businessRequest = mapper.toBusiness(starterKit, productName, codeApplicatif, features);

        Instance instance = componentService.generateComponent(businessRequest);

        String filename = String.format("%s-%s.zip", productName, codeApplicatif);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/zip"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(instance.getContent());
    }

    @Override
    public ResponseEntity<List<String>> getComponentFeatures(StarterKitType starterKitType) {
        StarterKit starterKit = starterKitMapper.toBusiness(starterKitType);

        List<String> features = componentService.getComponentFeatures(starterKit);

        return ResponseEntity.ok(features);
    }
}