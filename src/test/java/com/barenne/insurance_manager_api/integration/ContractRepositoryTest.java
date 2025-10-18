package com.barenne.insurance_manager_api.integration;

import com.barenne.insurance_manager_api.model.Person;
import com.barenne.insurance_manager_api.model.Contract;
import com.barenne.insurance_manager_api.repository.ContractRepository;
import com.barenne.insurance_manager_api.repository.PersonRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@DataJpaTest
public class ContractRepositoryTest {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Test
    public void testFindActiveContractsByClientId() {

        //Create a person
        Person person = new Person();
        person.setName("TestName");
        person.setEmail("test@email.com");
        person.setPhone("+41123456789");
        person.setBirthDate(LocalDate.of(1995, 1, 1));
        Person savedPerson = personRepository.save(person);

        //Create active contracts
        Contract activeContract1 = new Contract();
        activeContract1.setClient(savedPerson);
        activeContract1.setStartDate(LocalDate.now().minusDays(10));
        activeContract1.setEndDate(LocalDate.now().plusDays(10));
        activeContract1.setCostAmount(new BigDecimal("100.00"));
        contractRepository.save(activeContract1);

        Contract activeContract2 = new Contract();
        activeContract2.setClient(savedPerson);
        activeContract2.setStartDate(LocalDate.now().minusDays(5));
        activeContract2.setEndDate(LocalDate.now().plusDays(20));
        activeContract2.setCostAmount(new BigDecimal("50.00"));
        contractRepository.save(activeContract2);

        //Create a expired contract
        Contract expiredContract = new Contract();
        expiredContract.setClient(savedPerson);
        expiredContract.setStartDate(LocalDate.now().minusDays(30));
        expiredContract.setEndDate(LocalDate.now().minusDays(5));
        expiredContract.setCostAmount(new BigDecimal("50.00"));
        contractRepository.save(expiredContract);

        //Ensure active contracts can be retrieved
        List<Contract> activeContracts = contractRepository.findActiveContractsByClientId(
                savedPerson.getId(), LocalDate.now()
        );

        assertThat(activeContracts).hasSize(2);
        assertThat(activeContracts.get(0).getId()).isEqualTo(activeContract1.getId());

        //Test the sum of active contract costs
        BigDecimal totalCost = contractRepository.sumCostAmountOfActiveContractsByClientId(
                savedPerson.getId(), LocalDate.now()
        );

        assertThat(totalCost).isEqualByComparingTo(new BigDecimal("150.00"));

    }

}
