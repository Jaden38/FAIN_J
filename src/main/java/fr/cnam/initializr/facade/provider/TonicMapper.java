package fr.cnam.initializr.facade.provider;

import fr.cnam.client.tonic.controller.rest.model.ProjectRequest;
import fr.cnam.initializr.facade.business.ComponentArchive;
import fr.cnam.initializr.facade.business.ComponentRequest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class TonicMapper {
    public ProjectRequest toClientRequest(ComponentRequest business) {
        ProjectRequest request = new ProjectRequest();
        request.setDependencies(business.getFeatures());
        request.setGroupId("fr.cnam." + business.getProductName());
        request.setName(business.getProductName());
        return request;
    }

    public ComponentArchive toBusinessArchive(File clientResponse) {
        return new ComponentArchive(new FileSystemResource(clientResponse));
    }
}