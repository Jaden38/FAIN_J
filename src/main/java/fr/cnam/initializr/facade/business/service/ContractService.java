package fr.cnam.initializr.facade.business.service;

import fr.cnam.initializr.facade.business.model.Contract;
import fr.cnam.initializr.facade.business.model.Instance;
import fr.cnam.initializr.facade.business.model.StarterKit;
import fr.cnam.initializr.facade.business.port.TonicProvider;
import fr.cnam.toni.starter.core.exceptions.ClientException;
import fr.cnam.toni.starter.core.exceptions.CommonProblemType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class ContractService {
    private final TonicProvider tonicProvider;
    private final MetricService metricService;
    public static final List<StarterKit> AVAILABLE_CONTRACT_STARTER_KITS = List.of(StarterKit.TONIC);

    public Instance generateContract(@Valid Contract contract) {
        validateContractStarterKit(contract.getStarterKit());
        validateContractType(contract.getStarterKit(), contract.getContractType());

        Instance instance = null;
        if (contract.getStarterKit() == StarterKit.TONIC) {
            instance = tonicProvider.generateContract(contract);
        }

        metricService.recordContractGeneration(contract);

        return instance;
    }

    public List<String> getAvailableContracts(StarterKit starterKit) {
        List<String> contractTypes = Collections.emptyList();

        if (starterKit == StarterKit.TONIC) {
            contractTypes = tonicProvider.getAvailableContracts();
        }

        return contractTypes;
    }

    private void validateContractType(StarterKit starterKit, String contractType) {
        List<String> availableContractTypes = Collections.emptyList();
        if (starterKit.equals(StarterKit.TONIC)) {
            availableContractTypes = tonicProvider.getAvailableContracts();
        }

        if (!availableContractTypes.contains(contractType)) {
            throw new ClientException(CommonProblemType.DONNEES_INVALIDES_MSG_AVEC_PROBLEMES,
                    "Invalid contract type: " + contractType + ". Available contract types are: " + availableContractTypes
            );
        }
    }

    public void validateContractStarterKit(StarterKit starterKit) {
        if (AVAILABLE_CONTRACT_STARTER_KITS.stream().noneMatch(availableStarterKit -> availableStarterKit.equals(starterKit))) {
            throw new ClientException(
                    CommonProblemType.DONNEES_INVALIDES_MSG_AVEC_PROBLEMES,
                    "Invalid starter kit starterKit: " + starterKit + ". Available contract types are: " + AVAILABLE_CONTRACT_STARTER_KITS
            );
        }
    }
}