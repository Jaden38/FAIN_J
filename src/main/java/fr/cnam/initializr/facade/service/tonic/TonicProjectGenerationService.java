package fr.cnam.initializr.facade.service.tonic;

import fr.cnam.initializr.facade.client.tonic.controller.rest.api.TonicProjectGenerationControllerApi;
import fr.cnam.initializr.facade.client.tonic.controller.rest.model.TonicDependenciesResponse;
import fr.cnam.initializr.facade.config.InitializerConfig;
import fr.cnam.toni.starter.core.exceptions.CommonProblemType;
import fr.cnam.toni.starter.core.exceptions.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

/**
 * Service de génération de projets utilisant l'initialisateur TONIC.
 * <p>
 * Ce service fournit des fonctionnalités pour générer des projets Spring Boot
 * en utilisant le service d'initialisation TONIC. Il gère la communication
 * avec le service distant via WebClient et traite les réponses pour générer
 * des archives de projets et récupérer les dépendances disponibles.
 */
@Slf4j
@Service
public class TonicProjectGenerationService implements TonicProjectGenerationControllerApi {

    /**
     * Client web pour communiquer avec le service TONIC.
     */
    private final WebClient webClient;

    /**
     * Constante définissant le délai maximum d'attente pour la génération d'un projet.
     * Si la génération dépasse cette durée, une exception est levée.
     */
    private static final Duration TIMEOUT = Duration.ofSeconds(30);

    /**
     * Construit un nouveau service de génération de projets TONIC.
     *
     * @param properties Les propriétés de configuration contenant l'URL du service TONIC
     * @throws ServiceException si l'URL du service TONIC n'est pas configurée correctement
     */
    public TonicProjectGenerationService(InitializerConfig properties) {
        String tonicInitializrUrl = properties.getTonicUrl();
        this.webClient = WebClient.builder()
                .baseUrl(tonicInitializrUrl)
                .defaultRequest(req -> req.header("User-Agent", "TonicProjectGenerationService"))
                .filter(ExchangeFilterFunction.ofRequestProcessor(
                        clientRequest -> {
                            log.debug("Request: {} {}", clientRequest.method(), clientRequest.url());
                            return Mono.just(clientRequest);
                        }
                ))
                .build();
        log.info("Initialized TonicProjectGenerationService with URL: {}", tonicInitializrUrl);
    }

    /**
     * Récupère la requête web native si disponible.
     *
     * @return Optional contenant la requête web native
     */
    @Override
    public Optional<NativeWebRequest> getRequest() {
        return TonicProjectGenerationControllerApi.super.getRequest();
    }

    /**
     * Génère une archive ZIP contenant un projet avec les paramètres spécifiés.
     *
     * @param dependencies Liste des dépendances à inclure dans le projet
     * @param groupId ID du groupe Maven
     * @param artifactId ID de l'artefact Maven
     * @param name Nom du projet
     * @param type Type du projet
     * @param description Description du projet
     * @param version Version du projet
     * @param bootVersion Version de Spring Boot
     * @param packaging Type de packaging (jar, war)
     * @param applicationName Nom de l'application
     * @param language Langage de programmation
     * @param packageName Nom du package
     * @param javaVersion Version de Java
     * @param baseDir Répertoire de base
     * @return ResponseEntity contenant la ressource ZIP du projet généré
     * @throws ServiceException avec CommonProblemType.ERREUR_INATTENDUE en cas d'erreur de génération
     */
    @Override
    public ResponseEntity<Resource> getProjectZip(
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
                    .timeout(TIMEOUT)
                    .onErrorMap(TimeoutException.class, ex ->
                            new ServiceException(
                                    CommonProblemType.ERREUR_INATTENDUE,
                                    ex,
                                    "Timeout while generating project"
                            )
                    )
                    .block();

            if (projectZip == null) {
                throw new ServiceException(
                        CommonProblemType.ERREUR_INATTENDUE,
                        "Failed to generate project: Empty response from Tonic initializer"
                );
            }

            return ResponseEntity.ok(projectZip);

        } catch (WebClientResponseException e) {
            log.error("Error response from Tonic initializer: {}", e.getMessage());
            Map<String, Object> details = new HashMap<>();
            details.put("status", e.getStatusCode().value());
            details.put("response", e.getResponseBodyAsString());

            throw new ServiceException(
                    CommonProblemType.ERREUR_INATTENDUE,
                    e,
                    "Failed to generate project from Tonic initializer",
                    details
            );
        } catch (Exception e) {
            log.error("Failed to generate project", e);
            throw new ServiceException(
                    CommonProblemType.ERREUR_INATTENDUE,
                    e,
                    "Unexpected error while generating project"
            );
        }
    }

    /**
     * Récupère la liste des dépendances disponibles pour une version de Spring Boot donnée.
     *
     * @param bootVersion Version de Spring Boot (optionnel)
     * @return ResponseEntity contenant la réponse des dépendances TONIC
     * @throws ServiceException avec CommonProblemType.ERREUR_INATTENDUE en cas d'erreur lors de la récupération
     */
    @Override
    public ResponseEntity<TonicDependenciesResponse> getDependencies(String bootVersion) {
        try {
            TonicDependenciesResponse dependencies = webClient.get()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/dependencies");
                        if (bootVersion != null) {
                            builder.queryParam("bootVersion", bootVersion);
                        }
                        return builder.build();
                    })
                    .retrieve()
                    .bodyToMono(TonicDependenciesResponse.class)
                    .block();

            if (dependencies == null) {
                throw new ServiceException(
                        CommonProblemType.ERREUR_INATTENDUE,
                        "Failed to fetch dependencies: Empty response from Tonic initializer"
                );
            }

            return ResponseEntity.ok(dependencies);

        } catch (WebClientResponseException e) {
            log.error("Error fetching dependencies from Tonic initializer: {}", e.getMessage());
            Map<String, Object> details = new HashMap<>();
            details.put("status", e.getStatusCode().value());
            details.put("response", e.getResponseBodyAsString());

            throw new ServiceException(
                    CommonProblemType.ERREUR_INATTENDUE,
                    e,
                    "Failed to fetch dependencies from Tonic initializer",
                    details
            );
        } catch (Exception e) {
            log.error("Failed to fetch dependencies", e);
            throw new ServiceException(
                    CommonProblemType.ERREUR_INATTENDUE,
                    e,
                    "Unexpected error while fetching dependencies"
            );
        }
    }
}