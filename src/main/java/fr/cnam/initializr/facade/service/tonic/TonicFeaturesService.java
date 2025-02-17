package fr.cnam.initializr.facade.service.tonic;

import fr.cnam.initializr.facade.client.tonic.controller.rest.model.TonicDependenciesResponse;
import fr.cnam.toni.starter.core.exceptions.CommonProblemType;
import fr.cnam.toni.starter.core.exceptions.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TonicFeaturesService {

    private final TonicProjectGenerationService tonicService;

    /**
     * Liste des fonctionnalités de contrat supportées.
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
     * Récupère la liste des fonctionnalités disponibles dans TONIC.
     *
     * @param includeContractFeatures Si true, inclut les fonctionnalités de contrat dans la liste
     * @return Liste des fonctionnalités disponibles
     * @throws ServiceException en cas d'erreur lors de la récupération des dépendances ou
     *         si la réponse du service est invalide
     */
    public List<String> getAvailableFeatures(boolean includeContractFeatures) {
        try {
            ResponseEntity<TonicDependenciesResponse> response = tonicService.getDependencies(null);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                Map<String, Object> details = new HashMap<>();
                details.put("statusCode", response.getStatusCode().value());

                throw new ServiceException(
                        CommonProblemType.ERREUR_INATTENDUE,
                        "Failed to get dependencies from TONIC service",
                        details
                );
            }

            TonicDependenciesResponse dependencies = response.getBody();

            if (dependencies.getDependencies() == null || dependencies.getDependencies().isEmpty()) {
                log.warn("No dependencies found in TONIC service response");
                return new ArrayList<>();
            }

            Set<String> features = new HashSet<>(dependencies.getDependencies().keySet());

            if (includeContractFeatures) {
                features.addAll(CONTRACT_FEATURES);
            } else {
                CONTRACT_FEATURES.forEach(features::remove);
            }

            return new ArrayList<>(features);

        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error while fetching TONIC features", e);
            throw new ServiceException(
                    CommonProblemType.ERREUR_INATTENDUE,
                    e,
                    "Unexpected error while fetching TONIC features"
            );
        }
    }

    /**
     * Récupère la liste des fonctionnalités disponibles, sans les fonctionnalités de contrat.
     *
     * @return Liste des fonctionnalités disponibles, hors contrats
     * @throws ServiceException en cas d'erreur lors de la récupération
     */
    public List<String> getAvailableFeatures() {
        return getAvailableFeatures(false);
    }
}