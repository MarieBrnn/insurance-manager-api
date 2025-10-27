package com.barenne.insurance_manager_api.unit.dto;

import com.barenne.insurance_manager_api.dto.CompanyDto;
import com.barenne.insurance_manager_api.dto.ContractDto;
import com.barenne.insurance_manager_api.dto.DtoConverter;
import com.barenne.insurance_manager_api.dto.PersonDto;
import com.barenne.insurance_manager_api.model.Client;
import com.barenne.insurance_manager_api.model.Company;
import com.barenne.insurance_manager_api.model.Contract;
import com.barenne.insurance_manager_api.model.Person;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DtoConverterTest {
    @Test
    public void testConvertPersonToDto() {
        // Create a person
        Person person = Person.builder()
                .id(1L)
                .name("John Doe")
                .phone("+33612345678")
                .email("john@example.com")
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        // Convert to DTO
        PersonDto personDto = DtoConverter.convertToDto(person);

        // Assert correct mapping
        assertThat(personDto.getId()).isEqualTo(person.getId());
        assertThat(personDto.getName()).isEqualTo(person.getName());
        assertThat(personDto.getPhone()).isEqualTo(person.getPhone());
        assertThat(personDto.getEmail()).isEqualTo(person.getEmail());
        assertThat(personDto.getBirthDate()).isEqualTo(person.getBirthDate());
        assertThat(personDto.getClientType()).isEqualTo("PERSON");
    }

    @Test
    public void testConvertCompanyToDto() {
        // Create a company
        Company company = Company.builder()
                .id(2L)
                .name("Acme Inc")
                .phone("+33612345679")
                .email("contact@acme.com")
                .companyIdentifier("acm-123")
                .build();

        // Convert to DTO
        CompanyDto companyDto = DtoConverter.convertToDto(company);

        // Assert correct mapping
        assertThat(companyDto.getId()).isEqualTo(company.getId());
        assertThat(companyDto.getName()).isEqualTo(company.getName());
        assertThat(companyDto.getPhone()).isEqualTo(company.getPhone());
        assertThat(companyDto.getEmail()).isEqualTo(company.getEmail());
        assertThat(companyDto.getCompanyIdentifier()).isEqualTo(company.getCompanyIdentifier());
        assertThat(companyDto.getClientType()).isEqualTo("COMPANY");
    }

    @Test
    public void testConvertContractToDto() {
        // Create a client
        Client client = Person.builder()
                .id(1L)
                .name("John Doe")
                .phone("+33612345678")
                .email("john@example.com")
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        // Create a contract
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusYears(1);
        BigDecimal costAmount = new BigDecimal("1000.00");
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        Contract contract = Contract.builder()
                .id(1L)
                .client(client)
                .startDate(startDate)
                .endDate(endDate)
                .costAmount(costAmount)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        // Convert to DTO
        ContractDto contractDto = DtoConverter.convertToDto(contract);

        // Assert correct mapping
        assertThat(contractDto.getId()).isEqualTo(contract.getId());
        assertThat(contractDto.getClientId()).isEqualTo(client.getId());
        assertThat(contractDto.getStartDate()).isEqualTo(contract.getStartDate());
        assertThat(contractDto.getEndDate()).isEqualTo(contract.getEndDate());
        assertThat(contractDto.getCostAmount()).isEqualByComparingTo(contract.getCostAmount());
        assertThat(contractDto.getCreatedAt()).isEqualTo(contract.getCreatedAt());
    }

    @Test
    public void testConvertContractListToDtoList() {
        // Create a client
        Client client = Person.builder()
                .id(1L)
                .name("John Doe")
                .phone("+33612345678")
                .email("john@example.com")
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        // Create contracts
        Contract contract1 = Contract.builder()
                .id(1L)
                .client(client)
                .startDate(LocalDate.now().minusMonths(1))
                .endDate(LocalDate.now().plusMonths(11))
                .costAmount(new BigDecimal("1000.00"))
                .createdAt(LocalDateTime.now().minusDays(30))
                .updatedAt(LocalDateTime.now().minusDays(30))
                .build();

        Contract contract2 = Contract.builder()
                .id(2L)
                .client(client)
                .startDate(LocalDate.now().minusMonths(2))
                .endDate(LocalDate.now().plusMonths(10))
                .costAmount(new BigDecimal("2000.00"))
                .createdAt(LocalDateTime.now().minusDays(60))
                .updatedAt(LocalDateTime.now().minusDays(60))
                .build();

        List<Contract> contracts = Arrays.asList(contract1, contract2);

        // Convert to DTO list
        List<ContractDto> contractDtos = DtoConverter.convertToDtoList(contracts);

        // Assert correct mapping
        assertThat(contractDtos).hasSize(2);
        assertThat(contractDtos.get(0).getId()).isEqualTo(contract1.getId());
        assertThat(contractDtos.get(0).getClientId()).isEqualTo(client.getId());
        assertThat(contractDtos.get(0).getCostAmount()).isEqualByComparingTo(contract1.getCostAmount());
        assertThat(contractDtos.get(1).getId()).isEqualTo(contract2.getId());
        assertThat(contractDtos.get(1).getClientId()).isEqualTo(client.getId());
        assertThat(contractDtos.get(1).getCostAmount()).isEqualByComparingTo(contract2.getCostAmount());
    }

    @Test
    public void testConvertPersonDtoToEntity() {
        // Create a PersonDto
        PersonDto personDto = PersonDto.builder()
                .id(1L)
                .name("John Doe")
                .phone("+33612345678")
                .email("john@example.com")
                .birthDate(LocalDate.of(1990, 1, 1))
                .clientType("PERSON")
                .build();

        // Convert to entity
        Person person = DtoConverter.convertToEntity(personDto);

        // Assert correct mapping
        assertThat(person.getId()).isEqualTo(personDto.getId());
        assertThat(person.getName()).isEqualTo(personDto.getName());
        assertThat(person.getPhone()).isEqualTo(personDto.getPhone());
        assertThat(person.getEmail()).isEqualTo(personDto.getEmail());
        assertThat(person.getBirthDate()).isEqualTo(personDto.getBirthDate());
    }

    @Test
    public void testConvertCompanyDtoToEntity() {
        // Create a CompanyDto
        CompanyDto companyDto = CompanyDto.builder()
                .id(2L)
                .name("Acme Inc")
                .phone("+33612345679")
                .email("contact@acme.com")
                .companyIdentifier("acm-123")
                .clientType("COMPANY")
                .build();

        // Convert to entity
        Company company = DtoConverter.convertToEntity(companyDto);

        // Assert correct mapping
        assertThat(company.getId()).isEqualTo(companyDto.getId());
        assertThat(company.getName()).isEqualTo(companyDto.getName());
        assertThat(company.getPhone()).isEqualTo(companyDto.getPhone());
        assertThat(company.getEmail()).isEqualTo(companyDto.getEmail());
        assertThat(company.getCompanyIdentifier()).isEqualTo(companyDto.getCompanyIdentifier());
    }

    @Test
    public void testConvertContractDtoToEntity() {
        // Create a ContractDto
        ContractDto contractDto = ContractDto.builder()
                .id(1L)
                .clientId(1L) // Note: This is not used in conversion
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusYears(1))
                .costAmount(new BigDecimal("1000.00"))
                .createdAt(LocalDateTime.now()) // Note: This is not used in conversion
                .build();

        // Convert to entity
        Contract contract = DtoConverter.convertToEntity(contractDto);

        // Assert correct mapping
        assertThat(contract.getId()).isEqualTo(contractDto.getId());
        assertThat(contract.getStartDate()).isEqualTo(contractDto.getStartDate());
        assertThat(contract.getEndDate()).isEqualTo(contractDto.getEndDate());
        assertThat(contract.getCostAmount()).isEqualByComparingTo(contractDto.getCostAmount());
        // Client is set separately, not in conversion
        assertThat(contract.getClient()).isNull();
    }

    @Test
    public void testConvertContractDtoListToEntityList() {
        // Create ContractDtos
        ContractDto contractDto1 = ContractDto.builder()
                .id(1L)
                .clientId(1L)
                .startDate(LocalDate.now().minusMonths(1))
                .endDate(LocalDate.now().plusMonths(11))
                .costAmount(new BigDecimal("1000.00"))
                .createdAt(LocalDateTime.now().minusDays(30))
                .build();

        ContractDto contractDto2 = ContractDto.builder()
                .id(2L)
                .clientId(1L)
                .startDate(LocalDate.now().minusMonths(2))
                .endDate(LocalDate.now().plusMonths(10))
                .costAmount(new BigDecimal("2000.00"))
                .createdAt(LocalDateTime.now().minusDays(60))
                .build();

        List<ContractDto> contractDtos = Arrays.asList(contractDto1, contractDto2);

        // Convert to entity list
        List<Contract> contracts = DtoConverter.convertToEntityList(contractDtos);

        // Assert correct mapping
        assertThat(contracts).hasSize(2);
        assertThat(contracts.get(0).getId()).isEqualTo(contractDto1.getId());
        assertThat(contracts.get(0).getStartDate()).isEqualTo(contractDto1.getStartDate());
        assertThat(contracts.get(0).getCostAmount()).isEqualByComparingTo(contractDto1.getCostAmount());
        assertThat(contracts.get(1).getId()).isEqualTo(contractDto2.getId());
        assertThat(contracts.get(1).getStartDate()).isEqualTo(contractDto2.getStartDate());
        assertThat(contracts.get(1).getCostAmount()).isEqualByComparingTo(contractDto2.getCostAmount());
    }
}
