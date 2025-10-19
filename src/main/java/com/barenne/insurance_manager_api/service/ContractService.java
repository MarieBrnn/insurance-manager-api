package com.barenne.insurance_manager_api.service;

import com.barenne.insurance_manager_api.model.Contract;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ContractService {
    Contract createContract(Long clientId, Contract contract);
    Contract updateContractCostAmount(Long contractId, BigDecimal newCostAmount);
    List<Contract> getActiveContractsByClientId(Long clientId);
    List<Contract> getActiveContractsByClientIdByUpdateDate(Long clientId, LocalDateTime updateDate);
    BigDecimal getTotalCostOfActiveContractsByClientId(Long clientId);
    void terminateAllClientContracts(Long clientId, LocalDate terminateDate);
}
