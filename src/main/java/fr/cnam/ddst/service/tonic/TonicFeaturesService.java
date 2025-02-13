package fr.cnam.ddst.service.tonic;

import fr.cnam.ddst.client.tonic.controller.rest.model.TonicDependenciesResponse;
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

    private static final List<String> CONTRACT_FEATURES = Arrays.asList(
            "toni-contract-openapi",
            "toni-contract-avro"
    );

    public TonicFeaturesService(TonicProjectGenerationService tonicService) {
        this.tonicService = tonicService;
    }

    /**
     * Récupère la liste des fonctionnalités (dépendances) disponibles dans TONIC.
     *
     * @param includeContractFeatures Si true, inclut les fonctionnalités de contrat dans la liste
     * @return Liste des identifiants de fonctionnalités disponibles
     * @throws ServiceException en cas d'erreur lors de la récupération des dépendances
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
     * Version surchargée qui garde la compatibilité avec l'ancien code.
     * Par défaut, n'inclut pas les fonctionnalités de contrat.
     */
    public List<String> getAvailableFeatures() {
        return getAvailableFeatures(false);
    }
}