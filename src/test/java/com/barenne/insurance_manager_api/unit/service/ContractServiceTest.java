package com.barenne.insurance_manager_api.unit.service;

import com.barenne.insurance_manager_api.model.Contract;
import com.barenne.insurance_manager_api.model.Client;
import com.barenne.insurance_manager_api.model.Person;
import com.barenne.insurance_manager_api.repository.ClientRepository;
import com.barenne.insurance_manager_api.repository.ContractRepository;
import com.barenne.insurance_manager_api.service.ContractServiceImpl;
import com.barenne.insurance_manager_api.service.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ContractServiceTest {

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ContractServiceImpl contractService;

    private Client client;
    private Contract activeContract1;
    private Contract activeContract2;
    private Contract inactiveContract;
    private LocalDate currentDate;
    private LocalDateTime updateDate;

    @BeforeEach
    public void setup() {
        // Set current date
        currentDate = LocalDate.now();
        updateDate = LocalDateTime.now().minusDays(1);

        // Create client
        client = Person.builder()
                .id(1L)
                .name("John Doe")
                .phone("+33612345678")
                .email("john@example.com")
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        // Create contracts
        activeContract1 = Contract.builder()
                .id(1L)
                .client(client)
                .startDate(currentDate.minusMonths(2))
                .endDate(null) // No end date = active indefinitely
                .costAmount(new BigDecimal("1000.00"))
                .createdAt(updateDate)
                .updatedAt(updateDate)
                .build();

        activeContract2 = Contract.builder()
                .id(2L)
                .client(client)
                .startDate(currentDate.minusMonths(1))
                .endDate(currentDate.plusMonths(6)) // Future end date = active
                .costAmount(new BigDecimal("2000.00"))
                .createdAt(updateDate)
                .updatedAt(updateDate)
                .build();

        inactiveContract = Contract.builder()
                .id(3L)
                .client(client)
                .startDate(currentDate.minusMonths(6))
                .endDate(currentDate.minusMonths(1)) // Past end date = inactive
                .costAmount(new BigDecimal("3000.00"))
                .createdAt(updateDate)
                .updatedAt(updateDate)
                .build();
    }

    @Test
    public void testCreateContract_Success() {
        // Given
        Contract newContract = Contract.builder()
                .startDate(currentDate)
                .endDate(currentDate.plusYears(1))
                .costAmount(new BigDecimal("4000.00"))
                .build();

        Contract savedContract = Contract.builder()
                .id(4L)
                .client(client)
                .startDate(currentDate)
                .endDate(currentDate.plusYears(1))
                .costAmount(new BigDecimal("4000.00"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(contractRepository.save(any(Contract.class))).thenReturn(savedContract);

        // When
        Contract result = contractService.createContract(1L, newContract);

        // Then
        assertThat(result).isEqualTo(savedContract);
        verify(clientRepository).findById(1L);
        verify(contractRepository).save(any(Contract.class));
    }

    @Test
    public void testCreateContract_ClientNotFound() {
        // Given
        Contract newContract = Contract.builder()
                .startDate(currentDate)
                .endDate(currentDate.plusYears(1))
                .costAmount(new BigDecimal("4000.00"))
                .build();

        when(clientRepository.findById(999L)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(ResourceNotFoundException.class, () -> {
            contractService.createContract(999L, newContract);
        });
        verify(clientRepository).findById(999L);
        verify(contractRepository, never()).save(any(Contract.class));
    }

    @Test
    public void testCreateContract_WithNullStartDate() {
        // Given
        Contract newContract = Contract.builder()
                .startDate(null) // Null start date should be set to current date
                .endDate(currentDate.plusYears(1))
                .costAmount(new BigDecimal("4000.00"))
                .build();

        Contract savedContract = Contract.builder()
                .id(4L)
                .client(client)
                .startDate(currentDate) // Should be set to current date
                .endDate(currentDate.plusYears(1))
                .costAmount(new BigDecimal("4000.00"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(contractRepository.save(any(Contract.class))).thenReturn(savedContract);

        // When
        Contract result = contractService.createContract(1L, newContract);

        // Then
        assertThat(result.getStartDate()).isEqualTo(currentDate);
        verify(clientRepository).findById(1L);
        verify(contractRepository).save(any(Contract.class));
    }

    @Test
    public void testUpdateContractCostAmount_Success() {
        // Given
        BigDecimal newCostAmount = new BigDecimal("5000.00");

        Contract updatedContract = Contract.builder()
                .id(1L)
                .client(client)
                .startDate(activeContract1.getStartDate())
                .endDate(activeContract1.getEndDate())
                .costAmount(newCostAmount) // Updated cost
                .createdAt(activeContract1.getCreatedAt())
                .updatedAt(LocalDateTime.now()) // Updated timestamp
                .build();

        when(contractRepository.findById(1L)).thenReturn(Optional.of(activeContract1));
        when(contractRepository.save(any(Contract.class))).thenReturn(updatedContract);

        // When
        Contract result = contractService.updateContractCostAmount(1L, newCostAmount);

        // Then
        assertThat(result.getCostAmount()).isEqualTo(newCostAmount);
        verify(contractRepository).findById(1L);
        verify(contractRepository).save(any(Contract.class));
    }

    @Test
    public void testUpdateContractCostAmount_ContractNotFound() {
        // Given
        BigDecimal newCostAmount = new BigDecimal("5000.00");

        when(contractRepository.findById(999L)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(ResourceNotFoundException.class, () -> {
            contractService.updateContractCostAmount(999L, newCostAmount);
        });
        verify(contractRepository).findById(999L);
        verify(contractRepository, never()).save(any(Contract.class));
    }

    @Test
    public void testGetActiveContractsByClientId_Success() {
        // Given
        List<Contract> activeContracts = Arrays.asList(activeContract1, activeContract2);

        when(clientRepository.existsById(1L)).thenReturn(true);
        when(contractRepository.findActiveContractsByClientId(1L, currentDate)).thenReturn(activeContracts);

        // When
        List<Contract> result = contractService.getActiveContractsByClientId(1L);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(activeContract1, activeContract2);
        verify(clientRepository).existsById(1L);
        verify(contractRepository).findActiveContractsByClientId(1L, currentDate);
    }

    @Test
    public void testGetActiveContractsByClientId_ClientNotFound() {
        // Given
        when(clientRepository.existsById(999L)).thenReturn(false);

        // When/Then
        assertThrows(ResourceNotFoundException.class, () -> {
            contractService.getActiveContractsByClientId(999L);
        });
        verify(clientRepository).existsById(999L);
        verify(contractRepository, never()).findActiveContractsByClientId(anyLong(), any(LocalDate.class));
    }

    @Test
    public void testGetActiveContractsByClientIdByUpdateDate_Success() {
        // Given
        List<Contract> filteredContracts = Collections.singletonList(activeContract1);
        LocalDateTime filterDate = LocalDateTime.now().minusHours(1);

        when(clientRepository.existsById(1L)).thenReturn(true);
        when(contractRepository.findActiveContractsByClientIdFilteredByUpdateDate(1L, currentDate, filterDate))
                .thenReturn(filteredContracts);

        // When
        List<Contract> result = contractService.getActiveContractsByClientIdByUpdateDate(1L, filterDate);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(activeContract1);
        verify(clientRepository).existsById(1L);
        verify(contractRepository).findActiveContractsByClientIdFilteredByUpdateDate(1L, currentDate, filterDate);
    }

    @Test
    public void testGetActiveContractsByClientIdByUpdateDate_ClientNotFound() {
        // Given
        LocalDateTime filterDate = LocalDateTime.now().minusHours(1);
        when(clientRepository.existsById(999L)).thenReturn(false);

        // When/Then
        assertThrows(ResourceNotFoundException.class, () -> {
            contractService.getActiveContractsByClientIdByUpdateDate(999L, filterDate);
        });
        verify(clientRepository).existsById(999L);
        verify(contractRepository, never())
                .findActiveContractsByClientIdFilteredByUpdateDate(anyLong(), any(LocalDate.class), any(LocalDateTime.class));
    }

    @Test
    public void testGetTotalCostOfActiveContractsByClientId_Success() {
        // Given
        BigDecimal totalCost = new BigDecimal("3000.00"); // 1000 + 2000

        when(clientRepository.existsById(1L)).thenReturn(true);
        when(contractRepository.sumCostAmountOfActiveContractsByClientId(1L, currentDate)).thenReturn(totalCost);

        // When
        BigDecimal result = contractService.getTotalCostOfActiveContractsByClientId(1L);

        // Then
        assertThat(result).isEqualByComparingTo(totalCost);
        verify(clientRepository).existsById(1L);
        verify(contractRepository).sumCostAmountOfActiveContractsByClientId(1L, currentDate);
    }

    @Test
    public void testGetTotalCostOfActiveContractsByClientId_NoContracts() {
        // Given - No contracts, repository returns null
        when(clientRepository.existsById(1L)).thenReturn(true);
        when(contractRepository.sumCostAmountOfActiveContractsByClientId(1L, currentDate)).thenReturn(null);

        // When
        BigDecimal result = contractService.getTotalCostOfActiveContractsByClientId(1L);

        // Then
        assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
        verify(clientRepository).existsById(1L);
        verify(contractRepository).sumCostAmountOfActiveContractsByClientId(1L, currentDate);
    }

    @Test
    public void testGetTotalCostOfActiveContractsByClientId_ClientNotFound() {
        // Given
        when(clientRepository.existsById(999L)).thenReturn(false);

        // When/Then
        assertThrows(ResourceNotFoundException.class, () -> {
            contractService.getTotalCostOfActiveContractsByClientId(999L);
        });
        verify(clientRepository).existsById(999L);
        verify(contractRepository, never()).sumCostAmountOfActiveContractsByClientId(anyLong(), any(LocalDate.class));
    }

    @Test
    public void testTerminateAllClientContracts_Success() {
        // Given
        List<Contract> activeContracts = Arrays.asList(activeContract1, activeContract2);
        LocalDate terminationDate = currentDate;

        when(clientRepository.existsById(1L)).thenReturn(true);
        when(contractRepository.findActiveContractsByClientId(1L, currentDate)).thenReturn(activeContracts);
        when(contractRepository.save(any(Contract.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        contractService.terminateAllClientContracts(1L, terminationDate);

        // Then
        verify(clientRepository).existsById(1L);
        verify(contractRepository).findActiveContractsByClientId(1L, currentDate);
        verify(contractRepository, times(2)).save(any(Contract.class));
    }

    @Test
    public void testTerminateAllClientContracts_ClientNotFound() {
        // Given
        LocalDate terminationDate = currentDate;
        when(clientRepository.existsById(999L)).thenReturn(false);

        // When/Then
        assertThrows(ResourceNotFoundException.class, () -> {
            contractService.terminateAllClientContracts(999L, terminationDate);
        });
        verify(clientRepository).existsById(999L);
        verify(contractRepository, never()).findActiveContractsByClientId(anyLong(), any(LocalDate.class));
    }
}
