package com.barenne.insurance_manager_api.integration;

import com.barenne.insurance_manager_api.InsuranceManagerApiApplication;
import com.barenne.insurance_manager_api.dto.CompanyDto;
import com.barenne.insurance_manager_api.dto.ContractDto;
import com.barenne.insurance_manager_api.dto.PersonDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(classes = InsuranceManagerApiApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class BaseIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    /**
     * Creates a test person through the API
     */
    protected PersonDto createTestPerson(String name, String email, String phone, LocalDate birthDate) throws Exception {
        PersonDto personDto = new PersonDto();
        personDto.setName(name);
        personDto.setEmail(email);
        personDto.setPhone(phone);
        personDto.setBirthDate(birthDate);

        MvcResult result = mockMvc.perform(post("/api/clients/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personDto)))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readValue(result.getResponse().getContentAsString(), PersonDto.class);
    }

    /**
     * Creates a test company through the API
     */
    protected CompanyDto createTestCompany(String name, String email, String phone, String companyIdentifier) throws Exception {
        CompanyDto companyDto = new CompanyDto();
        companyDto.setName(name);
        companyDto.setEmail(email);
        companyDto.setPhone(phone);
        companyDto.setCompanyIdentifier(companyIdentifier);

        MvcResult result = mockMvc.perform(post("/api/clients/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(companyDto)))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readValue(result.getResponse().getContentAsString(), CompanyDto.class);
    }

    /**
     * Creates a test contract through the API
     */
    protected ContractDto createTestContract(Long clientId, LocalDate startDate, LocalDate endDate, BigDecimal costAmount) throws Exception {
        ContractDto contractDto = new ContractDto();
        contractDto.setStartDate(startDate);
        contractDto.setEndDate(endDate);
        contractDto.setCostAmount(costAmount);

        MvcResult result = mockMvc.perform(post("/api/contracts/clients/" + clientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contractDto)))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readValue(result.getResponse().getContentAsString(), ContractDto.class);
    }
}
