package com.barenne.insurance_manager_api.service;

import com.barenne.insurance_manager_api.model.Client;
import com.barenne.insurance_manager_api.model.Contract;
import com.barenne.insurance_manager_api.repository.ClientRepository;
import com.barenne.insurance_manager_api.repository.ContractRepository;
import com.barenne.insurance_manager_api.service.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;
    private final ClientRepository clientRepository;

    @Autowired
    public ContractServiceImpl (
            ContractRepository contractRepository,
            ClientRepository clientRepository
    ) {
        this.contractRepository = contractRepository;
        this.clientRepository = clientRepository;
    }

    @Override
    @Transactional
    public Contract createContract(Long clientId, Contract contract) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", clientId));

        contract.setClient(client);

        if(contract.getStartDate() == null) contract.setStartDate(LocalDate.now());

        return contractRepository.save(contract);
    }

    @Override
    @Transactional
    public Contract updateContractCostAmount(Long contractId, BigDecimal newCostAmount) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Contract", contractId));

        contract.setCostAmount(newCostAmount);
        //The updateDate is handled by the @PreUpdate hook in Contract entity

        return contractRepository.save(contract);
    }

    @Override
    public List<Contract> getActiveContractsByClientId(Long clientId) {
        if(!clientRepository.existsById(clientId)) {
            throw new ResourceNotFoundException("Client", clientId);
        }
        return contractRepository.findActiveContractsByClientId(clientId, LocalDate.now());
    }

    @Override
    public List<Contract> getActiveContractsByClientIdByUpdateDate(Long clientId, LocalDateTime updateDate) {
        if(!clientRepository.existsById(clientId)) {
            throw new ResourceNotFoundException("Client", clientId);
        }

        return contractRepository.findActiveContractsByClientIdFilteredByUpdateDate(clientId, LocalDate.now(), updateDate);
    }

    @Override
    public BigDecimal getTotalCostOfActiveContractsByClientId(Long clientId) {
        if(!clientRepository.existsById(clientId)) {
            throw new ResourceNotFoundException("Client", clientId);
        }

        BigDecimal totalCost = contractRepository.sumCostAmountOfActiveContractsByClientId(clientId, LocalDate.now());

        //If no active contract is found, return 0
        return totalCost != null ? totalCost : BigDecimal.ZERO;
    }

    @Override
    @Transactional
    public void terminateAllClientContracts(Long clientId, LocalDate terminationDate) {
        if(!clientRepository.existsById(clientId)) {
            throw new ResourceNotFoundException("Client", clientId);
        }

        List<Contract> activeContracts = contractRepository.findActiveContractsByClientId(clientId, LocalDate.now());

        //Update the EndDate of each contract
        for (Contract contract : activeContracts) {
            contract.setEndDate(terminationDate);
            contractRepository.save(contract);
        }
    }
}
