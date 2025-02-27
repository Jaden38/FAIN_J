package fr.cnam.initializr.facade.business.service;

import fr.cnam.initializr.facade.controller.rest.model.StarterKitType;
import fr.cnam.toni.starter.core.exceptions.ClientException;
import fr.cnam.toni.starter.core.exceptions.CommonProblemType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ValidationService {

    public void validateStarterKitType(StarterKitType type) {
        if (type == null) {
            log.warn("Starter kit type cannot be null");
            throw new ClientException(
                    CommonProblemType.DONNEES_INVALIDES_MSG_AVEC_PROBLEMES,
                    "Starter kit type cannot be null"
            );
        }

        if (type != StarterKitType.TONIC) {
            log.warn("Invalid starter kit type: {}. Only TONIC is currently supported", type);
            throw new ClientException(
                    CommonProblemType.DONNEES_INVALIDES_MSG_AVEC_PROBLEMES,
                    "Invalid starter kit type: " + type + ". Only TONIC is currently supported"
            );
        }
    }

    public void validateProductName(String productName) {
        if (productName == null || productName.trim().isEmpty()) {
            log.warn("Product name cannot be empty");
            throw new ClientException(
                    CommonProblemType.DONNEES_INVALIDES_MSG_AVEC_PROBLEMES,
                    "Product name cannot be empty"
            );
        }
    }

    public void validateCodeApplicatif(String codeApplicatif) {
        if (codeApplicatif == null || codeApplicatif.trim().isEmpty()) {
            log.warn("Code applicatif cannot be empty");
            throw new ClientException(
                    CommonProblemType.DONNEES_INVALIDES_MSG_AVEC_PROBLEMES,
                    "Code applicatif cannot be empty"
            );
        }
    }
}