package fr.cnam.initializr.facade.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration spécifique pour l'instanciateur TONIC.
 */
@Getter
@Setter
@ConfigurationProperties("initializer.tonic")
public class TonicConfig {

    /**
     * URL de l'instanciateur TONIC.
     */
    private String url;

    /**
     * Identifiant du groupe par défaut pour l'instanciateur TONIC.
     */
    private String groupId;
}