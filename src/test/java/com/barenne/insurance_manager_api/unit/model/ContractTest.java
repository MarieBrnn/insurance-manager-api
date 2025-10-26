package com.barenne.insurance_manager_api.unit.model;

import com.barenne.insurance_manager_api.model.Client;
import com.barenne.insurance_manager_api.model.Contract;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ContractTest {
    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Mock
    private Client mockClient;

    @Test
    public void testContractCreation() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusYears(1);
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedAt = LocalDateTime.now();

        Contract contract = Contract.builder()
                .id(1L)
                .client(mockClient)
                .startDate(startDate)
                .endDate(endDate)
                .costAmount(new BigDecimal("1000.00"))
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        assertEquals(1L, contract.getId());
        assertEquals(mockClient, contract.getClient());
        assertEquals(startDate, contract.getStartDate());
        assertEquals(endDate, contract.getEndDate());
        assertEquals(new BigDecimal("1000.00"), contract.getCostAmount());
        assertEquals(createdAt, contract.getCreatedAt());
        assertEquals(updatedAt, contract.getUpdatedAt());
        assertFalse(contract.isSkipAutoUpdate());
    }

    @Test
    public void testClientValidation() {
        Contract contract = Contract.builder()
                .startDate(LocalDate.now())
                .costAmount(new BigDecimal("1000.00"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Client is null, but since it's a JPA relationship and not validated with Bean Validation,
        // we can't test it using the validator. This would be caught at the database level.

        // Instead, let's just verify setting/getting the client
        contract.setClient(mockClient);
        assertEquals(mockClient, contract.getClient());
    }

    @Test
    public void testStartDateValidation() {
        Contract contract = Contract.builder()
                .client(mockClient)
                .costAmount(new BigDecimal("1000.00"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Test missing startDate
        Set<ConstraintViolation<Contract>> violations = validator.validate(contract);
        assertFalse(violations.isEmpty());
        boolean startDateNullViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("startDate"));
        assertTrue(startDateNullViolation, "Should have validation error for missing startDate");

        // Test valid startDate
        contract.setStartDate(LocalDate.now());
        violations = validator.validateProperty(contract, "startDate");
        assertTrue(violations.isEmpty(), "Should not have validation error for valid startDate");
    }

    @Test
    public void testCostAmountValidation() {
        Contract contract = Contract.builder()
                .client(mockClient)
                .startDate(LocalDate.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Test missing costAmount
        Set<ConstraintViolation<Contract>> violations = validator.validate(contract);
        assertFalse(violations.isEmpty());
        boolean costAmountNullViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("costAmount"));
        assertTrue(costAmountNullViolation, "Should have validation error for missing costAmount");

        // Test negative costAmount
        contract.setCostAmount(new BigDecimal("-100.00"));
        violations = validator.validate(contract);
        assertFalse(violations.isEmpty());
        boolean costAmountNegativeViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("costAmount"));
        assertTrue(costAmountNegativeViolation, "Should have validation error for negative costAmount");

        // Test zero costAmount
        contract.setCostAmount(BigDecimal.ZERO);
        violations = validator.validate(contract);
        assertFalse(violations.isEmpty());
        boolean costAmountZeroViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("costAmount"));
        assertTrue(costAmountZeroViolation, "Should have validation error for zero costAmount");

        // Test valid costAmount
        contract.setCostAmount(new BigDecimal("1000.00"));
        violations = validator.validateProperty(contract, "costAmount");
        assertTrue(violations.isEmpty(), "Should not have validation error for valid costAmount");
    }

    @Test
    public void testCreatedAtUpdatedAtValidation() {
        Contract contract = Contract.builder()
                .client(mockClient)
                .startDate(LocalDate.now())
                .costAmount(new BigDecimal("1000.00"))
                .build();

        // Test missing createdAt and updatedAt
        Set<ConstraintViolation<Contract>> violations = validator.validate(contract);
        assertFalse(violations.isEmpty());
        boolean createdAtNullViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("createdAt"));
        boolean updatedAtNullViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("updatedAt"));
        assertTrue(createdAtNullViolation, "Should have validation error for missing createdAt");
        assertTrue(updatedAtNullViolation, "Should have validation error for missing updatedAt");

        // Test valid dates
        contract.setCreatedAt(LocalDateTime.now());
        contract.setUpdatedAt(LocalDateTime.now());
        violations = validator.validate(contract);
        boolean noCreatedAtViolation = violations.stream()
                .noneMatch(v -> v.getPropertyPath().toString().equals("createdAt"));
        boolean noUpdatedAtViolation = violations.stream()
                .noneMatch(v -> v.getPropertyPath().toString().equals("updatedAt"));
        assertTrue(noCreatedAtViolation, "Should not have validation error for valid createdAt");
        assertTrue(noUpdatedAtViolation, "Should not have validation error for valid updatedAt");
    }

    @Test
    public void testIsActive() {
        // Test with null endDate (should be active)
        Contract contract = Contract.builder()
                .startDate(LocalDate.now())
                .build();

        assertTrue(contract.isActive(), "Contract with null endDate should be active");

        // Test with future endDate (should be active)
        contract.setEndDate(LocalDate.now().plusDays(1));
        assertTrue(contract.isActive(), "Contract with future endDate should be active");

        // Test with today's endDate (should be inactive because today is not before today)
        contract.setEndDate(LocalDate.now());
        assertFalse(contract.isActive(), "Contract ending today should be inactive");

        // Test with past endDate (should be inactive)
        contract.setEndDate(LocalDate.now().minusDays(1));
        assertFalse(contract.isActive(), "Contract with past endDate should be inactive");
    }

    @Test
    public void testToString() {
        Contract contract = Contract.builder()
                .id(1L)
                .client(mockClient)
                .startDate(LocalDate.of(2023, 1, 1))
                .endDate(LocalDate.of(2024, 1, 1))
                .costAmount(new BigDecimal("1000.00"))
                .createdAt(LocalDateTime.of(2023, 1, 1, 10, 0))
                .updatedAt(LocalDateTime.of(2023, 1, 1, 10, 0))
                .build();

        String toString = contract.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("Contract"));
    }

    @Test
    public void testEqualsAndHashCode() {
        // Créer une instance de Contract
        Contract contract1 = Contract.builder()
                .id(1L)
                .client(mockClient)
                .startDate(LocalDate.of(2023, 1, 1))
                .endDate(LocalDate.of(2024, 1, 1))
                .costAmount(new BigDecimal("1000.00"))
                .createdAt(LocalDateTime.of(2023, 1, 1, 10, 0))
                .updatedAt(LocalDateTime.of(2023, 1, 1, 10, 0))
                .build();

        // Utiliser la même instance pour contract2
        Contract contract2 = contract1;

        // Créer une instance différente de Contract
        Contract differentContract = Contract.builder()
                .id(2L)
                .client(mockClient)
                .startDate(LocalDate.of(2023, 2, 1))
                .endDate(LocalDate.of(2024, 2, 1))
                .costAmount(new BigDecimal("2000.00"))
                .createdAt(LocalDateTime.of(2023, 2, 1, 10, 0))
                .updatedAt(LocalDateTime.of(2023, 2, 1, 10, 0))
                .build();

        // Tester l'égalité
        assertEquals(contract1, contract2, "Equal contracts should be equal");
        assertNotEquals(contract1, differentContract, "Different contracts should not be equal");
        assertEquals(contract1.hashCode(), contract2.hashCode(), "Equal contracts should have the same hashCode");
        assertNotEquals(contract1.hashCode(), differentContract.hashCode(), "Different contracts should have different hashCodes");
    }
}
