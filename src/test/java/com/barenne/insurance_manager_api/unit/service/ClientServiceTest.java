package com.barenne.insurance_manager_api.unit.service;

import com.barenne.insurance_manager_api.model.Company;
import com.barenne.insurance_manager_api.model.Person;
import com.barenne.insurance_manager_api.model.Client;
import com.barenne.insurance_manager_api.repository.ClientRepository;
import com.barenne.insurance_manager_api.repository.CompanyRepository;
import com.barenne.insurance_manager_api.repository.PersonRepository;
import com.barenne.insurance_manager_api.service.ClientServiceImpl;
import com.barenne.insurance_manager_api.service.ContractService;
import com.barenne.insurance_manager_api.service.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientServiceTest {
    @Mock
    private ClientRepository clientRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private ContractService contractService;

    @InjectMocks
    private ClientServiceImpl clientService;

    private Person person;
    private Company company;
    private List<Client> clients;

    @BeforeEach
    public void setup() {
        // Create test data
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

        clients = Arrays.asList(person, company);
    }

    @Test
    public void testGetAllClients() {
        // Given
        when(clientRepository.findAll()).thenReturn(clients);

        // When
        List<Client> result = clientService.getAllClients();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(person, company);
        verify(clientRepository).findAll();
    }

    @Test
    public void testGetClientById_Found() {
        // Given
        when(clientRepository.findById(1L)).thenReturn(Optional.of(person));

        // When
        Client result = clientService.getClientById(1L);

        // Then
        assertThat(result).isEqualTo(person);
        verify(clientRepository).findById(1L);
    }

    @Test
    public void testGetClientById_NotFound() {
        // Given
        when(clientRepository.findById(999L)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(ResourceNotFoundException.class, () -> {
            clientService.getClientById(999L);
        });
        verify(clientRepository).findById(999L);
    }

    @Test
    public void testDeleteClient() {
        // Given
        when(clientRepository.findById(1L)).thenReturn(Optional.of(person));
        doNothing().when(contractService).terminateAllClientContracts(anyLong(), any(LocalDate.class));
        doNothing().when(clientRepository).deleteById(anyLong());

        // When
        clientService.deleteClient(1L);

        // Then
        verify(clientRepository).findById(1L);
        verify(contractService).terminateAllClientContracts(eq(1L), any(LocalDate.class));
        verify(clientRepository).deleteById(1L);
    }

    @Test
    public void testCreatePerson() {
        // Given
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

        when(personRepository.save(any(Person.class))).thenReturn(savedPerson);

        // When
        Person result = clientService.createPerson(newPerson);

        // Then
        assertThat(result).isEqualTo(savedPerson);
        assertThat(result.getId()).isEqualTo(3L);
        verify(personRepository).save(newPerson);
    }

    @Test
    public void testUpdatePerson_Success() {
        // Given
        Person personDetails = Person.builder()
                .name("John Updated")
                .phone("+33612345681")
                .email("john.updated@example.com")
                .build();

        Person updatedPerson = Person.builder()
                .id(1L)
                .name("John Updated")
                .phone("+33612345681")
                .email("john.updated@example.com")
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        when(personRepository.findById(1L)).thenReturn(Optional.of(person));
        when(personRepository.save(any(Person.class))).thenReturn(updatedPerson);

        // When
        Person result = clientService.updatePerson(1L, personDetails);

        // Then
        assertThat(result).isEqualTo(updatedPerson);
        verify(personRepository).findById(1L);
        verify(personRepository).save(any(Person.class));
    }

    @Test
    public void testUpdatePerson_NotFound() {
        // Given
        Person personDetails = Person.builder()
                .name("John Updated")
                .phone("+33612345681")
                .email("john.updated@example.com")
                .build();

        when(personRepository.findById(999L)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(ResourceNotFoundException.class, () -> {
            clientService.updatePerson(999L, personDetails);
        });
        verify(personRepository).findById(999L);
        verify(personRepository, never()).save(any(Person.class));
    }

    @Test
    public void testCreateCompany_Success() {
        // Given
        Company newCompany = Company.builder()
                .name("New Company")
                .phone("+33612345682")
                .email("contact@newcompany.com")
                .companyIdentifier("new-123")
                .build();

        Company savedCompany = Company.builder()
                .id(3L)
                .name("New Company")
                .phone("+33612345682")
                .email("contact@newcompany.com")
                .companyIdentifier("new-123")
                .build();

        when(companyRepository.existsByCompanyIdentifier("new-123")).thenReturn(false);
        when(companyRepository.save(any(Company.class))).thenReturn(savedCompany);

        // When
        Company result = clientService.createCompany(newCompany);

        // Then
        assertThat(result).isEqualTo(savedCompany);
        verify(companyRepository).existsByCompanyIdentifier("new-123");
        verify(companyRepository).save(newCompany);
    }

    @Test
    public void testCreateCompany_DuplicateIdentifier() {
        // Given
        Company newCompany = Company.builder()
                .name("Duplicate Company")
                .phone("+33612345683")
                .email("contact@duplicate.com")
                .companyIdentifier("acm-123") // Already exists
                .build();

        when(companyRepository.existsByCompanyIdentifier("acm-123")).thenReturn(true);

        // When/Then
        assertThrows(IllegalArgumentException.class, () -> {
            clientService.createCompany(newCompany);
        });
        verify(companyRepository).existsByCompanyIdentifier("acm-123");
        verify(companyRepository, never()).save(any(Company.class));
    }

    @Test
    public void testUpdateCompany_Success() {
        // Given
        Company companyDetails = Company.builder()
                .name("Acme Updated")
                .phone("+33612345684")
                .email("contact.updated@acme.com")
                .build();

        Company updatedCompany = Company.builder()
                .id(2L)
                .name("Acme Updated")
                .phone("+33612345684")
                .email("contact.updated@acme.com")
                .companyIdentifier("acm-123")
                .build();

        when(companyRepository.findById(2L)).thenReturn(Optional.of(company));
        when(companyRepository.save(any(Company.class))).thenReturn(updatedCompany);

        // When
        Company result = clientService.updateCompany(2L, companyDetails);

        // Then
        assertThat(result).isEqualTo(updatedCompany);
        verify(companyRepository).findById(2L);
        verify(companyRepository).save(any(Company.class));
    }

    @Test
    public void testUpdateCompany_NotFound() {
        // Given
        Company companyDetails = Company.builder()
                .name("Acme Updated")
                .phone("+33612345684")
                .email("contact.updated@acme.com")
                .build();

        when(companyRepository.findById(999L)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(ResourceNotFoundException.class, () -> {
            clientService.updateCompany(999L, companyDetails);
        });
        verify(companyRepository).findById(999L);
        verify(companyRepository, never()).save(any(Company.class));
    }
}
