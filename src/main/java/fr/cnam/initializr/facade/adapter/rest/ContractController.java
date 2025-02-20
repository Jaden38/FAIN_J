package fr.cnam.initializr.facade.adapter.rest;

import fr.cnam.initializr.facade.application.service.ContractService;
import fr.cnam.initializr.facade.controller.rest.api.ContractsApi;
import fr.cnam.initializr.facade.controller.rest.model.ContractType;
import fr.cnam.initializr.facade.controller.rest.model.StarterKitType;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ContractController implements ContractsApi {
    private final ContractService contractService;

    @Override
    public ResponseEntity<Resource> createContract(StarterKitType starterKit,
                                                   ContractType contractType,
                                                   String productName,
                                                   String codeApplicatif) {
        Resource resource = contractService.generateContract(starterKit, contractType, productName, codeApplicatif);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @Override
    public ResponseEntity<List<ContractType>> getAvailableContracts(StarterKitType starterKit) {
        return ResponseEntity.ok(contractService.getAvailableContracts(starterKit));
    }
}