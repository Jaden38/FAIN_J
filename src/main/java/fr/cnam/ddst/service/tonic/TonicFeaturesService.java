package fr.cnam.ddst.service.tonic;

import fr.cnam.ddst.client.tonic.controller.rest.model.TonicDependenciesResponse;
import fr.cnam.toni.starter.core.exceptions.CommonProblemType;
import fr.cnam.toni.starter.core.exceptions.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service de gestion des fonctionnalités disponibles dans TONIC.
 * <p>
 * Ce service est responsable de la récupération et de la gestion des fonctionnalités
 * (dépendances) disponibles dans le service TONIC. Il agit comme une couche d'abstraction
 * entre le service de génération de projets TONIC et le reste de l'application.
 */
@Slf4j
@Service
public class TonicFeaturesService {

    private final TonicProjectGenerationService tonicService;
    private static final List<String> CONTRACT_FEATURES = Arrays.asList(
            "toni-contract-openapi",
            "toni-contract-avro"
    );
    /**
     * Construit un nouveau service de gestion des fonctionnalités TONIC.
     *
     * @param tonicService Le service de génération de projets TONIC sous-jacent
     */
    public TonicFeaturesService(TonicProjectGenerationService tonicService) {
        this.tonicService = tonicService;
    }

    /**
     * Récupère la liste des fonctionnalités (dépendances) disponibles dans TONIC.
     * <p>
     * Cette méthode interroge le service TONIC pour obtenir toutes les dépendances
     * disponibles et les convertit en une liste de fonctionnalités utilisables.
     *
     * @return Liste des identifiants de fonctionnalités disponibles
     * @throws ServiceException avec CommonProblemType.ERREUR_INATTENDUE en cas d'erreur
     *         lors de la récupération des dépendances ou si la réponse est invalide
     */
    public List<String> getAvailableFeatures() {
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

            return dependencies.getDependencies().keySet().stream()
                    .filter(feature -> !CONTRACT_FEATURES.contains(feature))
                    .collect(Collectors.toCollection(ArrayList::new));

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
}