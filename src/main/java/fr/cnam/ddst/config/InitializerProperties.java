package fr.cnam.ddst.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

/**
 * Configuration des propriétés pour les différents instanciateurs de projets.
 * <p>
 * Cette classe gère la configuration des URLs et des fonctionnalités disponibles pour chaque
 * type d'instanciateur (TONIC, STUMP, HUMAN). Les propriétés sont chargées depuis le fichier
 * de configuration application.yml.
 *
 * @see org.springframework.boot.context.properties.ConfigurationProperties
 * @see org.springframework.context.annotation.Configuration
 */
@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "initializer")
public class InitializerProperties {

    /**
     * Configuration spécifique à l'instanciateur TONIC.
     */
    private Map<String, Object> tonic;

    /**
     * Configuration spécifique à l'instanciateur STUMP.
     */
    private Map<String, Object> stump;

    /**
     * Configuration spécifique à l'instanciateur HUMAN.
     */
    private Map<String, Object> human;

    /**
     * Mapping des fonctionnalités disponibles pour chaque type d'instanciateur.
     */
    private Map<String, List<String>> features;

    /**
     * Récupère l'URL de l'instanciateur TONIC.
     *
     * @return L'URL de l'instanciateur TONIC
     */
    public String getTonicUrl() {
        return (String) tonic.get("url");
    }

    /**
     * Récupère l'URL de l'instanciateur STUMP.
     *
     * @return L'URL de l'instanciateur STUMP
     */
    public String getStumpUrl() {
        return (String) stump.get("url");
    }

    /**
     * Récupère l'URL de l'instanciateur HUMAN.
     *
     * @return L'URL de l'instanciateur HUMAN
     */
    public String getHumanUrl() {
        return (String) human.get("url");
    }

    /**
     * Récupère la liste des fonctionnalités disponibles pour un type d'instanciateur donné.
     *
     * @param type Le type d'instanciateur (TONIC, STUMP, HUMAN)
     * @return La liste des fonctionnalités disponibles pour ce type
     */
    public List<String> getFeaturesForType(String type) {
        return features.get(type.toLowerCase());
    }
}