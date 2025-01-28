package fr.cnam.ddst.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration de la documentation OpenAPI pour le service FACADE_J.
 * <p>
 * Cette classe configure la documentation API basée sur OpenAPI 3.0 pour le service.
 * Elle définit les métadonnées de base comme le titre, la description et la version
 * de l'API.
 *
 * @see io.swagger.v3.oas.models.OpenAPI
 */
@Configuration
public class OpenApiConfig {

    /**
     * Crée et configure l'objet OpenAPI pour la documentation de l'API.
     *
     * @return Un objet OpenAPI configuré avec les métadonnées du service
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("FAIN_J API")
                        .description("Service Facade pour Instanciateurs DDST")
                        .version("1.0.0"));
    }
}