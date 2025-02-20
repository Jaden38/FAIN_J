package fr.cnam.initializr.facade.provider.mapper;

import fr.cnam.client.tonic.controller.rest.model.ProjectRequest;
import fr.cnam.initializr.facade.business.model.ComponentArchive;
import fr.cnam.initializr.facade.business.model.ComponentRequest;
import fr.cnam.initializr.facade.business.model.ContractRequest;
import fr.cnam.initializr.facade.business.model.LibraryRequest;
import fr.cnam.initializr.facade.controller.rest.model.ContractType;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

@Component
public class TonicMapper {
    private static final Map<ContractType, String> CONTRACT_FEATURES;

    static {
        CONTRACT_FEATURES = new EnumMap<>(ContractType.class);
        CONTRACT_FEATURES.put(ContractType.OPENAPI, "toni-contract-openapi");
        CONTRACT_FEATURES.put(ContractType.AVRO, "toni-contract-avro");
    }

    public ProjectRequest toClientRequest(ComponentRequest business) {
        ProjectRequest request = new ProjectRequest();
        request.setDependencies(business.getFeatures());
        request.setGroupId("fr.cnam." + business.getProductName().toLowerCase());
        request.setArtifactId(business.getCodeApplicatif().toLowerCase());
        request.setName(business.getProductName());
        request.setVersion("1.0.0-SNAPSHOT");
        return request;
    }

    public ProjectRequest toClientContractRequest(ContractRequest business) {
        ProjectRequest request = new ProjectRequest();
        String contractFeature = CONTRACT_FEATURES.get(business.getContractType());
        request.setDependencies(Collections.singletonList(contractFeature));

        request.setGroupId("fr.cnam." + business.getProductName().toLowerCase());
        request.setArtifactId(business.getCodeApplicatif().toLowerCase());
        request.setName(business.getProductName());
        request.setVersion("1.0.0-SNAPSHOT");
        return request;
    }

    public ProjectRequest toLibraryContractRequest(LibraryRequest business) {
        // Not implemented
        return null;
    }

    public ComponentArchive toBusinessArchive(byte[] zipContent) {
        return new ComponentArchive(new ByteArrayResource(zipContent));
    }
}