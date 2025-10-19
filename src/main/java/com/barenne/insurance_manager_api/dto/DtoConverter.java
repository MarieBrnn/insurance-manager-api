package com.barenne.insurance_manager_api.dto;

import com.barenne.insurance_manager_api.model.Company;
import com.barenne.insurance_manager_api.model.Contract;
import com.barenne.insurance_manager_api.model.Person;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class DtoConverter {

    //Convert Person to PersonDto
    public static PersonDto convertToDto(Person person) {
        return PersonDto.builder()
                .id(person.getId())
                .name(person.getName())
                .phone(person.getPhone())
                .email(person.getEmail())
                .birthDate(person.getBirthDate())
                .clientType("PERSON")
                .build();
    }

    //Convert Company to CompanyDto
    public static CompanyDto convertToDto(Company company) {
        return CompanyDto.builder()
                .id(company.getId())
                .name(company.getName())
                .phone(company.getPhone())
                .email(company.getEmail())
                .companyIdentifier(company.getCompanyIdentifier())
                .clientType("COMPANY")
                .build();
    }

    //Comvert Contract to ContractDto
    public static ContractDto convertToDto(Contract contract) {
        return ContractDto.builder()
                .id(contract.getId())
                .clientId(contract.getClient().getId())
                .startDate(contract.getStartDate())
                .endDate(contract.getEndDate())
                .costAmount(contract.getCostAmount())
                .createdAt(contract.getCreatedAt())
                .build();
    }

    //Convert List<Contract> to List<ContractDto>
    public static List<ContractDto> convertToDtoList(List<Contract> contracts) {
        return contracts.stream()
                .map(DtoConverter::convertToDto)
                .collect(Collectors.toList());
    }

    //Convert PersonDto to Person
    public static Person convertToEntity(PersonDto personDto) {
        return Person.builder()
                .id(personDto.getId())
                .name(personDto.getName())
                .phone(personDto.getPhone())
                .email(personDto.getEmail())
                .birthDate(personDto.getBirthDate())
                .build();
    }

    //Convert CompanyDto to Company
    public static Company convertToEntity(CompanyDto companyDto) {
        return Company.builder()
                .id(companyDto.getId())
                .name(companyDto.getName())
                .phone(companyDto.getPhone())
                .email(companyDto.getEmail())
                .companyIdentifier(companyDto.getCompanyIdentifier())
                .build();
    }

    //Convert ContractDto to Contract
    public static Contract convertToEntity(ContractDto contractDto) {
        return Contract.builder()
                .id(contractDto.getId())
                .startDate(contractDto.getStartDate())
                .endDate(contractDto.getEndDate())
                .costAmount(contractDto.getCostAmount())
                .build();
    }

    //Convert List<ContractSto> to List<Contract>
    public static List<Contract> convertToEntityList(List<ContractDto> contractDtos) {
        return contractDtos.stream()
                .map(DtoConverter::convertToEntity)
                .collect(Collectors.toList());
    }
}
