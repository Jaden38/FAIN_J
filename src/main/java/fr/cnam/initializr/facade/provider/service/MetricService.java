package fr.cnam.initializr.facade.provider.service;

import fr.cnam.initializr.facade.business.model.ComponentRequest;
import fr.cnam.initializr.facade.business.model.ContractRequest;
import fr.cnam.initializr.facade.business.model.LibraryRequest;
import fr.cnam.initializr.facade.client.metric.controller.rest.api.ModuleApi;
import fr.cnam.initializr.facade.client.metric.controller.rest.model.ModuleResource;
import fr.cnam.initializr.facade.client.metric.controller.rest.model.ResponseOkAvecModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetricService {

    private final ModuleApi moduleApi;

    @Value("#{${initializer.starter-kit.versions}}")
    private Map<String, String> starterKitVersions;

    public void recordComponentGeneration(ComponentRequest request) {
        try {
            ModuleResource moduleResource = createModuleResource(request.getProductName(),
                    request.getCodeApplicatif(),
                    request.getType().getValue());
            moduleResource.setTypeModule(determineComponentType(request.getCodeApplicatif()));

            if (request.getFeatures() != null && !request.getFeatures().isEmpty()) {
                moduleResource.setUsecases(String.join(",", request.getFeatures()));
            }

            ResponseEntity<ResponseOkAvecModule> response = moduleApi.putModule(moduleResource);
            log.info("Recorded component generation metric for {} with status: {}",
                    request.getCodeApplicatif(), response.getStatusCode());
        } catch (Exception e) {
            log.warn("Failed to record metric for component generation: {}", e.getMessage());
        }
    }

    public void recordContractGeneration(ContractRequest request) {
        try {
            ModuleResource moduleResource = createModuleResource(request.getProductName(),
                    request.getCodeApplicatif(),
                    request.getType().name());
            moduleResource.setTypeModule("contract");
            moduleResource.setUsecases(request.getContractType().name().toLowerCase());

            ResponseEntity<ResponseOkAvecModule> response = moduleApi.putModule(moduleResource);
            log.info("Recorded contract generation metric for {} with status: {}",
                    request.getCodeApplicatif(), response.getStatusCode());
        } catch (Exception e) {
            log.warn("Failed to record metric for contract generation: {}", e.getMessage());
        }
    }

    public void recordLibraryGeneration(LibraryRequest request) {
        try {
            ModuleResource moduleResource = createModuleResource(request.getProductName(),
                    request.getCodeApplicatif(),
                    request.getType().name());
            moduleResource.setTypeModule("library");

            ResponseEntity<ResponseOkAvecModule> response = moduleApi.putModule(moduleResource);
            log.info("Recorded library generation metric for {} with status: {}",
                    request.getCodeApplicatif(), response.getStatusCode());
        } catch (Exception e) {
            log.warn("Failed to record metric for library generation: {}", e.getMessage());
        }
    }

    private ModuleResource createModuleResource(String productName, String codeModule, String starterKitType) {
        ModuleResource moduleResource = new ModuleResource();
        moduleResource.setDds(productName);
        moduleResource.setCodeModule(codeModule);
        moduleResource.setDateInstanciation(LocalDate.now());
        moduleResource.setTypeSK(starterKitType);
        moduleResource.setVersionSK(determineVersion(starterKitType));
        return moduleResource;
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
        return starterKitVersions.getOrDefault(starterKitType, "1.0.0");
    }
}