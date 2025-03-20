package fr.cnam.initializr.facade.controller.mapper;

import fr.cnam.initializr.facade.business.model.Contract;
import fr.cnam.initializr.facade.controller.rest.model.ContractType;
import fr.cnam.initializr.facade.controller.rest.model.StarterKitType;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ContractMapper {
    StarterKitMapper starterKitMapper;

    public Contract toBusiness(StarterKitType starterKitType, ContractType contractType, String productName, String codeApplicatif) {
        return new Contract(
                starterKitMapper.toBusiness(starterKitType),
                contractType.name(),
                productName,
                codeApplicatif);
    }
}