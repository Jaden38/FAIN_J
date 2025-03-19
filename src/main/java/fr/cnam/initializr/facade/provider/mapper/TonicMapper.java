package fr.cnam.initializr.facade.provider.mapper;

import fr.cnam.client.tonic.controller.rest.model.ProjectRequest;
import fr.cnam.initializr.facade.business.model.ComponentArchive;
import fr.cnam.initializr.facade.business.model.Component;
import fr.cnam.initializr.facade.business.model.Contract;
import fr.cnam.initializr.facade.business.model.Library;
import fr.cnam.initializr.facade.controller.rest.model.ContractType;
import org.springframework.core.io.ByteArrayResource;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

@org.springframework.stereotype.Component
public class TonicMapper {
    private static final Map<ContractType, String> CONTRACT_FEATURES;

    static {
        CONTRACT_FEATURES = new EnumMap<>(ContractType.class);
        CONTRACT_FEATURES.put(ContractType.OPENAPI, "toni-contract-openapi");
        CONTRACT_FEATURES.put(ContractType.AVRO, "toni-contract-avro");
    }

    public ProjectRequest toClientRequest(Component business) {
        ProjectRequest request = new ProjectRequest();
        request.setDependencies(business.getFeatures());
        request.setGroupId("fr.cnam." + business.getProductName().toLowerCase());
        request.setArtifactId(business.getCodeApplicatif().toLowerCase());
        request.setName(business.getProductName());
        request.setVersion("1.0.0-SNAPSHOT");
        return request;
    }

    public ProjectRequest toClientContractRequest(Contract business) {
        ProjectRequest request = new ProjectRequest();
        String contractFeature = CONTRACT_FEATURES.get(business.getContractType());
        request.setDependencies(Collections.singletonList(contractFeature));

        request.setGroupId("fr.cnam." + business.getProductName().toLowerCase());
        request.setArtifactId(business.getCodeApplicatif().toLowerCase());
        request.setName(business.getProductName());
        request.setVersion("1.0.0-SNAPSHOT");
        return request;
    }

    public ProjectRequest toLibraryContractRequest(Library business) {
        // Not implemented
        return null;
    }

    public ComponentArchive toBusinessArchive(byte[] zipContent) {
        return new ComponentArchive(new ByteArrayResource(zipContent));
    }
}