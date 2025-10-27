package com.barenne.insurance_manager_api.unit.controller;

import com.barenne.insurance_manager_api.controller.ClientController;
import com.barenne.insurance_manager_api.dto.CompanyDto;
import com.barenne.insurance_manager_api.dto.PersonDto;
import com.barenne.insurance_manager_api.model.Client;
import com.barenne.insurance_manager_api.model.Company;
import com.barenne.insurance_manager_api.model.Person;
import com.barenne.insurance_manager_api.service.ClientService;
import com.barenne.insurance_manager_api.service.exception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClientControllerTest.class)
public class ClientControllerTest {
    @Mock
    private ClientService clientService;

    @InjectMocks
    private ClientController clientController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private Person person;
    private Company company;

    @BeforeEach
    public void setup() {

        // Initialize ObjectMapper
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(objectMapper);

        // Initialize MockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(clientController)
                .setMessageConverters(converter)
                .build();

        // Create test models
        person = Person.builder()
                .id(1L)
                .name("John Doe")
                .phone("+33612345678")
                .email("john@example.com")
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        company = Company.builder()
                .id(2L)
                .name("Acme Inc")
                .phone("+33612345679")
                .email("contact@acme.com")
                .companyIdentifier("acm-123")
                .build();

        // Create test DTOs
        PersonDto personDto = new PersonDto();
        personDto.setId(1L);
        personDto.setName("John Doe");
        personDto.setPhone("+33612345678");
        personDto.setEmail("john@example.com");
        personDto.setBirthDate(LocalDate.of(1990, 1, 1));
        personDto.setClientType("PERSON");

        CompanyDto companyDto = new CompanyDto();
        companyDto.setId(2L);
        companyDto.setName("Acme Inc");
        companyDto.setPhone("+33612345679");
        companyDto.setEmail("contact@acme.com");
        companyDto.setCompanyIdentifier("acm-123");
        companyDto.setClientType("COMPANY");
    }

    @Test
    public void testGetAllClients() throws Exception {
        // Given
        List<Client> clients = Arrays.asList(person, company);
        when(clientService.getAllClients()).thenReturn(clients);

        // When & Then
        mockMvc.perform(get("/api/clients"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[0].clientType").value("PERSON"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Acme Inc"))
                .andExpect(jsonPath("$[1].clientType").value("COMPANY"));

        verify(clientService).getAllClients();
    }

    @Test
    public void testGetClientById_Person() throws Exception {
        // Given
        when(clientService.getClientById(1L)).thenReturn(person);

        // When & Then
        mockMvc.perform(get("/api/clients/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.clientType").value("PERSON"))
                .andExpect(jsonPath("$.birthDate").value("1990-01-01"));

        verify(clientService).getClientById(1L);
    }

    @Test
    public void testGetClientById_Company() throws Exception {
        // Given
        when(clientService.getClientById(2L)).thenReturn(company);

        // When & Then
        mockMvc.perform(get("/api/clients/2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("Acme Inc"))
                .andExpect(jsonPath("$.email").value("contact@acme.com"))
                .andExpect(jsonPath("$.clientType").value("COMPANY"))
                .andExpect(jsonPath("$.companyIdentifier").value("acm-123"));

        verify(clientService).getClientById(2L);
    }

    @Test
    public void testGetClientById_NotFound() {
        // Given
        when(clientService.getClientById(999L)).thenThrow(new ResourceNotFoundException("Client", 999L));

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> clientController.getClientById(999L));

        verify(clientService).getClientById(999L);
    }

    @Test
    public void testCreatePerson() throws Exception {
        // Given
        PersonDto newPersonDto = new PersonDto();
        newPersonDto.setName("Jane Doe");
        newPersonDto.setPhone("+33612345680");
        newPersonDto.setEmail("jane@example.com");
        newPersonDto.setBirthDate(LocalDate.of(1992, 2, 2));

        Person newPerson = Person.builder()
                .name("Jane Doe")
                .phone("+33612345680")
                .email("jane@example.com")
                .birthDate(LocalDate.of(1992, 2, 2))
                .build();

        Person savedPerson = Person.builder()
                .id(3L)
                .name("Jane Doe")
                .phone("+33612345680")
                .email("jane@example.com")
                .birthDate(LocalDate.of(1992, 2, 2))
                .build();

        when(clientService.createPerson(any(Person.class))).thenReturn(savedPerson);

        // When & Then
        mockMvc.perform(post("/api/clients/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPersonDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("Jane Doe"))
                .andExpect(jsonPath("$.email").value("jane@example.com"))
                .andExpect(jsonPath("$.clientType").value("PERSON"));

        verify(clientService).createPerson(any(Person.class));
    }

    @Test
    public void testUpdatePerson() throws Exception {
        // Given
        PersonDto updatedPersonDto = new PersonDto();
        updatedPersonDto.setName("John Updated");
        updatedPersonDto.setPhone("+33612345681");
        updatedPersonDto.setEmail("john.updated@example.com");
        updatedPersonDto.setBirthDate(LocalDate.of(1990, 1, 1));
        updatedPersonDto.setClientType("PERSON");

        Person updatedPerson = Person.builder()
                .id(1L)
                .name("John Updated")
                .phone("+33612345681")
                .email("john.updated@example.com")
                .birthDate(LocalDate.of(1990, 1, 1)) // Keeping original birthdate
                .build();

        when(clientService.updatePerson(eq(1L), any(Person.class))).thenReturn(updatedPerson);

        // When & Then
        mockMvc.perform(put("/api/clients/persons/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedPersonDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Updated"))
                .andExpect(jsonPath("$.email").value("john.updated@example.com"))
                .andExpect(jsonPath("$.clientType").value("PERSON"));

        verify(clientService).updatePerson(eq(1L), any(Person.class));
    }

    @Test
    public void testCreateCompany() throws Exception {
        // Given
        CompanyDto newCompanyDto = new CompanyDto();
        newCompanyDto.setName("New Company");
        newCompanyDto.setPhone("+33612345682");
        newCompanyDto.setEmail("contact@newcompany.com");
        newCompanyDto.setCompanyIdentifier("new-123");

        Company savedCompany = Company.builder()
                .id(3L)
                .name("New Company")
                .phone("+33612345682")
                .email("contact@newcompany.com")
                .companyIdentifier("new-123")
                .build();

        when(clientService.createCompany(any(Company.class))).thenReturn(savedCompany);

        // When & Then
        mockMvc.perform(post("/api/clients/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCompanyDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("New Company"))
                .andExpect(jsonPath("$.companyIdentifier").value("new-123"))
                .andExpect(jsonPath("$.clientType").value("COMPANY"));

        verify(clientService).createCompany(any(Company.class));
    }

    @Test
    public void testUpdateCompany() throws Exception {
        // Given
        CompanyDto updatedCompanyDto = new CompanyDto();
        updatedCompanyDto.setName("Acme Updated");
        updatedCompanyDto.setPhone("+33612345683");
        updatedCompanyDto.setEmail("contact.updated@acme.com");
        updatedCompanyDto.setCompanyIdentifier("acm-123");

        Company updatedCompany = Company.builder()
                .id(2L)
                .name("Acme Updated")
                .phone("+33612345683")
                .email("contact.updated@acme.com")
                .companyIdentifier("acm-123") // Keeping original identifier
                .build();

        when(clientService.updateCompany(eq(2L), any(Company.class))).thenReturn(updatedCompany);

        // When & Then
        mockMvc.perform(put("/api/clients/companies/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCompanyDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("Acme Updated"))
                .andExpect(jsonPath("$.email").value("contact.updated@acme.com"))
                .andExpect(jsonPath("$.clientType").value("COMPANY"));

        verify(clientService).updateCompany(eq(2L), any(Company.class));
    }

    @Test
    public void testDeleteClient() throws Exception {
        // Given
        doNothing().when(clientService).deleteClient(1L);

        // When & Then
        mockMvc.perform(delete("/api/clients/1"))
                .andExpect(status().isNoContent());

        verify(clientService).deleteClient(1L);
    }
}
