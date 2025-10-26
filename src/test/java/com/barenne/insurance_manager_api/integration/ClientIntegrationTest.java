package com.barenne.insurance_manager_api.integration;

import com.barenne.insurance_manager_api.dto.CompanyDto;
import com.barenne.insurance_manager_api.dto.ContractDto;
import com.barenne.insurance_manager_api.dto.PersonDto;
import com.barenne.insurance_manager_api.repository.ClientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ClientIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ClientRepository clientRepository;

    @Test
    public void createPersonAndGetById() throws Exception {

        PersonDto createdPerson = createTestPerson(
                "Jane Doe",
                "jane.doe@example.com",
                "+4178451236",
                LocalDate.of(1995,10,26)
        );

        //Extract ID from response
        Long personId = createdPerson.getId();

        //Get the Person by Id
        mockMvc.perform(get("/api/clients/" + personId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(personId.intValue())))
                .andExpect(jsonPath("$.name", is("Jane Doe")))
                .andExpect(jsonPath("$.email", is("jane.doe@example.com")))
                .andExpect(jsonPath("$.phone", is("+4178451236")))
                .andExpect(jsonPath("$.birthDate", is("1995-10-26")))
                .andExpect(jsonPath("$.clientType", is("PERSON")));

    }

    @Test
    public void updatePerson() throws Exception {
        // Create a test person
        PersonDto createdPerson = createTestPerson(
                "Person To Update",
                "before.update@example.com",
                "+33612345678",
                LocalDate.of(1990, 1, 1)
        );
        //Extract ID from response
        Long personId = createdPerson.getId();

        // Prepare update data
        PersonDto updatePersonDto = new PersonDto();
        updatePersonDto.setName("Updated Person");
        updatePersonDto.setEmail("after.update@example.com");
        updatePersonDto.setPhone("+33698765432");
        updatePersonDto.setBirthDate(LocalDate.of(1990, 1, 1)); // Should remain unchanged

        // Update the person
        mockMvc.perform(put("/api/clients/persons/" + personId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePersonDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(personId.intValue())))
                .andExpect(jsonPath("$.name", is("Updated Person")))
                .andExpect(jsonPath("$.email", is("after.update@example.com")))
                .andExpect(jsonPath("$.phone", is("+33698765432")))
                .andExpect(jsonPath("$.birthDate", is("1990-01-01"))); // Verify birthdate remains unchanged

        // Verify the update persisted by retrieving the person again
        mockMvc.perform(get("/api/clients/" + personId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Person")))
                .andExpect(jsonPath("$.email", is("after.update@example.com")))
                .andExpect(jsonPath("$.phone", is("+33698765432")))
                .andExpect(jsonPath("$.birthDate", is("1990-01-01")));
    }

    @Test
    public void createCompanyAndUpdateIt() throws Exception {
        CompanyDto createdCompany = createTestCompany(
                "ABCD Inc.",
                "contact@abcd.com",
                "+4112345678",
                "azc-123"
        );

        //Extract ID from response
        Long companyId = createdCompany.getId();

        //Update Company
        CompanyDto updatedCompanyDto = new CompanyDto();
        updatedCompanyDto.setName("ABCD Corporation");
        updatedCompanyDto.setEmail("new.contact@abcd.com");
        updatedCompanyDto.setPhone("+4123456789");
        updatedCompanyDto.setCompanyIdentifier("azc-123"); //should not change

        //Update the Company via API
        mockMvc.perform(put("/api/clients/companies/"+companyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedCompanyDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(companyId.intValue())))
                .andExpect(jsonPath("$.name", is("ABCD Corporation")))
                .andExpect(jsonPath("$.email", is("new.contact@abcd.com")))
                .andExpect(jsonPath("$.phone", is("+4123456789")))
                .andExpect(jsonPath("$.companyIdentifier", is("azc-123")));
    }

    @Test
    public void getAllClients() throws Exception {
        // Create a test person and company
        createTestPerson(
                "Jane Smith",
                "jane.smith@example.com",
                "+41687654321",
                LocalDate.of(1985, 5, 15)
        );

        createTestCompany(
                "Tech Solutions",
                "info@techsolutions.com",
                "+41123456780",
                "tec-456"
        );

        // Get all clients
        mockMvc.perform(get("/api/clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$[*].name", hasItems("Jane Smith", "Tech Solutions")));
    }

    @Test
    public void deleteClientAndCheckContractsTermination() throws Exception {
        // Create a test person
        PersonDto createdPerson = createTestPerson(
                "Client To Delete",
                "delete.me@example.com",
                "+33611223344",
                LocalDate.of(1980, 10, 10)
        );

        //Extract ID from response
        Long personId = createdPerson.getId();

        // Create active contract for the person
        ContractDto createdContract = createTestContract(
                personId,
                LocalDate.now().minusDays(10),
                LocalDate.now().plusDays(30),
                new BigDecimal("1000.00")
        );

        // Verify contract is active
        mockMvc.perform(get("/api/contracts/clients/" + personId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(createdContract.getId().intValue())));

        // Delete the client
        mockMvc.perform(delete("/api/clients/" + personId))
                .andExpect(status().isNoContent());

        //Verify if the client exists
        mockMvc.perform(get("/api/clients/" + personId))
                .andExpect(status().isNotFound());

        // Verify no active contracts remain
        mockMvc.perform(get("/api/contracts/clients/" + personId))
                .andExpect(status().isNotFound());
    }
}
