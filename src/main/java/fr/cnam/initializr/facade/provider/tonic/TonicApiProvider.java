package fr.cnam.initializr.facade.provider.tonic;

import fr.cnam.client.tonic.controller.rest.api.TonicProjectGenerationControllerApi;
import fr.cnam.client.tonic.controller.rest.model.ProjectRequest;
import fr.cnam.initializr.facade.business.model.*;
import fr.cnam.initializr.facade.business.port.TonicProvider;
import fr.cnam.initializr.facade.controller.rest.model.ContractType;
import fr.cnam.initializr.facade.provider.mapper.TonicMapper;
import fr.cnam.initializr.facade.provider.service.TonicFeaturesService;
import fr.cnam.toni.starter.core.exceptions.CommonProblemType;
import fr.cnam.toni.starter.core.exceptions.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.Arrays;
import java.util.List;

@org.springframework.stereotype.Component
@RequiredArgsConstructor
public class TonicApiProvider implements TonicProvider {

    private final TonicProjectGenerationControllerApi tonicApi;
    private final TonicMapper mapper;
    private final TonicFeaturesService featuresService;

    @Override
    public Instance generateComponent(Component component) {
        ProjectRequest projectRequest = mapper.toClientRequest(component);

        return getInstance(projectRequest);
    }

    private Instance getInstance(ProjectRequest projectRequest) {
        try {
            RestClient.ResponseSpec responseSpec = tonicApi.getProjectZipWithResponseSpec(
                    projectRequest.getDependencies(),
                    projectRequest.getGroupId(),
                    projectRequest.getArtifactId(),
                    projectRequest.getName(),
                    projectRequest.getType(),
                    projectRequest.getDescription(),
                    projectRequest.getVersion(),
                    projectRequest.getBootVersion(),
                    projectRequest.getPackaging(),
                    projectRequest.getApplicationName(),
                    projectRequest.getLanguage(),
                    projectRequest.getPackageName(),
                    projectRequest.getJavaVersion(),
                    projectRequest.getBaseDir()
            );

            byte[] content = responseSpec.body(byte[].class);

            return mapper.toBusinessArchive(content);

        } catch (RestClientResponseException e) {
            throw new ServiceException(CommonProblemType.ERREUR_INATTENDUE, e);
        }
    }

    @Override
    public List<String> getComponentFeatures() {
        return featuresService.getAvailableFeatures();
    }


    @Override
    public Instance generateContract(Contract contract) {
        ProjectRequest projectRequest = mapper.toClientContractRequest(contract);

        return getInstance(projectRequest);
    }

    @Override
    public List<String> getAvailableContracts() {
        return Arrays.asList(ContractType.OPENAPI.name(), ContractType.AVRO.name());
    }
}
