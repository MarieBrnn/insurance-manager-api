package com.barenne.insurance_manager_api.controller;

import com.barenne.insurance_manager_api.dto.ContractDto;
import com.barenne.insurance_manager_api.dto.DtoConverter;
import com.barenne.insurance_manager_api.dto.TotalCostDto;
import com.barenne.insurance_manager_api.model.Contract;
import com.barenne.insurance_manager_api.service.ContractService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/api/contracts")
public class ContractController {

    private final ContractService contractService;

    @Autowired
    public ContractController(ContractService contractService) {
        this.contractService = contractService;
    }

    @GetMapping("/clients/{clientId}")
    public ResponseEntity<List<ContractDto>> getActiveContractsByClientId(
            @PathVariable Long clientId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime updateDate) {

        List<Contract> contracts;

        if (updateDate != null) {
            contracts = contractService.getActiveContractsByClientIdByUpdateDate(clientId, updateDate);
        } else {
            contracts = contractService.getActiveContractsByClientId(clientId);
        }
        return ResponseEntity.ok(DtoConverter.convertToDtoList(contracts));
    }

    @GetMapping("/clients/{clientId}/totalCost")
    public ResponseEntity<TotalCostDto> getTotalCostOfActiveContractsByClientId(@PathVariable Long clientId) {
        BigDecimal totalCost = contractService.getTotalCostOfActiveContractsByClientId(clientId);
        return ResponseEntity.ok(TotalCostDto.of(clientId, totalCost));
    }

    @PostMapping("/clients/{clientId}")
    public ResponseEntity<ContractDto> createContract(@PathVariable Long clientId, @Valid @RequestBody ContractDto contractDto) {
        Contract contract = DtoConverter.convertToEntity(contractDto);
        Contract createdContract = contractService.createContract(clientId, contract);
        return new ResponseEntity<>(DtoConverter.convertToDto(createdContract), HttpStatus.CREATED);
    }

    @PutMapping("/{contractId}/cost")
    public ResponseEntity<ContractDto> updateContractCostAmount(@PathVariable Long contractId, @RequestBody BigDecimal newCostAmount) {
        Contract updatedContract = contractService.updateContractCostAmount(contractId, newCostAmount);
        return ResponseEntity.ok(DtoConverter.convertToDto(updatedContract));
    }

}
