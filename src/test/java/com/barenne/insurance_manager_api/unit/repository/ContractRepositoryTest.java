package com.barenne.insurance_manager_api.unit.repository;

import com.barenne.insurance_manager_api.model.Contract;
import com.barenne.insurance_manager_api.model.Person;
import com.barenne.insurance_manager_api.repository.ContractRepository;
import lombok.Data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
@ActiveProfiles("test")
public class ContractRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ContractRepository contractRepository;

    private Person client;
    private Contract activeContract1;
    private Contract activeContract2;
    private Contract inactiveContract;

    @BeforeEach
    public void setup() {
        // Create a client
        client = Person.builder()
                .name("John Doe")
                .phone("+33612345678")
                .email("john@example.com")
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();
        entityManager.persist(client);

        // Create contracts
        LocalDate today = LocalDate.now();
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);

        // Active contract 1 (no end date)
        activeContract1 = Contract.builder()
                .client(client)
                .startDate(today.minusMonths(2))
                .endDate(null) // Indefinite
                .costAmount(new BigDecimal("1000.00"))
                .createdAt(yesterday)
                .updatedAt(yesterday)
                .build();
        entityManager.persist(activeContract1);

        // Active contract 2 (future end date)
        activeContract2 = Contract.builder()
                .client(client)
                .startDate(today.minusMonths(1))
                .endDate(today.plusMonths(5))
                .costAmount(new BigDecimal("2000.00"))
                .createdAt(yesterday)
                .updatedAt(yesterday)
                .build();
        entityManager.persist(activeContract2);

        // Inactive contract (past end date)
        inactiveContract = Contract.builder()
                .client(client)
                .startDate(today.minusMonths(6))
                .endDate(today.minusMonths(1))
                .costAmount(new BigDecimal("3000.00"))
                .createdAt(yesterday)
                .updatedAt(yesterday)
                .build();
        entityManager.persist(inactiveContract);

        entityManager.flush();
    }

    @Test
    public void testFindByClientId() {
        // Find contracts by client id
        List<Contract> contracts = contractRepository.findByClientId(client.getId());

        // Assert results
        assertThat(contracts).hasSize(3);
        assertThat(contracts).extracting(Contract::getCostAmount)
                .contains(
                        new BigDecimal("1000.00"),
                        new BigDecimal("2000.00"),
                        new BigDecimal("3000.00")
                );
    }

    @Test
    public void testFindActiveContractsByClientId() {
        // Find active contracts
        List<Contract> activeContracts = contractRepository.findActiveContractsByClientId(
                client.getId(), LocalDate.now());

        // Assert results - should contain only the two active contracts
        assertThat(activeContracts).hasSize(2);
        assertThat(activeContracts).extracting(Contract::getId)
                .contains(activeContract1.getId(), activeContract2.getId())
                .doesNotContain(inactiveContract.getId());
    }

    @Test
    public void testFindActiveContractsByClientIdFilteredByUpdateDate() {
        // Create timestamps for different time periods
        LocalDateTime timestamp1 = LocalDateTime.of(2022, 1, 1, 12, 0); // January 2022
        LocalDateTime timestamp2 = LocalDateTime.of(2023, 1, 1, 12, 0); // January 2023

        // Set different update times for the contracts
        activeContract1.setUpdatedAt(timestamp2); // 2023 - more recent
        activeContract2.setUpdatedAt(timestamp1); // 2022 - older
        activeContract1.setSkipAutoUpdate(true);
        activeContract2.setSkipAutoUpdate(true);

        entityManager.merge(activeContract1);
        entityManager.merge(activeContract2);
        entityManager.flush();
        entityManager.clear();

        // Filter using timestamp from January 2023
        LocalDateTime filterDate = LocalDateTime.of(2023, 1, 1, 12, 0);

        // This should return only contracts with updated_at >= 2023-01-01
        List<Contract> filteredContracts = contractRepository.findActiveContractsByClientIdFilteredByUpdateDate(
                client.getId(), LocalDate.now(), filterDate);

        // Should only include activeContract1 which has timestamp from 2023
        assertThat(filteredContracts).hasSize(1);
        assertThat(filteredContracts.get(0).getId()).isEqualTo(activeContract1.getId());
    }

    @Test
    public void testSumCostAmountOfActiveContractsByClientId() {
        // Get sum of active contracts
        BigDecimal sum = contractRepository.sumCostAmountOfActiveContractsByClientId(
                client.getId(), LocalDate.now());

        // Expected: 1000 + 2000 = 3000
        assertThat(sum).isEqualByComparingTo(new BigDecimal("3000.00"));
    }

    @Test
    public void testUpdateAllContractsEndDateByClientId() {
        // New end date for all contracts
        LocalDate newEndDate = LocalDate.now().plusYears(1);

        // Update all contracts
        contractRepository.updateAllContractsEndDateByClientId(
                client.getId(), newEndDate, LocalDate.now());
        entityManager.flush();
        entityManager.clear();

        // Retrieve all contracts and check end dates
        List<Contract> updatedContracts = contractRepository.findByClientId(client.getId());

        // All active contracts should have the new end date
        for (Contract contract : updatedContracts) {
            if (contract.isActive()) {
                assertThat(contract.getEndDate()).isEqualTo(newEndDate);
            }
        }
    }
}
