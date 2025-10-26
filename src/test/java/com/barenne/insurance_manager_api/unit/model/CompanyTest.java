package com.barenne.insurance_manager_api.unit.model;

import com.barenne.insurance_manager_api.model.Client;
import com.barenne.insurance_manager_api.model.Company;
import com.barenne.insurance_manager_api.model.Contract;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CompanyTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    public void testCompanyCreation() {
        Company company = Company.builder()
                .id(1L)
                .name("Acme Inc.")
                .phone("+33612345678")
                .email("contact@acme.com")
                .companyIdentifier("acm-123")
                .contracts(new ArrayList<>())
                .build();

        assertEquals(1L, company.getId());
        assertEquals("Acme Inc.", company.getName());
        assertEquals("+33612345678", company.getPhone());
        assertEquals("contact@acme.com", company.getEmail());
        assertEquals("acm-123", company.getCompanyIdentifier());
        assertNotNull(company.getContracts());
        assertTrue(company.getContracts().isEmpty());
    }

    @Test
    public void testCompanyInheritance() {
        Company company = new Company();
        company.setName("Acme Inc.");
        company.setPhone("+33612345678");
        company.setEmail("contact@acme.com");
        company.setCompanyIdentifier("acm-123");

        assertTrue(company instanceof Client, "Company should be an instance of Client");

        assertEquals("Acme Inc.", company.getName());
        assertEquals("+33612345678", company.getPhone());
        assertEquals("contact@acme.com", company.getEmail());
    }

    // Tests for validations inherited from Client

    @Test
    public void testNameValidation() {
        Company company = new Company();
        company.setPhone("+33612345678");
        company.setEmail("contact@acme.com");
        company.setCompanyIdentifier("acm-123");
        // Name is missing

        Set<ConstraintViolation<Company>> violations = validator.validate(company);
        assertFalse(violations.isEmpty());

        boolean nameViolationFound = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("name"));
        assertTrue(nameViolationFound, "Should have validation error for name");

        // Set valid name
        company.setName("Acme Inc.");
        violations = validator.validateProperty(company, "name");
        assertTrue(violations.isEmpty(), "Should not have validation error for valid name");
    }

    @Test
    public void testPhoneValidation() {
        Company company = new Company();
        company.setName("Acme Inc.");
        company.setEmail("contact@acme.com");
        company.setCompanyIdentifier("acm-123");

        // Test missing phone
        Set<ConstraintViolation<Company>> violations = validator.validate(company);
        assertFalse(violations.isEmpty());
        boolean phoneNullViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("phone"));
        assertTrue(phoneNullViolation, "Should have validation error for missing phone");

        // Test invalid phone format
        company.setPhone("123");
        violations = validator.validate(company);
        assertFalse(violations.isEmpty());
        boolean phoneFormatViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("phone"));
        assertTrue(phoneFormatViolation, "Should have validation error for invalid phone format");

        // Test valid phone format
        company.setPhone("+33612345678");
        violations = validator.validateProperty(company, "phone");
        assertTrue(violations.isEmpty(), "Should not have validation error for valid phone format");
    }

    @Test
    public void testEmailValidation() {
        Company company = new Company();
        company.setName("Acme Inc.");
        company.setPhone("+33612345678");
        company.setCompanyIdentifier("acm-123");

        // Test missing email
        Set<ConstraintViolation<Company>> violations = validator.validate(company);
        assertFalse(violations.isEmpty());
        boolean emailNullViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("email"));
        assertTrue(emailNullViolation, "Should have validation error for missing email");

        // Test invalid email format
        company.setEmail("invalid-email");
        violations = validator.validate(company);
        assertFalse(violations.isEmpty());
        boolean emailFormatViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("email"));
        assertTrue(emailFormatViolation, "Should have validation error for invalid email format");

        // Test valid email format
        company.setEmail("contact@acme.com");
        violations = validator.validateProperty(company, "email");
        assertTrue(violations.isEmpty(), "Should not have validation error for valid email format");
    }

    // Tests specific to Company

    @Test
    public void testCompanyIdentifierValidation() {
        Company company = new Company();
        company.setName("Acme Inc.");
        company.setPhone("+33612345678");
        company.setEmail("contact@acme.com");

        // Test missing companyIdentifier
        Set<ConstraintViolation<Company>> violations = validator.validate(company);
        assertFalse(violations.isEmpty());
        boolean identifierNullViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("companyIdentifier"));
        assertTrue(identifierNullViolation, "Should have validation error for missing companyIdentifier");

        // Test invalid companyIdentifier format
        company.setCompanyIdentifier("invalid");

        violations = validator.validate(company);
        assertFalse(violations.isEmpty());
        boolean identifierFormatViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("companyIdentifier"));
        assertTrue(identifierFormatViolation, "Should have validation error for invalid companyIdentifier format");

        // Test invalid uppercase format
        company.setCompanyIdentifier("ACM-123");

        violations = validator.validate(company);
        assertFalse(violations.isEmpty());
        boolean identifierUppercaseViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("companyIdentifier"));
        assertTrue(identifierUppercaseViolation, "Should have validation error for uppercase in companyIdentifier");

        // Test valid companyIdentifier
        company.setCompanyIdentifier("acm-123");

        violations = validator.validateProperty(company, "companyIdentifier");
        assertTrue(violations.isEmpty(), "Should not have validation error for valid companyIdentifier");
    }

    @Test
    public void testEqualsAndHashCode() {
        Company company1 = Company.builder()
                .id(1L)
                .name("Acme Inc.")
                .phone("+33612345678")
                .email("contact@acme.com")
                .companyIdentifier("acm-123")
                .build();

        Company company2 = company1;

        Company differentCompany = Company.builder()
                .id(2L)
                .name("Beta Corp.")
                .phone("+33612345679")
                .email("contact@beta.com")
                .companyIdentifier("bet-456")
                .build();

        assertEquals(company1, company2, "Equal companies should be equal");
        assertNotEquals(company1, differentCompany, "Different companies should not be equal");
        assertEquals(company1.hashCode(), company2.hashCode(), "Equal companies should have the same hashCode");
        assertNotEquals(company1.hashCode(), differentCompany.hashCode(), "Different companies should have different hashCodes");
    }

    @Test
    public void testToString() {
        Company company = Company.builder()
                .id(1L)
                .name("Acme Inc.")
                .phone("+33612345678")
                .email("contact@acme.com")
                .companyIdentifier("acm-123")
                .build();

        String toString = company.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("Company"));
    }

    @Test
    public void testContractsManagement() {
        // Create a company
        Company company = Company.builder()
                .name("Acme Inc.")
                .phone("+33612345678")
                .email("contact@acme.com")
                .companyIdentifier("acm-123")
                .build();

        // Verify contracts collection is initialized
        assertNotNull(company.getContracts());
        assertTrue(company.getContracts().isEmpty());

        // Create and add a contract
        Contract contract = new Contract();
        contract.setClient(company);
        company.getContracts().add(contract);

        // Verify contract was added
        assertEquals(1, company.getContracts().size());
        assertTrue(company.getContracts().contains(contract));
    }

}
