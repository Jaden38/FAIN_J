package fr.cnam.initializr.facade.provider.service;

import fr.cnam.client.tonic.controller.rest.api.TonicProjectGenerationControllerApi;
import fr.cnam.toni.starter.core.exceptions.CommonProblemType;
import fr.cnam.toni.starter.core.exceptions.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@EnableCaching
@RequiredArgsConstructor
public class TonicFeaturesService {
    private static final List<String> CONTRACT_FEATURES = Arrays.asList(
            "toni-contract-openapi",
            "toni-contract-avro"
    );

    private final TonicProjectGenerationControllerApi tonicApi;

    @Cacheable(value = "tonicFeatures")
    public List<String> getAvailableFeatures() {
        try {
            log.debug("Cache miss - fetching features from TONIC service");
            var dependencies = tonicApi.getDependencies(null);

            if (dependencies.getDependencies() == null || dependencies.getDependencies().isEmpty()) {
                log.warn("No dependencies found in TONIC service response");
                return new ArrayList<>();
            }

            Set<String> features = new HashSet<>(dependencies.getDependencies().keySet());
            CONTRACT_FEATURES.forEach(features::remove);
            List<String> result = new ArrayList<>(features);
            log.debug("Retrieved {} features from TONIC service", result.size());
            return result;
        } catch (Exception e) {
            log.error("Error while fetching TONIC features", e);
            throw new ServiceException(CommonProblemType.ERREUR_INATTENDUE, e);
        }
    }
}