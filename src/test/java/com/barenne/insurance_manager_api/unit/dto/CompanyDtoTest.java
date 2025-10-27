package com.barenne.insurance_manager_api.unit.dto;

import com.barenne.insurance_manager_api.dto.CompanyDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class CompanyDtoTest {

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
    public void testCompanyDtoValidation_Valid() {
        // Create a valid CompanyDto
        CompanyDto companyDto = CompanyDto.builder()
                .name("Acme Inc")
                .phone("+33612345678")
                .email("contact@acme.com")
                .companyIdentifier("acm-123")
                .build();

        // Validate the DTO
        Set<ConstraintViolation<CompanyDto>> violations = validator.validate(companyDto);

        // Assert no violations
        assertThat(violations).isEmpty();
    }

    @Test
    public void testCompanyDtoValidation_InvalidCompanyIdentifier() {
        // Create a CompanyDto with invalid company identifier
        CompanyDto companyDto = CompanyDto.builder()
                .name("Acme Inc")
                .phone("+33612345678")
                .email("contact@acme.com")
                .companyIdentifier("INVALID") // Not matching the pattern
                .build();

        // Validate the DTO
        Set<ConstraintViolation<CompanyDto>> violations = validator.validate(companyDto);

        // Assert violations
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Invalid Company Identifier");
    }

    @Test
    public void testCompanyDtoValidation_NullCompanyIdentifier() {
        // Create a CompanyDto with null company identifier
        CompanyDto companyDto = CompanyDto.builder()
                .name("Acme Inc")
                .phone("+33612345678")
                .email("contact@acme.com")
                .companyIdentifier(null)
                .build();

        // Validate the DTO
        Set<ConstraintViolation<CompanyDto>> violations = validator.validate(companyDto);

        // Assert violations
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Company identifier cannot be null");
    }

    @Test
    public void testCompanyDtoValidation_InvalidName() {
        // Create a CompanyDto with null name
        CompanyDto companyDto = CompanyDto.builder()
                .name(null) // Name is @NotBlank
                .phone("+33612345678")
                .email("contact@acme.com")
                .companyIdentifier("acm-123")
                .build();

        // Validate the DTO
        Set<ConstraintViolation<CompanyDto>> violations = validator.validate(companyDto);

        // Assert violations
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("name cannot be null");
    }

    @Test
    public void testCompanyDtoValidation_InvalidPhone() {
        // Create a CompanyDto with invalid phone
        CompanyDto companyDto = CompanyDto.builder()
                .name("Acme Inc")
                .phone("invalid-phone") // Not matching the pattern
                .email("contact@acme.com")
                .companyIdentifier("acm-123")
                .build();

        // Validate the DTO
        Set<ConstraintViolation<CompanyDto>> violations = validator.validate(companyDto);

        // Assert violations
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Invalid phone number format");
    }

    @Test
    public void testCompanyDtoValidation_InvalidEmail() {
        // Create a CompanyDto with invalid email
        CompanyDto companyDto = CompanyDto.builder()
                .name("Acme Inc")
                .phone("+33612345678")
                .email("invalid-email") // Not a valid email
                .companyIdentifier("acm-123")
                .build();

        // Validate the DTO
        Set<ConstraintViolation<CompanyDto>> violations = validator.validate(companyDto);

        // Assert violations
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Invalid email format");
    }

    @Test
    public void testFactoryMethod() {
        // Test the from() factory method
        Long id = 2L;
        String name = "Acme Inc";
        String phone = "+33612345678";
        String email = "contact@acme.com";
        String companyIdentifier = "acm-123";

        CompanyDto companyDto = CompanyDto.from(id, name, phone, email, companyIdentifier);

        // Assert correct mapping
        assertThat(companyDto.getId()).isEqualTo(id);
        assertThat(companyDto.getName()).isEqualTo(name);
        assertThat(companyDto.getPhone()).isEqualTo(phone);
        assertThat(companyDto.getEmail()).isEqualTo(email);
        assertThat(companyDto.getCompanyIdentifier()).isEqualTo(companyIdentifier);
        assertThat(companyDto.getClientType()).isEqualTo("COMPANY");
    }
}
