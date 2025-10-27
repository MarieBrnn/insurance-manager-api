package com.barenne.insurance_manager_api.unit.controller;

import com.barenne.insurance_manager_api.controller.ContractController;
import com.barenne.insurance_manager_api.dto.ContractDto;
import com.barenne.insurance_manager_api.dto.DtoConverter;
import com.barenne.insurance_manager_api.model.Client;
import com.barenne.insurance_manager_api.model.Contract;
import com.barenne.insurance_manager_api.model.Person;
import com.barenne.insurance_manager_api.service.ContractService;
import com.barenne.insurance_manager_api.service.exception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ContractControllerTest {
    @Mock
    private ContractService contractService;

    @InjectMocks
    private ContractController contractController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private Client client;
    private Contract activeContract1;
    private Contract activeContract2;
    private ContractDto contractDto;
    private LocalDateTime updateDate;
    private LocalDate currentDate;

    @BeforeEach
    public void setup() {

        // Initialize MockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(contractController)
                .build();

        // Initialize ObjectMapper
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // For proper serialization of Java 8 date/time types

        // Current date
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

        // Create DTO
        contractDto = new ContractDto();
        contractDto.setId(1L);
        contractDto.setClientId(1L);
        contractDto.setStartDate(currentDate.minusMonths(2));
        contractDto.setEndDate(null);
        contractDto.setCostAmount(new BigDecimal("1000.00"));
    }

    @Test
    public void testGetActiveContractsByClientId() throws Exception {
        // Given
        List<Contract> activeContracts = Arrays.asList(activeContract1, activeContract2);
        when(contractService.getActiveContractsByClientId(1L)).thenReturn(activeContracts);

        // When & Then
        mockMvc.perform(get("/api/contracts/clients/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].clientId").value(1))
                .andExpect(jsonPath("$[0].costAmount").value(1000.00))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].clientId").value(1));

        verify(contractService).getActiveContractsByClientId(1L);
    }

    @Test
    public void testGetActiveContractsByClientIdWithUpdateDate() throws Exception {
        // Given
        LocalDateTime filterDate = updateDate.minusHours(1);
        List<Contract> filteredContracts = Collections.singletonList(activeContract1);
        when(contractService.getActiveContractsByClientIdByUpdateDate(eq(1L), any(LocalDateTime.class)))
                .thenReturn(filteredContracts);

        // When & Then
        mockMvc.perform(get("/api/contracts/clients/1")
                        .param("updateDate", filterDate.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].clientId").value(1))
                .andExpect(jsonPath("$[0].costAmount").value(1000.00));

        verify(contractService).getActiveContractsByClientIdByUpdateDate(eq(1L), any(LocalDateTime.class));
    }

    @Test
    public void testGetTotalCostOfActiveContractsByClientId() throws Exception {
        // Given
        BigDecimal totalCost = new BigDecimal("3000.00");
        when(contractService.getTotalCostOfActiveContractsByClientId(1L)).thenReturn(totalCost);

        // When & Then
        mockMvc.perform(get("/api/contracts/clients/1/totalCost"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.clientId").value(1))
                .andExpect(jsonPath("$.totalCost").value(3000.00));

        verify(contractService).getTotalCostOfActiveContractsByClientId(1L);
    }

    @Test
    public void testGetActiveContractsByClientId_ClientNotFound() throws Exception {
        // Given
        when(contractService.getActiveContractsByClientId(999L))
                .thenThrow(new ResourceNotFoundException("Client", 999L));

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            contractController.getActiveContractsByClientId(999L, null);
        });

        verify(contractService).getActiveContractsByClientId(999L);
    }

    @Test
    public void testCreateContract() throws Exception {
        // Given
        ContractDto newContractDto = new ContractDto();
        newContractDto.setStartDate(currentDate);
        newContractDto.setEndDate(currentDate.plusYears(1));
        newContractDto.setCostAmount(new BigDecimal("4000.00"));

        Contract createdContract = Contract.builder()
                .id(3L)
                .client(client)
                .startDate(currentDate)
                .endDate(currentDate.plusYears(1))
                .costAmount(new BigDecimal("4000.00"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(contractService.createContract(eq(1L), any(Contract.class))).thenReturn(createdContract);

        // When & Then
        mockMvc.perform(post("/api/contracts/clients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newContractDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.clientId").value(1))
                .andExpect(jsonPath("$.costAmount").value(4000.00));

        verify(contractService).createContract(eq(1L), any(Contract.class));
    }

    @Test
    public void testCreateContract_ClientNotFound() throws Exception {
        // Given
        ContractDto newContractDto = new ContractDto();
        newContractDto.setStartDate(currentDate);
        newContractDto.setEndDate(currentDate.plusYears(1));
        newContractDto.setCostAmount(new BigDecimal("4000.00"));

        Contract newContract = DtoConverter.convertToEntity(newContractDto);

        when(contractService.createContract(eq(999L), any(Contract.class)))
                .thenThrow(new ResourceNotFoundException("Client", 999L));

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            contractController.createContract(999L, newContractDto);
        });

        verify(contractService).createContract(eq(999L), any(Contract.class));
    }

    @Test
    public void testUpdateContractCostAmount() throws Exception {
        // Given
        BigDecimal newCostAmount = new BigDecimal("5000.00");

        Contract updatedContract = Contract.builder()
                .id(1L)
                .client(client)
                .startDate(activeContract1.getStartDate())
                .endDate(activeContract1.getEndDate())
                .costAmount(newCostAmount)
                .createdAt(activeContract1.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        when(contractService.updateContractCostAmount(eq(1L), any(BigDecimal.class))).thenReturn(updatedContract);

        // When & Then
        mockMvc.perform(put("/api/contracts/1/cost")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCostAmount)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.costAmount").value(5000.00));

        verify(contractService).updateContractCostAmount(eq(1L), any(BigDecimal.class));
    }

    @Test
    public void testUpdateContractCostAmount_ContractNotFound() throws Exception {
        // Given
        BigDecimal newCostAmount = new BigDecimal("5000.00");

        when(contractService.updateContractCostAmount(eq(999L), any(BigDecimal.class)))
                .thenThrow(new ResourceNotFoundException("Contract", 999L));

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            contractController.updateContractCostAmount(999L, newCostAmount);
        });

        verify(contractService).updateContractCostAmount(eq(999L), any(BigDecimal.class));
    }
}
