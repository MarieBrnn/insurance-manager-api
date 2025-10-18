package com.barenne.insurance_manager_api.integration;

import com.barenne.insurance_manager_api.model.Person;
import com.barenne.insurance_manager_api.model.Contract;
import com.barenne.insurance_manager_api.repository.ContractRepository;
import com.barenne.insurance_manager_api.repository.PersonRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
public class ContractRepositoryTest {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private EntityManager entityManager;

    private Person savedPerson;

    @BeforeEach
    void setup() {
        Person person = new Person();
        person.setName("TestName");
        person.setEmail("test@email.com");
        person.setPhone("+41123456789");
        person.setBirthDate(LocalDate.of(1995, 1, 1));
        savedPerson = personRepository.save(person);
    }

    @Test
    void testFindByClientId() {
        Contract contract = new Contract();
        contract.setClient(savedPerson);
        contract.setStartDate(LocalDate.now());
        contract.setEndDate(LocalDate.now().plusDays(10));
        contract.setCostAmount(new BigDecimal("100.00"));
        contract.setCreatedAt(LocalDateTime.now());
        contract.setUpdatedAt(LocalDateTime.now());

        contract.setSkipAutoUpdate(true);
        contractRepository.save(contract);
        entityManager.flush();
        entityManager.clear();

        List<Contract> contracts = contractRepository.findByClientId(savedPerson.getId());
        assertThat(contracts).hasSize(1);
        assertThat(contracts.get(0).getClient().getId()).isEqualTo(savedPerson.getId());
    }

    @Test
    public void testFindActiveContractsByClientId() {

        Contract activeContract1 = createContract(LocalDate.now().minusDays(10), LocalDate.now().plusDays(10), "100.00");
        Contract activeContract2 = createContract(LocalDate.now().minusDays(5), LocalDate.now().plusDays(20), "50.00");
        Contract expiredContract = createContract(LocalDate.now().minusDays(30), LocalDate.now().minusDays(5), "50.00");

        contractRepository.saveAll(List.of(activeContract1, activeContract2, expiredContract));

        //Ensure active contracts can be retrieved
        List<Contract> activeContracts = contractRepository.findActiveContractsByClientId(
                savedPerson.getId(), LocalDate.now()
        );

        assertThat(activeContracts).hasSize(2);
        assertThat(activeContracts.get(0).getId()).isEqualTo(activeContract1.getId());
        assertThat(activeContracts.get(1).getId()).isEqualTo(activeContract2.getId());

        //Test the sum of active contract costs
        BigDecimal totalCost = contractRepository.sumCostAmountOfActiveContractsByClientId(
                savedPerson.getId(), LocalDate.now()
        );

        assertThat(totalCost).isEqualByComparingTo(new BigDecimal("150.00"));

    }

    @Test
    public void testFindActiveContractsByClientIdFilteredByUpdateDate() {
        LocalDate fixedDate = LocalDate.of(2025, 10, 18);
        LocalDateTime now = LocalDateTime.of(2025, 10, 18, 12, 0);
        LocalDateTime oneHourAgo = now.minusHours(1);
        LocalDateTime twoHoursAgo = now.minusHours(2);

        Contract updatedContract = createContract(fixedDate.minusDays(10), fixedDate.plusDays(10), "80.00");
        updatedContract.setCreatedAt(now.minusDays(1));
        updatedContract.setSkipAutoUpdate(true);
        updatedContract.setUpdatedAt(oneHourAgo);
        contractRepository.save(updatedContract);
        entityManager.flush();
        entityManager.clear();

        Contract oldUpdatedContract = createContract(fixedDate.minusDays(5), fixedDate.plusDays(5), "60.00");
        oldUpdatedContract.setUpdatedAt(twoHoursAgo.minusMinutes(1));
        oldUpdatedContract.setSkipAutoUpdate(true);
        contractRepository.save(oldUpdatedContract);
        entityManager.flush();
        entityManager.clear();

        List<Contract> filtered = contractRepository.findActiveContractsByClientIdFilteredByUpdateDate(
                savedPerson.getId(), fixedDate, twoHoursAgo
        );

        assertThat(filtered).hasSize(1);
        assertThat(filtered.get(0).getCostAmount()).isEqualByComparingTo(new BigDecimal("80.00"));
    }

    @Test
    @Transactional
    void testUpdateAllContractsEndDateByClientId() {
        LocalDate fixedNow = LocalDate.of(2025, 10, 18);
        Contract c1 = createContract(fixedNow.minusDays(10), fixedNow.plusDays(10), "100.00");
        Contract c2 = createContract(fixedNow.minusDays(5), fixedNow.plusDays(5), "50.00");
        contractRepository.saveAll(List.of(c1, c2));
        entityManager.flush();
        entityManager.clear();

        LocalDate currentDate = fixedNow;
        LocalDate newEndDate = fixedNow.plusDays(30);
        contractRepository.updateAllContractsEndDateByClientId(savedPerson.getId(), newEndDate, fixedNow);
        entityManager.flush();
        entityManager.clear();


        List<Contract> updatedContracts = contractRepository.findByClientId(savedPerson.getId());

        assertThat(updatedContracts).allSatisfy(c ->
                assertThat(c.getEndDate()).isEqualTo(newEndDate)
        );
    }

    private Contract createContract(LocalDate start, LocalDate end, String cost) {
        Contract contract = new Contract();
        contract.setClient(savedPerson);
        contract.setStartDate(start);
        contract.setEndDate(end);
        contract.setCostAmount(new BigDecimal(cost));
        contract.setCreatedAt(LocalDateTime.now());
        contract.setUpdatedAt(LocalDateTime.now());
        contract.setSkipAutoUpdate(true);
        return contract;
    }

}
