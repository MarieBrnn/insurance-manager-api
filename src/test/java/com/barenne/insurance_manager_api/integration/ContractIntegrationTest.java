package com.barenne.insurance_manager_api.integration;

import com.barenne.insurance_manager_api.dto.ContractDto;
import com.barenne.insurance_manager_api.dto.PersonDto;
import com.barenne.insurance_manager_api.dto.TotalCostDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ContractIntegrationTest extends BaseIntegrationTest {

    @Test
    public void createContractAndRetrieveIt() throws Exception {
        //Create test person
        PersonDto createdPerson = createTestPerson(
                "Jane Doe",
                "jane.doe@example.com",
                "+33687654321",
                LocalDate.of(1985, 5, 15)
        );

        //Extract ID from response
        Long personId = createdPerson.getId();

        // Create a contract for the person
        ContractDto createdContract = createTestContract(
                personId,
                LocalDate.now().minusDays(5),
                LocalDate.now().plusDays(30),
                new BigDecimal("1000.00")
        );

        //Extract ID from response
        Long contractId = createdContract.getId();

        // Retrieve active contracts for the person
        mockMvc.perform(get("/api/contracts/clients/" + personId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(contractId.intValue())))
                .andExpect(jsonPath("$[0].costAmount", is(1000.00)));
    }

    @Test
    public void updateContractCostAmount() throws Exception {
        // Create a test person
        PersonDto createdPerson = createTestPerson(
                "Cost Update Tester",
                "cost.update@example.com",
                "+33612345678",
                LocalDate.of(1990, 1, 1)
        );

        //Extract ID from response
        Long personId = createdPerson.getId();

        // Create a contract for the person
        ContractDto createdContract = createTestContract(
                personId,
                LocalDate.now().minusDays(5),
                LocalDate.now().plusDays(30),
                new BigDecimal("1000.00")
        );

        //Extract ID from response
        Long contractId = createdContract.getId();

        // Update the contract's cost amount
        mockMvc.perform(put("/api/contracts/" + contractId + "/cost")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("1500.00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.costAmount", is(1500.00)));

        // Verify the total cost is updated
        MvcResult totalCostResult = mockMvc.perform(get("/api/contracts/clients/" + personId + "/totalCost"))
                .andExpect(status().isOk())
                .andReturn();

        TotalCostDto totalCostDto = objectMapper.readValue(
                totalCostResult.getResponse().getContentAsString(), TotalCostDto.class);
        assertEquals(0, new BigDecimal("1500.00").compareTo(totalCostDto.getTotalCost()));
    }

    @Test
    public void testContractFilteringByUpdateDate() throws Exception {
        // Create test person
        PersonDto createdPerson = createTestPerson(
                "Filter Tester",
                "filter.test@example.com",
                "+33687654321",
                LocalDate.of(1985, 5, 15)
        );
        Long personId = createdPerson.getId();

        // Create first contract
        ContractDto contract1 = createTestContract(
                personId,
                LocalDate.now().minusDays(10),
                LocalDate.now().plusDays(20),
                new BigDecimal("1000.00")
        );

        // Test with a future date that no contract can satisfy
        LocalDateTime futureDateTime = LocalDateTime.now().plusYears(1);
        String formattedFutureDate = futureDateTime.format(DateTimeFormatter.ISO_DATE_TIME);

        mockMvc.perform(get("/api/contracts/clients/" + personId + "?updateDate=" + formattedFutureDate))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void testExpiredContractsAreNotIncluded() throws Exception {
        // Create a test person
        PersonDto createdPerson = createTestPerson(
                "Expired Contract Tester",
                "expired.test@example.com",
                "+33612345678",
                LocalDate.of(1990, 1, 1)
        );

        //Extract ID from response
        Long personId = createdPerson.getId();

        // Create an active contract
        ContractDto activeContract = createTestContract(
                personId,
                LocalDate.now().minusDays(10),
                LocalDate.now().plusDays(20),
                new BigDecimal("1000.00")
        );

        // Create an expired contract
        ContractDto expiredContract = createTestContract(
                personId,
                LocalDate.now().minusDays(50),
                LocalDate.now().minusDays(10),
                new BigDecimal("500.00")
        );

        // Check that only active contracts are returned
        mockMvc.perform(get("/api/contracts/clients/" + personId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(activeContract.getId().intValue())))
                .andExpect(jsonPath("$[0].costAmount", is(1000.00)));

        // Check that only active contracts are included in total cost
        MvcResult totalCostResult = mockMvc.perform(get("/api/contracts/clients/" + personId + "/totalCost"))
                .andExpect(status().isOk())
                .andReturn();

        TotalCostDto totalCostDto = objectMapper.readValue(
                totalCostResult.getResponse().getContentAsString(), TotalCostDto.class);
        assertEquals(0, new BigDecimal("1000.00").compareTo(totalCostDto.getTotalCost()));
    }
}
