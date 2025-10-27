package com.barenne.insurance_manager_api.unit.dto;

import com.barenne.insurance_manager_api.dto.ContractDto;
import com.barenne.insurance_manager_api.model.Client;
import com.barenne.insurance_manager_api.model.Contract;
import com.barenne.insurance_manager_api.model.Person;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ContractDtoTest {

    private static Validator validator;
    private static ValidatorFactory factory;

    @BeforeAll
    public static void setupValidatorInstance() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    public static void closeFactory() {
        if (factory != null) {
            factory.close();
        }
    }

    @Test
    public void testContractDtoValidation_Valid() {
        // Create a valid ContractDto
        ContractDto contractDto = ContractDto.builder()
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusYears(1))
                .costAmount(new BigDecimal("1000.00"))
                .build();

        // Validate the DTO
        Set<ConstraintViolation<ContractDto>> violations = validator.validate(contractDto);

        // Assert no violations
        assertThat(violations).isEmpty();
    }

    @Test
    public void testContractDtoValidation_NullCostAmount() {
        // Create a ContractDto with null cost amount
        ContractDto contractDto = ContractDto.builder()
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusYears(1))
                .costAmount(null) // CostAmount is @NotNull
                .build();

        // Validate the DTO
        Set<ConstraintViolation<ContractDto>> violations = validator.validate(contractDto);

        // Assert violations
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Cost amount cannot be null");
    }

    @Test
    public void testContractDtoValidation_ZeroCostAmount() {
        // Create a ContractDto with zero cost amount
        ContractDto contractDto = ContractDto.builder()
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusYears(1))
                .costAmount(BigDecimal.ZERO) // Cost amount is @Positive
                .build();

        // Validate the DTO
        Set<ConstraintViolation<ContractDto>> violations = validator.validate(contractDto);

        // Assert violations
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Cost amount must be positive");
    }

    @Test
    public void testContractDtoValidation_NegativeCostAmount() {
        // Create a ContractDto with negative cost amount
        ContractDto contractDto = ContractDto.builder()
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusYears(1))
                .costAmount(new BigDecimal("-100.00")) // Cost amount is @Positive
                .build();

        // Validate the DTO
        Set<ConstraintViolation<ContractDto>> violations = validator.validate(contractDto);

        // Assert violations
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Cost amount must be positive");
    }

    @Test
    public void testFactoryMethod() {
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

        Contract contract = Contract.builder()
                .id(1L)
                .client(client)
                .startDate(startDate)
                .endDate(endDate)
                .costAmount(costAmount)
                .createdAt(createdAt)
                .updatedAt(LocalDateTime.now())
                .build();

        // Use the factory method
        ContractDto contractDto = ContractDto.from(contract);

        // Assert correct mapping
        assertThat(contractDto.getId()).isEqualTo(1L);
        assertThat(contractDto.getClientId()).isEqualTo(1L);
        assertThat(contractDto.getStartDate()).isEqualTo(startDate);
        assertThat(contractDto.getEndDate()).isEqualTo(endDate);
        assertThat(contractDto.getCostAmount()).isEqualByComparingTo(costAmount);
        assertThat(contractDto.getCreatedAt()).isEqualTo(createdAt);
    }
}
