package fr.cnam.initializr.facade.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Configuration des propriétés pour les différents instanciateurs de projets.
 * <p>
 * Cette classe gère la configuration des URLs et des fonctionnalités disponibles pour chaque
 * type d'instanciateur (TONIC, HUMAN). Les propriétés sont chargées depuis le fichier
 * de configuration application.yml.
 *
 * @see org.springframework.boot.context.properties.ConfigurationProperties
 * @see org.springframework.context.annotation.Configuration
 */
@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "initializer")
public class InitializerConfig {

    /**
     * Configuration spécifique à l'instanciateur TONIC.
     */
    private Map<String, Object> tonic;

    /**
     * Récupère l'URL de l'instanciateur TONIC.
     *
     * @return L'URL de l'instanciateur TONIC
     */
    public String getTonicUrl() {
        return (String) tonic.get("url");
    }


}