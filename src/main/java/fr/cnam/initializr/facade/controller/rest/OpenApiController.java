package fr.cnam.initializr.facade.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.models.OpenAPI;
import jakarta.annotation.PostConstruct;
import org.springdoc.core.customizers.SpringDocCustomizers;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.providers.SpringDocProviders;
import org.springdoc.core.service.AbstractRequestService;
import org.springdoc.core.service.GenericResponseService;
import org.springdoc.core.service.OpenAPIService;
import org.springdoc.core.service.OperationService;
import org.springdoc.webmvc.api.OpenApiWebMvcResource;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Locale;

import static org.springdoc.core.utils.Constants.SPRINGDOC_ENABLED;

@RestController
@ConditionalOnProperty(name = SPRINGDOC_ENABLED, matchIfMissing = true)
public class OpenApiController extends OpenApiWebMvcResource {

    /**
     * Resource pointant vers le fichier contenant le contrat de l'API au format OpenApi / Yml
     */
    @Value("${openapi.contract.location}")
    private Resource openAPIResource;

    private OpenAPI openAPI;

    /**
     * Constructeur avec dépendances.
     */
    public OpenApiController( // NOSONAR - Nothing i can do about too much parameters
                              ObjectFactory<OpenAPIService> openAPIBuilderObjectFactory,
                              AbstractRequestService requestBuilder,
                              GenericResponseService responseBuilder,
                              OperationService operationParser,
                              SpringDocConfigProperties springDocConfigProperties,
                              SpringDocProviders springDocProviders,
                              SpringDocCustomizers springDocCustomizers) {
        super(openAPIBuilderObjectFactory,
                requestBuilder,
                responseBuilder,
                operationParser,
                springDocConfigProperties,
                springDocProviders,
                springDocCustomizers);
    }

    /**
     * Parsing du fichier yaml contenu dans {@code openAPIResource} pour construire l'objet {@code openAPI}.
     */
    @PostConstruct
    public void initOpenAPI() throws IOException {
        ObjectMapper yamlMapper = Yaml.mapper();
        openAPI = yamlMapper.readValue(openAPIResource.getInputStream(), OpenAPI.class);
    }

    @Override
    protected synchronized OpenAPI getOpenApi(Locale locale) {
        return openAPI;
    }
}