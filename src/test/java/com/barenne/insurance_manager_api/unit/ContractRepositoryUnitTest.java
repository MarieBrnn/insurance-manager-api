package com.barenne.insurance_manager_api.unit;

import com.barenne.insurance_manager_api.model.Contract;
import com.barenne.insurance_manager_api.model.Person;
import com.barenne.insurance_manager_api.repository.ContractRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ContractRepositoryUnitTest {

    @Mock
    private ContractRepository contractRepository;

    public ContractRepositoryUnitTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindByClientId() {
        Long clientId = 1L;
        Person client = new Person();
        client.setId(clientId);

        Contract contract = new Contract();
        contract.setId(100L);
        contract.setClient(client);

        when(contractRepository.findByClientId(clientId)).thenReturn(List.of(contract));

        List<Contract> result = contractRepository.findByClientId(clientId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(100L);
        assertThat(result.get(0).getClient().getId()).isEqualTo(clientId);

        verify(contractRepository).findByClientId(clientId);
    }

    @Test
    void testFindActiveContractsByClientId() {
        Long clientId = 1L;
        LocalDate currentDate = LocalDate.now();
        Contract contract = new Contract();
        contract.setId(101L);

        when(contractRepository.findActiveContractsByClientId(clientId, currentDate))
                .thenReturn(List.of(contract));

        List<Contract> result = contractRepository.findActiveContractsByClientId(clientId, currentDate);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(101L);
        verify(contractRepository).findActiveContractsByClientId(clientId, currentDate);
    }

    @Test
    void  testFindActiveContractsByClientIdFilteredByUpdatedDate() {
        Long clientId = 1L;
        LocalDate currentDate = LocalDate.now();
        LocalDateTime updateDate = LocalDateTime.now().minusDays(1);
        Contract contract = new Contract();
        contract.setId(102L);

        when(contractRepository.findActiveContractsByClientIdFilteredByUpdateDate(clientId, currentDate, updateDate))
                .thenReturn(List.of(contract));

        List<Contract> result = contractRepository.findActiveContractsByClientIdFilteredByUpdateDate(clientId, currentDate, updateDate);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(102L);
        verify(contractRepository).findActiveContractsByClientIdFilteredByUpdateDate(clientId, currentDate, updateDate);
    }

    @Test
    void testSumCostAmountOfActiveContractsByClientId() {
        Long clientId = 1L;
        LocalDate currentDate = LocalDate.now();
        BigDecimal expectedSum = BigDecimal.valueOf(150.00);

        when(contractRepository.sumCostAmountOfActiveContractsByClientId(clientId, currentDate))
                .thenReturn(expectedSum);

        BigDecimal result = contractRepository.sumCostAmountOfActiveContractsByClientId(clientId, currentDate);
        assertThat(result).isEqualByComparingTo(expectedSum);
        verify(contractRepository).sumCostAmountOfActiveContractsByClientId(clientId, currentDate);
    }

    @Test
    void testUpdateAllContractsEndDateByClientId() {
        Long clientId = 1L;
        LocalDate currentDate = LocalDate.now();
        LocalDate newEndDate = LocalDate.now().plusDays(10);

        doNothing().when(contractRepository).updateAllContractsEndDateByClientId(clientId, newEndDate, currentDate);

        contractRepository.updateAllContractsEndDateByClientId(clientId, newEndDate, currentDate);

        verify(contractRepository).updateAllContractsEndDateByClientId(clientId, newEndDate, currentDate);
    }
}
