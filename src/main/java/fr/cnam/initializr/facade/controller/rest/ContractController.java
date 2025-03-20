package fr.cnam.initializr.facade.controller.rest;

import fr.cnam.initializr.facade.business.model.Instance;
import fr.cnam.initializr.facade.business.model.Contract;
import fr.cnam.initializr.facade.business.model.StarterKit;
import fr.cnam.initializr.facade.business.service.ContractService;
import fr.cnam.initializr.facade.controller.mapper.ContractMapper;
import fr.cnam.initializr.facade.controller.mapper.StarterKitMapper;
import fr.cnam.initializr.facade.controller.rest.api.ContractsApi;
import fr.cnam.initializr.facade.controller.rest.model.ContractType;
import fr.cnam.initializr.facade.controller.rest.model.StarterKitType;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ContractController implements ContractsApi {
    private final ContractService contractService;
    private final ContractMapper mapper;
    private final StarterKitMapper starterKitMapper;

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/test/{starter-kit}/{contract-type}",
            produces = { "application/octet-stream", "application/json" }
    )
    public ResponseEntity<Resource> createContract2(
            @Parameter(name = "starter-kit", description = "Type de starter kit", required = true, in = ParameterIn.PATH) @PathVariable("starter-kit") StarterKitType starterKit,
            @Parameter(name = "contract-type", description = "Type de contrat à générer", required = true, in = ParameterIn.PATH) @PathVariable("contract-type") ContractType contractType,
            @NotNull @Parameter(name = "product-name", description = "Nom du produit auquel le composant généré appartient", required = true, in = ParameterIn.QUERY) @Valid @RequestParam(value = "product-name", required = true) String productName,
            @NotNull @Parameter(name = "code-applicatif", description = "Code applicatif du composant à générer", required = true, in = ParameterIn.QUERY) @Valid @RequestParam(value = "code-applicatif", required = true) String codeApplicatif
    ) {
        Contract request = mapper.toBusiness(starterKit, contractType, productName, codeApplicatif);

        Instance archive = contractService.generateContract(request);

        String filename = String.format("%s-%s-contract.zip", productName, codeApplicatif);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/zip"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(archive.getContent());
    }

    @Override
    public ResponseEntity<Resource> createContract(StarterKitType starterKit,
                                                   ContractType contractType,
                                                   String productName,
                                                   String codeApplicatif) {
        Contract request = mapper.toBusiness(starterKit, contractType, productName, codeApplicatif);

        Instance archive = contractService.generateContract(request);

        String filename = String.format("%s-%s-contract.zip", productName, codeApplicatif);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/zip"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(archive.getContent());
    }

    @Override
    public ResponseEntity<List<ContractType>> getAvailableContracts(StarterKitType starterKitType) {
        StarterKit starterKit = starterKitMapper.toBusiness(starterKitType);

        List<String> availableContracts = contractService.getAvailableContracts(starterKit);

        List<ContractType> contractTypes = availableContracts.stream().map(ContractType::fromValue).toList();
        return ResponseEntity.ok(contractTypes);
    }
}