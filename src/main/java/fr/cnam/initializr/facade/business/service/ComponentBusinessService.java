package fr.cnam.initializr.facade.business.service;

import fr.cnam.initializr.facade.business.model.ComponentArchive;
import fr.cnam.initializr.facade.business.model.ComponentRequest;
import fr.cnam.initializr.facade.business.model.StarterKitBusiness;
import fr.cnam.initializr.facade.business.port.ComponentProvider;
import fr.cnam.initializr.facade.controller.rest.model.StarterKitType;
import fr.cnam.toni.starter.core.exceptions.ClientException;
import fr.cnam.toni.starter.core.exceptions.CommonProblemType;
import fr.cnam.toni.starter.core.exceptions.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ComponentBusinessService {
    private final ComponentProvider provider;

    public ComponentArchive generateComponent(ComponentRequest request) {
        validateRequest(request);
        try {
            return provider.generateComponent(request);
        } catch (Exception e) {
            throw new ServiceException(CommonProblemType.ERREUR_INATTENDUE, e);
        }
    }

    public List<StarterKitBusiness> getAvailableComponents() {
        try {
            return provider.getAvailableComponents();
        } catch (Exception e) {
            throw new ServiceException(CommonProblemType.ERREUR_INATTENDUE, e);
        }
    }

    public List<String> getComponentFeatures(StarterKitType type) {
        if (type == null) {
            throw new ClientException(
                    CommonProblemType.DONNEES_INVALIDES_MSG_AVEC_PROBLEMES,
                    "Starter kit type cannot be null"
            );
        }
        try {
            return provider.getComponentFeatures(type);
        } catch (Exception e) {
            throw new ServiceException(CommonProblemType.ERREUR_INATTENDUE, e);
        }
    }

    private void validateRequest(ComponentRequest request) {
        if (request.getType() == null) {
            throw new ClientException(
                    CommonProblemType.DONNEES_INVALIDES_MSG_AVEC_PROBLEMES,
                    "Component type cannot be null"
            );
        }
        if (request.getProductName() == null || request.getProductName().trim().isEmpty()) {
            throw new ClientException(
                    CommonProblemType.DONNEES_INVALIDES_MSG_AVEC_PROBLEMES,
                    "Product name cannot be empty"
            );
        }
        if (request.getCodeApplicatif() == null || request.getCodeApplicatif().trim().isEmpty()) {
            throw new ClientException(
                    CommonProblemType.DONNEES_INVALIDES_MSG_AVEC_PROBLEMES,
                    "Code applicatif cannot be empty"
            );
        }
    }
}