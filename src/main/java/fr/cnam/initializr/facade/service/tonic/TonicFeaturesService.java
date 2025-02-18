package fr.cnam.initializr.facade.service.tonic;

import fr.cnam.initializr.facade.client.tonic.controller.rest.model.TonicDependenciesResponse;
import fr.cnam.toni.starter.core.exceptions.CommonProblemType;
import fr.cnam.toni.starter.core.exceptions.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@EnableCaching
public class TonicFeaturesService {

    private final TonicProjectGenerationService tonicService;

    /**
     * Liste des fonctionnalités de contrat supportées.
     * Note: Ces fonctionnalités ne sont jamais incluses dans les résultats.
     */
    private static final List<String> CONTRACT_FEATURES = Arrays.asList(
            "toni-contract-openapi",
            "toni-contract-avro"
    );

    /**
     * Construit un nouveau service de gestion des fonctionnalités TONIC.
     *
     * @param tonicService Service de génération de projets TONIC
     */
    public TonicFeaturesService(TonicProjectGenerationService tonicService) {
        this.tonicService = tonicService;
    }

    /**
     * Récupère la liste des fonctionnalités disponibles dans TONIC, sans les fonctionnalités de contrat.
     * Les résultats sont mis en cache pour éviter des appels répétés au service.
     *
     * @return Liste des fonctionnalités disponibles
     * @throws ServiceException en cas d'erreur lors de la récupération des dépendances ou
     *         si la réponse du service est invalide
     */
    @Cacheable(value = "tonicFeatures")
    public List<String> getAvailableFeatures() {
        try {
            log.debug("Cache miss - fetching features from TONIC service");

            ResponseEntity<TonicDependenciesResponse> response = tonicService.getDependencies(null);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new ServiceException(
                        CommonProblemType.ERREUR_INATTENDUE
                );
            }

            TonicDependenciesResponse dependencies = response.getBody();

            if (dependencies.getDependencies() == null || dependencies.getDependencies().isEmpty()) {
                log.warn("No dependencies found in TONIC service response");
                return new ArrayList<>();
            }

            Set<String> features = new HashSet<>(dependencies.getDependencies().keySet());
            CONTRACT_FEATURES.forEach(features::remove);

            List<String> result = new ArrayList<>(features);
            log.debug("Retrieved {} features from TONIC service", result.size());
            return result;

        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error while fetching TONIC features", e);
            throw new ServiceException(
                    CommonProblemType.ERREUR_INATTENDUE
            );
        }
    }
}