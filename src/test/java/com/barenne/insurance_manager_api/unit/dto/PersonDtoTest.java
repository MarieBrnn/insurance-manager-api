package com.barenne.insurance_manager_api.unit.dto;

import com.barenne.insurance_manager_api.dto.PersonDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class PersonDtoTest {

    private static Validator validator;

    @BeforeAll
    public static void setupValidatorInstance() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testPersonDtoValidation_Valid() {
        // Create a valid PersonDto
        PersonDto personDto = PersonDto.builder()
                .name("John Doe")
                .phone("+33612345678")
                .email("john@example.com")
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        // Validate the DTO
        Set<ConstraintViolation<PersonDto>> violations = validator.validate(personDto);

        // Assert no violations
        assertThat(violations).isEmpty();
    }

    @Test
    public void testPersonDtoValidation_InvalidName() {
        // Create a PersonDto with invalid name (null)
        PersonDto personDto = PersonDto.builder()
                .name(null) // Name is @NotBlank
                .phone("+33612345678")
                .email("john@example.com")
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        // Validate the DTO
        Set<ConstraintViolation<PersonDto>> violations = validator.validate(personDto);

        // Assert violations
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("name cannot be null");
    }

    @Test
    public void testPersonDtoValidation_InvalidPhone() {
        // Create a PersonDto with invalid phone
        PersonDto personDto = PersonDto.builder()
                .name("John Doe")
                .phone("invalid-phone") // Not matching the pattern
                .email("john@example.com")
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        // Validate the DTO
        Set<ConstraintViolation<PersonDto>> violations = validator.validate(personDto);

        // Assert violations
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Invalid phone number format");
    }

    @Test
    public void testPersonDtoValidation_InvalidEmail() {
        // Create a PersonDto with invalid email
        PersonDto personDto = PersonDto.builder()
                .name("John Doe")
                .phone("+33612345678")
                .email("invalid-email") // Not a valid email
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        // Validate the DTO
        Set<ConstraintViolation<PersonDto>> violations = validator.validate(personDto);

        // Assert violations
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Invalid email format");
    }

    @Test
    public void testPersonDtoValidation_InvalidBirthDate() {
        // Create a PersonDto with future birthdate
        PersonDto personDto = PersonDto.builder()
                .name("John Doe")
                .phone("+33612345678")
                .email("john@example.com")
                .birthDate(LocalDate.now().plusYears(1)) // Future date
                .build();

        // Validate the DTO
        Set<ConstraintViolation<PersonDto>> violations = validator.validate(personDto);

        // Assert violations
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("BirthDate must be in the past");
    }

    @Test
    public void testPersonDtoValidation_NullBirthDate() {
        // Create a PersonDto with null birthdate
        PersonDto personDto = PersonDto.builder()
                .name("John Doe")
                .phone("+33612345678")
                .email("john@example.com")
                .birthDate(null) // Null date
                .build();

        // Validate the DTO
        Set<ConstraintViolation<PersonDto>> violations = validator.validate(personDto);

        // Assert violations
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("BirthDate cannot be null");
    }

    @Test
    public void testFactoryMethod() {
        // Test the from() factory method
        Long id = 1L;
        String name = "John Doe";
        String phone = "+33612345678";
        String email = "john@example.com";
        LocalDate birthDate = LocalDate.of(1990, 1, 1);

        PersonDto personDto = PersonDto.from(id, name, phone, email, birthDate);

        // Assert correct mapping
        assertThat(personDto.getId()).isEqualTo(id);
        assertThat(personDto.getName()).isEqualTo(name);
        assertThat(personDto.getPhone()).isEqualTo(phone);
        assertThat(personDto.getEmail()).isEqualTo(email);
        assertThat(personDto.getBirthDate()).isEqualTo(birthDate);
        assertThat(personDto.getClientType()).isEqualTo("PERSON");
    }
}
