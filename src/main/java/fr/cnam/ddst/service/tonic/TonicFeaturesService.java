package fr.cnam.ddst.service.tonic;

import fr.cnam.ddst.client.tonic.controller.rest.model.TonicDependenciesResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service responsable de la récupération et de la gestion des features TONIC.
 * Ce service interagit avec l'API TONIC pour obtenir la liste des dépendances disponibles.
 * Les dépendances sont reçues sous forme d'une map où les clés représentent les identifiants
 * des features disponibles.
 */
@Slf4j
@Service
public class TonicFeaturesService {

    private final TonicProjectGenerationService tonicService;

    public TonicFeaturesService(TonicProjectGenerationService tonicService) {
        this.tonicService = tonicService;
    }


    /**
     * Récupère la liste des features disponibles depuis l'API TONIC.
     * <p>
     * Cette méthode extrait les identifiants des features à partir de la réponse
     * de l'API TONIC qui fournit une map de dépendances. Les clés de cette map
     * constituent les identifiants des features disponibles.
     *
     * @return Liste des identifiants des dépendances disponibles
     */
    public List<String> getAvailableFeatures() {
        try {
            var response = tonicService.getDependencies(null);
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                log.error("Failed to get dependencies from TONIC service");
                return new ArrayList<>();
            }

            TonicDependenciesResponse dependencies = response.getBody();

            return new ArrayList<>(dependencies.getDependencies().keySet());

        } catch (Exception e) {
            log.error("Error while fetching TONIC features", e);
            return new ArrayList<>();
        }
    }
}