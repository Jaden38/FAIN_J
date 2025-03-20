package fr.cnam.initializr.facade.provider.service;

import fr.cnam.client.tonic.controller.rest.api.TonicProjectGenerationControllerApi;
import fr.cnam.client.tonic.controller.rest.model.InitializrMetadata;
import fr.cnam.client.tonic.controller.rest.model.DependencyGroup;
import fr.cnam.client.tonic.controller.rest.model.Dependency;
import fr.cnam.toni.starter.core.exceptions.CommonProblemType;
import fr.cnam.toni.starter.core.exceptions.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;

import java.util.*;

@Slf4j
@Service
@EnableCaching
@RequiredArgsConstructor
public class TonicFeaturesService {
    private final TonicProjectGenerationControllerApi tonicApi;

    @Cacheable(value = "tonicFeatures")
    public List<String> getAvailableFeatures() {
        try {
            log.debug("Cache miss - fetching features from TONIC service");
            InitializrMetadata metadata = tonicApi.getMetadataConfig();

            if (metadata == null || metadata.getDependencies() == null || metadata.getDependencies().getContent() == null) {
                log.warn("No dependencies found in TONIC service response");
                return new ArrayList<>();
            }

            Set<String> features = new HashSet<>();

            for (DependencyGroup group : metadata.getDependencies().getContent()) {
                if (group.getName() != null && !group.getName().equals("Contracts") && group.getContent() != null) {
                    for (Dependency dependency : group.getContent()) {
                        if (dependency.getId() != null) {
                            features.add(dependency.getId());
                        }
                    }
                }
            }

            List<String> result = new ArrayList<>(features);
            log.debug("Retrieved {} features from TONIC service", result.size());
            return result;
        } catch (RestClientResponseException e) {
            log.error("Error while fetching TONIC features", e);
            throw new ServiceException(CommonProblemType.ERREUR_INATTENDUE, e);
        }
    }
}