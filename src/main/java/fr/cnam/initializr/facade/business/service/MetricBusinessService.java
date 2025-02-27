package fr.cnam.initializr.facade.business.service;

import fr.cnam.initializr.facade.business.model.ComponentRequest;
import fr.cnam.initializr.facade.business.model.ContractRequest;
import fr.cnam.initializr.facade.business.model.LibraryRequest;
import fr.cnam.initializr.facade.business.model.ModuleMetric;
import fr.cnam.initializr.facade.business.port.MetricProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetricBusinessService {

    private final MetricProvider metricProvider;

    @Value("${initializer.starter-kit.versions.TONIC}")
    private String tonicVersion;

    @Value("${initializer.starter-kit.versions.STUMP}")
    private String stumpVersion;

    @Value("${initializer.starter-kit.versions.HUMAN:1.0.0}")
    private String humanVersion;

    @Value("${initializer.metric.dds-prefix:}")
    private String ddsPrefix;

    public void recordComponentGeneration(ComponentRequest request) {
        ModuleMetric moduleMetric = createModuleMetric(request.getProductName(),
                request.getCodeApplicatif(),
                request.getType().getValue());
        moduleMetric.setTypeModule(determineComponentType(request.getCodeApplicatif()));

        if (request.getFeatures() != null && !request.getFeatures().isEmpty()) {
            moduleMetric.setUsecases(String.join(",", request.getFeatures()));
        }

        recordMetricSafely(moduleMetric, "component", request.getCodeApplicatif());
    }

    public void recordContractGeneration(ContractRequest request) {
        ModuleMetric moduleMetric = createModuleMetric(request.getProductName(),
                request.getCodeApplicatif(),
                request.getType().name());
        moduleMetric.setTypeModule("contract");
        moduleMetric.setUsecases(request.getContractType().name().toLowerCase());

        recordMetricSafely(moduleMetric, "contract", request.getCodeApplicatif());
    }

    public void recordLibraryGeneration(LibraryRequest request) {
        ModuleMetric moduleMetric = createModuleMetric(request.getProductName(),
                request.getCodeApplicatif(),
                request.getType().name());
        moduleMetric.setTypeModule("library");

        recordMetricSafely(moduleMetric, "library", request.getCodeApplicatif());
    }

    private void recordMetricSafely(ModuleMetric moduleMetric, String resourceType, String codeApplicatif) {
        try {
            log.debug("Recording metric for {} generation: {}", resourceType, moduleMetric.toString());
            metricProvider.recordMetric(moduleMetric);
            log.info("Recorded {} generation metric for {}", resourceType, codeApplicatif);
        } catch (RuntimeException e) {
            // Échec non bloquant : l'enregistrement des métriques est optionnel et n'affecte pas le flux principal de l'application
            log.error("Failed to record metric for {} generation. Module: {}, Error: {}",
                    resourceType, moduleMetric, e.getMessage(), e);
        }
    }

    private ModuleMetric createModuleMetric(String productName, String codeModule, String starterKitType) {
        ModuleMetric moduleMetric = new ModuleMetric();
        moduleMetric.setDds(ddsPrefix + productName);
        moduleMetric.setCodeModule(codeModule);
        moduleMetric.setDateInstanciation(LocalDate.now());
        moduleMetric.setTypeSK(starterKitType);
        moduleMetric.setVersionSK(determineVersion(starterKitType));
        return moduleMetric;
    }

    private String determineComponentType(String codeApplicatif) {
        if (codeApplicatif == null || codeApplicatif.length() < 2) {
            return "other";
        }

        String suffix = codeApplicatif.substring(codeApplicatif.length() - 2).toUpperCase();

        return switch (suffix) {
            case "_J" -> "service";
            case "_B" -> "batch";
            case "_M" -> "web";
            case "_A" -> "doc";
            case "_Z" -> "resource";
            default -> "other";
        };
    }

    private String determineVersion(String starterKitType) {
        return switch (starterKitType) {
            case "TONIC" -> tonicVersion;
            case "STUMP" -> stumpVersion;
            case "HUMAN" -> humanVersion;
            default -> "1.0.0";
        };
    }
}