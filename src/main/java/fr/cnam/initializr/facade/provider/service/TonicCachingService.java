package fr.cnam.initializr.facade.provider.service;

import fr.cnam.client.tonic.controller.rest.api.TonicProjectGenerationControllerApi;
import fr.cnam.client.tonic.controller.rest.model.Dependency;
import fr.cnam.client.tonic.controller.rest.model.DependencyGroup;
import fr.cnam.client.tonic.controller.rest.model.InitializrMetadata;
import fr.cnam.toni.starter.core.exceptions.CommonProblemType;
import fr.cnam.toni.starter.core.exceptions.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Service responsable de la mise en cache des features TONIC.
 * Ce service est séparé pour éviter le problème d'auto-invocation du cache Spring.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TonicCachingService {

    private final TonicProjectGenerationControllerApi tonicApi;

    /**
     * Récupère toutes les fonctionnalités disponibles, organisées par catégorie
     *
     * @return Une map avec les catégories en clé et les listes de fonctionnalités en valeur
     */
    @Cacheable(value = "tonicFeaturesByCategory")
    public Map<String, List<String>> getAllFeaturesByCategory() {
        try {
            log.debug("Cache miss - fetching all features by category from TONIC service");
            InitializrMetadata metadata = tonicApi.getMetadataConfig();

            if (metadata == null || metadata.getDependencies() == null || metadata.getDependencies().getContent() == null) {
                log.warn("No dependencies found in TONIC service response");
                return new HashMap<>();
            }

            Map<String, List<String>> featuresByCategory = new HashMap<>();

            for (DependencyGroup group : metadata.getDependencies().getContent()) {
                if (group.getName() != null && group.getContent() != null) {
                    List<String> features = group.getContent().stream()
                            .map(Dependency::getId)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());

                    if (!features.isEmpty()) {
                        featuresByCategory.put(group.getName(), features);
                    }
                }
            }

            log.debug("Retrieved features for {} categories from TONIC service", featuresByCategory.size());
            return featuresByCategory;
        } catch (RestClientResponseException e) {
            log.error("Error while fetching TONIC features by category", e);
            throw new ServiceException(CommonProblemType.ERREUR_INATTENDUE, e);
        }
    }
}