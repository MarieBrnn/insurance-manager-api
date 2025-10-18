package com.barenne.insurance_manager_api.repository;

import com.barenne.insurance_manager_api.model.Contract;
import com.barenne.insurance_manager_api.utils.Constants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {

    //Find all contracts of a client
    List<Contract> findByClientId(Long clientId);

    //Find all active contracts of a client
    @Query(Constants.FIND_ACTIVE_CONTRACTS_BY_CLIENT_ID)
    List<Contract> findActiveContractsByClientId(
            @Param("clientId") Long clientId, @Param("currentDate") LocalDate currentDate
    );

    //Find all active contracts of a client, filtered by updateDate
    @Query(Constants.FIND_ACTIVE_CONTRACTS_BY_CLIENT_ID_FILTERED_BY_UPDATEDATE)
    List<Contract> findActiveContractsByClientIdFilteredByUpdateDate(
            @Param("clientId") Long clientId,
            @Param("currentDate") LocalDate currentDate,
            @Param("updateDate")LocalDateTime updateDate
            );

    //Calculate the sum of the costs of active contracts of a client
    @Query(Constants.SUM_COST_AMOUNT_ACTIVE_CONTRACTS_BY_CLIENT_ID)
    BigDecimal sumCostAmountOfActiveContractsByClientId(
            @Param("clientId") Long clientId,
            @Param("currentDate") LocalDate currentDate
    );

    //Update endDate of all the contracts of a client
    @Query(Constants.UPDATE_ALL_CONTRACTS_ENDDATE_BY_CLIENT_ID)
    void updateAllContractsEndDateByClientId(
            @Param("clientId") Long clientId,
            @Param("endDate") LocalDate endDate
    );

}
