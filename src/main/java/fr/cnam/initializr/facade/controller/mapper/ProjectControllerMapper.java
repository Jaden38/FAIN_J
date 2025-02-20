package fr.cnam.initializr.facade.controller.mapper;

import fr.cnam.initializr.facade.business.model.Project;
import fr.cnam.initializr.facade.business.model.ProjectGenerationRequest;
import fr.cnam.initializr.facade.controller.rest.model.StarterKitType;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProjectControllerMapper {

    public ProjectGenerationRequest toBusinessRequest(
            StarterKitType starterKit,
            String productName,
            String codeApplicatif,
            List<String> features) {
        return ProjectGenerationRequest.builder()
                .starterType(starterKit)
                .productName(productName)
                .applicationCode(codeApplicatif)
                .features(features)
                .groupId("fr.cnam." + productName.toLowerCase())
                .artifactId(codeApplicatif.toLowerCase())
                .build();
    }

    public ResponseEntity<Resource> toResponseEntity(Project project) {
        Resource resource = new ByteArrayResource(project.getContent());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(project.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + project.getFileName() + "\"")
                .body(resource);
    }
}