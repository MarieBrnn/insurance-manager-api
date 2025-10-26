package com.barenne.insurance_manager_api.unit.model;

import com.barenne.insurance_manager_api.model.Client;
import com.barenne.insurance_manager_api.model.Contract;
import com.barenne.insurance_manager_api.model.Person;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class PersonTest {
    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    public void testPersonCreation() {
        Person person = Person.builder()
                .id(1L)
                .name("John Doe")
                .phone("+33612345678")
                .email("john@example.com")
                .birthDate(LocalDate.of(1990, 1, 1))
                .contracts(new ArrayList<>())
                .build();

        assertEquals(1L, person.getId());
        assertEquals("John Doe", person.getName());
        assertEquals("+33612345678", person.getPhone());
        assertEquals("john@example.com", person.getEmail());
        assertEquals(LocalDate.of(1990, 1, 1), person.getBirthDate());
        assertNotNull(person.getContracts());
        assertTrue(person.getContracts().isEmpty());
    }

    @Test
    public void testPersonInheritance() {
        Person person = new Person();
        person.setName("John Doe");
        person.setPhone("+33612345678");
        person.setEmail("john@example.com");
        person.setBirthDate(LocalDate.of(1990, 1, 1));

        assertTrue(person instanceof Client, "Person should be an instance of Client");

        assertEquals("John Doe", person.getName());
        assertEquals("+33612345678", person.getPhone());
        assertEquals("john@example.com", person.getEmail());
    }

    // Tests for validations inherited from Client

    @Test
    public void testNameValidation() {
        Person person = new Person();
        person.setPhone("+33612345678");
        person.setEmail("john@example.com");
        person.setBirthDate(LocalDate.of(1990, 1, 1));
        // Name is missing

        Set<ConstraintViolation<Person>> violations = validator.validate(person);
        assertFalse(violations.isEmpty());

        boolean nameViolationFound = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("name"));
        assertTrue(nameViolationFound, "Should have validation error for name");

        // Set valid name
        person.setName("John Doe");
        violations = validator.validateProperty(person, "name");
        assertTrue(violations.isEmpty(), "Should not have validation error for valid name");
    }

    @Test
    public void testPhoneValidation() {
        Person person = new Person();
        person.setName("John Doe");
        person.setEmail("john@example.com");
        person.setBirthDate(LocalDate.of(1990, 1, 1));

        // Test missing phone
        Set<ConstraintViolation<Person>> violations = validator.validate(person);
        assertFalse(violations.isEmpty());
        boolean phoneNullViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("phone"));
        assertTrue(phoneNullViolation, "Should have validation error for missing phone");

        // Test invalid phone format
        person.setPhone("123");
        violations = validator.validate(person);
        assertFalse(violations.isEmpty());
        boolean phoneFormatViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("phone"));
        assertTrue(phoneFormatViolation, "Should have validation error for invalid phone format");

        // Test valid phone format
        person.setPhone("+33612345678");
        violations = validator.validateProperty(person, "phone");
        assertTrue(violations.isEmpty(), "Should not have validation error for valid phone format");
    }

    @Test
    public void testEmailValidation() {
        Person person = new Person();
        person.setName("John Doe");
        person.setPhone("+33612345678");
        person.setBirthDate(LocalDate.of(1990, 1, 1));

        // Test missing email
        Set<ConstraintViolation<Person>> violations = validator.validate(person);
        assertFalse(violations.isEmpty());
        boolean emailNullViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("email"));
        assertTrue(emailNullViolation, "Should have validation error for missing email");

        // Test invalid email format
        person.setEmail("invalid-email");
        violations = validator.validate(person);
        assertFalse(violations.isEmpty());
        boolean emailFormatViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("email"));
        assertTrue(emailFormatViolation, "Should have validation error for invalid email format");

        // Test valid email format
        person.setEmail("john@example.com");
        violations = validator.validateProperty(person, "email");
        assertTrue(violations.isEmpty(), "Should not have validation error for valid email format");
    }

    // Tests specific to Person

    @Test
    public void testBirthDateValidation() {
        Person person = new Person();
        person.setName("John Doe");
        person.setPhone("+33612345678");
        person.setEmail("john@example.com");

        // Test missing birthDate
        Set<ConstraintViolation<Person>> violations = validator.validate(person);
        assertFalse(violations.isEmpty());
        boolean birthDateNullViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("birthDate"));
        assertTrue(birthDateNullViolation, "Should have validation error for missing birthDate");

        // Test future birthDate
        LocalDate futureDate = LocalDate.now().plusYears(1);
        person.setBirthDate(futureDate);

        violations = validator.validate(person);
        assertFalse(violations.isEmpty());
        boolean birthDateFutureViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("birthDate"));
        assertTrue(birthDateFutureViolation, "Should have validation error for future birthDate");

        // Test valid birthDate
        LocalDate pastDate = LocalDate.of(1990, 1, 1);
        person.setBirthDate(pastDate);

        violations = validator.validateProperty(person, "birthDate");
        assertTrue(violations.isEmpty(), "Should not have validation error for valid birthDate");
    }

    @Test
    public void testEqualsAndHashCode() {
        Person person1 = Person.builder()
                .id(1L)
                .name("John Doe")
                .phone("+33612345678")
                .email("john@example.com")
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        Person person2 = person1;

        Person differentPerson = Person.builder()
                .id(2L)
                .name("Jane Doe")
                .phone("+33612345679")
                .email("jane@example.com")
                .birthDate(LocalDate.of(1992, 2, 2))
                .build();

        assertEquals(person1, person2, "Equal persons should be equal");
        assertNotEquals(person1, differentPerson, "Different persons should not be equal");
        assertEquals(person1.hashCode(), person2.hashCode(), "Equal persons should have the same hashCode");
        assertNotEquals(person1.hashCode(), differentPerson.hashCode(), "Different persons should have different hashCodes");
    }

    @Test
    public void testToString() {
        Person person = Person.builder()
                .id(1L)
                .name("John Doe")
                .phone("+33612345678")
                .email("john@example.com")
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        String toString = person.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("Person"));
    }

    @Test
    public void testContractsManagement() {
        // Create a person
        Person person = Person.builder()
                .name("John Doe")
                .phone("+33612345678")
                .email("john@example.com")
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        // Verify contracts collection is initialized
        assertNotNull(person.getContracts());
        assertTrue(person.getContracts().isEmpty());

        // Create and add a contract
        Contract contract = new Contract();
        contract.setClient(person);
        person.getContracts().add(contract);

        // Verify contract was added
        assertEquals(1, person.getContracts().size());
        assertTrue(person.getContracts().contains(contract));
    }
}
