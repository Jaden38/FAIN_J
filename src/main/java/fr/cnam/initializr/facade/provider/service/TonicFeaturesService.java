package fr.cnam.initializr.facade.provider.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@EnableCaching
@RequiredArgsConstructor
public class TonicFeaturesService {
    private static final String CONTRACTS_GROUP = "Contracts";

    private final TonicCachingService cachingService;

    public List<String> getAvailableFeatures() {
        Map<String, List<String>> featuresByCategory = cachingService.getAllFeaturesByCategory();

        List<String> features = new ArrayList<>();
        featuresByCategory.entrySet().stream()
                .filter(entry -> !CONTRACTS_GROUP.equals(entry.getKey()))
                .forEach(entry -> features.addAll(entry.getValue()));

        log.debug("Retrieved {} component features: {}", features.size(), features);
        return features;
    }

    public List<String> getAvailableContracts() {
        Map<String, List<String>> featuresByCategory = cachingService.getAllFeaturesByCategory();

        List<String> contracts = featuresByCategory.getOrDefault(CONTRACTS_GROUP, new ArrayList<>());

        List<String> formattedContracts = contracts.stream()
                .filter(id -> id.startsWith("toni-contract-"))
                .map(id -> id.substring("toni-contract-".length()).toUpperCase())
                .collect(Collectors.toList());

        log.debug("Retrieved {} contract types: {}", formattedContracts.size(), formattedContracts);
        return formattedContracts;
    }
}