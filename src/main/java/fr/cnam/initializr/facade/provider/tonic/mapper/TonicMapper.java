package fr.cnam.initializr.facade.provider.tonic.mapper;

import fr.cnam.client.tonic.controller.rest.model.TonicDependenciesResponse;
import fr.cnam.initializr.facade.business.model.Project;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class TonicMapper {

    public Project toProject(Resource resource) {
        if (resource == null) {
            return null;
        }

        try {
            return Project.builder()
                    .content(resource.getContentAsByteArray())
                    .fileName("project.zip")
                    .contentType("application/zip")
                    .build();
        } catch (IOException e) {
            throw new RuntimeException("Error reading project content", e);
        }
    }

    public List<String> toFeaturesList(TonicDependenciesResponse response) {
        if (response == null || response.getDependencies() == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(response.getDependencies().keySet());
    }
}