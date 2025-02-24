package fr.cnam.initializr.facade.provider.tonic;

import fr.cnam.client.tonic.controller.rest.api.TonicProjectGenerationControllerApi;
import fr.cnam.client.tonic.controller.rest.model.ProjectRequest;
import fr.cnam.initializr.facade.business.model.ComponentArchive;
import fr.cnam.initializr.facade.business.model.ContractRequest;
import fr.cnam.initializr.facade.business.port.ContractProvider;
import fr.cnam.initializr.facade.controller.rest.model.ContractType;
import fr.cnam.initializr.facade.controller.rest.model.StarterKitType;
import fr.cnam.initializr.facade.provider.mapper.TonicMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TonicContractProvider implements ContractProvider {
    private final TonicProjectGenerationControllerApi tonicApi;
    private final TonicMapper mapper;

    @Override
    public ComponentArchive generateContract(ContractRequest request) {
        ProjectRequest clientRequest = mapper.toClientContractRequest(request);

        RestClient.ResponseSpec responseSpec = tonicApi.getProjectZipWithResponseSpec(
                clientRequest.getDependencies(),
                clientRequest.getGroupId(),
                clientRequest.getArtifactId(),
                clientRequest.getName(),
                clientRequest.getType(),
                clientRequest.getDescription(),
                clientRequest.getVersion(),
                clientRequest.getBootVersion(),
                clientRequest.getPackaging(),
                clientRequest.getApplicationName(),
                clientRequest.getLanguage(),
                clientRequest.getPackageName(),
                clientRequest.getJavaVersion(),
                clientRequest.getBaseDir()
        );

        try {
            byte[] content = responseSpec.body(byte[].class);
            return mapper.toBusinessArchive(content);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read contract ZIP content", e);
        }
    }

    @Override
    public List<ContractType> getAvailableContracts(StarterKitType type) {
        if (type == StarterKitType.TONIC) {
            return Arrays.asList(ContractType.OPENAPI, ContractType.AVRO);
        }
        return Collections.emptyList();
    }
}