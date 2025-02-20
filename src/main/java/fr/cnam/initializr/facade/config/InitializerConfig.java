package fr.cnam.initializr.facade.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

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
@EnableConfigurationProperties(TonicConfig.class)
public class InitializerConfig {

    /**
     * Configuration spécifique à l'instanciateur TONIC.
     */
    private TonicConfig tonic;

    /**
     * Récupère l'URL de l'instanciateur TONIC.
     *
     * @return L'URL de l'instanciateur TONIC
     */
    public String getTonicUrl() {
        return tonic.getUrl();
    }

    /**
     * Récupère l'identifiant du groupe par défaut pour l'instanciateur TONIC.
     *
     * @return L'identifiant du groupe par défaut pour l'instanciateur TONIC
     */
    public String getTonicDefaultGroupId() {
        return tonic.getGroupId();
    }
}