package fr.cnam.initializr.facade.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Configuration du système de cache pour l'application.
 * <p>
 * Cette classe configure le système de cache Spring utilisé pour stocker temporairement
 * des données fréquemment accédées, notamment les fonctionnalités disponibles de TONIC.
 * Elle utilise un cache en mémoire basé sur ConcurrentMap avec un TTL configurable.
 */
@Slf4j
@Configuration
@EnableCaching
@EnableScheduling
public class CacheConfig {

    /**
     * Configure et fournit le gestionnaire de cache pour l'application.
     * <p>
     * Cette méthode crée un ConcurrentMapCacheManager qui gère un unique cache nommé "tonicFeatures".
     * Ce cache est utilisé pour stocker les fonctionnalités disponibles de TONIC avec un TTL configurable.
     *
     * @return Le gestionnaire de cache configuré
     */
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("tonicFeatures");
    }

    /**
     * Vide le cache des fonctionnalités TONIC selon l'intervalle TTL configuré.
     * Cette méthode est appelée automatiquement pour rafraîchir le cache.
     */
    @CacheEvict(value = "tonicFeatures", allEntries = true)
    @Scheduled(fixedRateString = "${cache.tonic-features.ttl}")
    public void evictTonicFeaturesCache() {
        log.info("Emptying tonicFeatures cache");
    }
}