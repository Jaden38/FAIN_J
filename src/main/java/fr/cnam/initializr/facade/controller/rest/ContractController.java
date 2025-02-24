package fr.cnam.initializr.facade.controller.rest;

import fr.cnam.initializr.facade.business.model.ComponentArchive;
import fr.cnam.initializr.facade.business.model.ContractRequest;
import fr.cnam.initializr.facade.business.service.ContractBusinessService;
import fr.cnam.initializr.facade.controller.mapper.ContractMapper;
import fr.cnam.initializr.facade.controller.rest.api.ContractsApi;
import fr.cnam.initializr.facade.controller.rest.model.ContractType;
import fr.cnam.initializr.facade.controller.rest.model.StarterKitType;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ContractController implements ContractsApi {
    private final ContractBusinessService contractService;
    private final ContractMapper mapper;

    @Override
    public ResponseEntity<Resource> createContract(StarterKitType starterKit,
                                                   ContractType contractType,
                                                   String productName,
                                                   String codeApplicatif) {
        ContractRequest request = mapper.toBusiness(starterKit, contractType, productName, codeApplicatif);
        ComponentArchive archive = contractService.generateContract(request);

        String filename = String.format("%s-%s-contract.zip", productName, codeApplicatif);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/zip"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(archive.getContent());
    }

    @Override
    public ResponseEntity<List<ContractType>> getAvailableContracts(StarterKitType starterKit) {
        return ResponseEntity.ok(contractService.getAvailableContracts(starterKit));
    }
}