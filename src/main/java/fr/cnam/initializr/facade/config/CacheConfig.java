package fr.cnam.initializr.facade.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

/**
 * Configuration du système de cache pour l'application.
 * <p>
 * Cette classe configure le système de cache Spring utilisé pour stocker temporairement
 * des données fréquemment accédées, notamment les fonctionnalités disponibles de TONIC.
 * Elle utilise un cache en mémoire basé sur ConcurrentMap pour des performances optimales
 * dans un environnement concurrent.
 *
 * @see org.springframework.cache.annotation.EnableCaching
 * @see org.springframework.cache.CacheManager
 * @see org.springframework.cache.concurrent.ConcurrentMapCache
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Configure et fournit le gestionnaire de cache pour l'application.
     * <p>
     * Cette méthode crée un SimpleCacheManager qui gère un unique cache nommé "tonicFeatures".
     * Ce cache est utilisé pour stocker les fonctionnalités disponibles de TONIC afin d'éviter
     * des appels répétés au service distant.
     *
     * @return Le gestionnaire de cache configuré avec le cache "tonicFeatures"
     */
    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(Collections.singletonList(
                new ConcurrentMapCache("tonicFeatures")
        ));
        return cacheManager;
    }
}