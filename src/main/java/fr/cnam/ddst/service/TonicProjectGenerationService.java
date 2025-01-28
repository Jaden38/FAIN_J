package fr.cnam.ddst.service;

import fr.cnam.ddst.client.tonic.controller.rest.api.TonicProjectGenerationControllerApi;
import fr.cnam.ddst.config.InitializerProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;

/**
 * Service de génération de projets pour l'instanciateur TONIC.
 * <p>
 * Ce service gère la communication avec l'instanciateur TONIC pour générer des projets
 * avec les fonctionnalités et paramètres spécifiés. Il implémente l'interface TonicProjectGenerationControllerApi
 * générée à partir de la spécification OpenAPI.
 *
 * @see fr.cnam.ddst.client.tonic.controller.rest.api.TonicProjectGenerationControllerApi
 */
@Slf4j
@Service
public class TonicProjectGenerationService implements TonicProjectGenerationControllerApi {

    private final String tonicInitializrUrl;

    /**
     * Construit un nouveau service de génération de projets TONIC.
     *
     * @param properties Les propriétés de configuration contenant l'URL de l'instanciateur TONIC
     */
    public TonicProjectGenerationService(InitializerProperties properties) {
        this.tonicInitializrUrl = properties.getTonicUrl();
        log.info("Initialized TonicProjectGenerationService with URL: {}", tonicInitializrUrl);
    }

    /**
     * Retourne la requête web native associée à ce service.
     *
     * @return Un Optional contenant la requête web native si disponible
     */
    @Override
    public Optional<NativeWebRequest> getRequest() {
        return TonicProjectGenerationControllerApi.super.getRequest();
    }

    /**
     * Génère un projet Spring Boot avec les paramètres spécifiés.
     *
     * Cette méthode communique avec l'instanciateur TONIC pour créer un nouveau projet
     * avec les dépendances et configurations demandées.
     *
     * @param dependencies Liste des dépendances à inclure dans le projet
     * @param groupId ID du groupe Maven
     * @param artifactId ID de l'artefact Maven
     * @param name Nom du projet
     * @param type Type de projet
     * @param description Description du projet
     * @param version Version du projet
     * @param bootVersion Version de Spring Boot
     * @param packaging Type de packaging
     * @param applicationName Nom de l'application
     * @param language Langage de programmation
     * @param packageName Nom du package
     * @param javaVersion Version de Java
     * @param baseDir Répertoire de base
     * @return ResponseEntity contenant la ressource ZIP du projet généré
     */
    @Override
    public ResponseEntity<Resource> springZip(
            List<String> dependencies,
            String groupId,
            String artifactId,
            String name,
            String type,
            String description,
            String version,
            String bootVersion,
            String packaging,
            String applicationName,
            String language,
            String packageName,
            String javaVersion,
            String baseDir) {

        try {
            WebClient webClient = WebClient.builder()
                    .baseUrl(tonicInitializrUrl)
                    .build();

            Resource projectZip = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/starter.zip")
                            .queryParam("name", name)
                            .queryParam("groupId", groupId)
                            .queryParam("artifactId", artifactId)
                            .queryParam("dependencies", String.join(",", dependencies))
                            .build())
                    .retrieve()
                    .bodyToMono(Resource.class)
                    .doOnError(error -> log.error("Error response from Tonic initializer: {}", error.getMessage()))
                    .block();

            return ResponseEntity.ok(projectZip);
        } catch (Exception e) {
            log.error("Failed to generate project", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}