package com.barenne.insurance_manager_api.unit.repository;

import com.barenne.insurance_manager_api.model.Client;
import com.barenne.insurance_manager_api.model.Company;
import com.barenne.insurance_manager_api.repository.ClientRepository;
import com.barenne.insurance_manager_api.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@ActiveProfiles("test")
public class CompanyRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private ClientRepository clientRepository;  // Pour tester les fonctionnalités héritées

    @Test
    public void testExistsByCompanyIdentifier() {
        // Create Companies
        Company company1 = Company.builder()
                .name("Acme Inc.")
                .phone("+33612345678")
                .email("contact@acme.com")
                .companyIdentifier("acm-123")
                .build();

        Company company2 = Company.builder()
                .name("Beta Corp")
                .phone("+33612345679")
                .email("contact@beta.com")
                .companyIdentifier("bet-456")
                .build();

        // Persist entities
        entityManager.persist(company1);
        entityManager.persist(company2);
        entityManager.flush();
        entityManager.clear();

        // Check existsByCompanyIdentifier
        assertThat(companyRepository.existsByCompanyIdentifier("acm-123")).isTrue();
        assertThat(companyRepository.existsByCompanyIdentifier("bet-456")).isTrue();
        assertThat(companyRepository.existsByCompanyIdentifier("non-existent")).isFalse();
    }

    @Test
    public void testFindByCompanyIdentifier() {
        // Create a Company
        Company company = Company.builder()
                .name("Acme Inc.")
                .phone("+33612345678")
                .email("contact@acme.com")
                .companyIdentifier("acm-123")
                .build();

        // Persist entity
        entityManager.persist(company);
        entityManager.flush();
        entityManager.clear();

        // Find by company identifier
        Optional<Company> foundCompany = companyRepository.findByCompanyIdentifier("acm-123");
        Optional<Company> notFoundCompany = companyRepository.findByCompanyIdentifier("non-existent");

        // Assert results
        assertThat(foundCompany).isPresent();
        assertThat(foundCompany.get().getName()).isEqualTo("Acme Inc.");
        assertThat(foundCompany.get().getCompanyIdentifier()).isEqualTo("acm-123");

        assertThat(notFoundCompany).isEmpty();
    }

    @Test
    public void testUniqueCompanyIdentifier() {
        // Create a Company
        Company company1 = Company.builder()
                .name("Acme Inc.")
                .phone("+33612345678")
                .email("contact@acme.com")
                .companyIdentifier("acm-123")
                .build();

        // Persist first company
        entityManager.persist(company1);
        entityManager.flush();

        // Create second company with same identifier
        Company company2 = Company.builder()
                .name("Another Acme")
                .phone("+33612345680")
                .email("another@acme.com")
                .companyIdentifier("acm-123") // Same identifier as company1
                .build();

        // Try to persist second company and expect an exception indicating constraint violation
        Exception exception = assertThrows(Exception.class, () -> {
            entityManager.persist(company2);
            entityManager.flush();
        });

        // Verify exception message contains relevant constraint violation info
        String exceptionMessage = exception.getMessage().toLowerCase();
        boolean isConstraintViolation =
                exceptionMessage.contains("constraint") ||
                        exceptionMessage.contains("unique") ||
                        exceptionMessage.contains("violation") ||
                        exceptionMessage.contains("duplicate");

        assertTrue(isConstraintViolation, "Exception should indicate a constraint violation");
    }

    @Test
    public void testFindAllCompanies() {
        // Create Companies
        Company company1 = Company.builder()
                .name("Acme Inc.")
                .phone("+33612345678")
                .email("contact@acme.com")
                .companyIdentifier("acm-123")
                .build();

        Company company2 = Company.builder()
                .name("Beta Corp")
                .phone("+33612345679")
                .email("contact@beta.com")
                .companyIdentifier("bet-456")
                .build();

        // Persist entities
        entityManager.persist(company1);
        entityManager.persist(company2);
        entityManager.flush();
        entityManager.clear();

        // Find all companies using CompanyRepository
        List<Company> companies = companyRepository.findAll();

        // Assert results
        assertThat(companies).hasSize(2);
        assertThat(companies.stream().map(Company::getName)).contains("Acme Inc.", "Beta Corp");
        assertThat(companies.stream().map(Company::getCompanyIdentifier)).contains("acm-123", "bet-456");
    }

    // Tests that use ClientRepository with Company entities

    @Test
    public void testFindCompanyByClientRepository() {
        // Create a Company
        Company company = Company.builder()
                .name("Acme Inc.")
                .phone("+33612345678")
                .email("contact@acme.com")
                .companyIdentifier("acm-123")
                .build();

        // Persist entity
        Long id = entityManager.persistAndGetId(company, Long.class);
        entityManager.flush();
        entityManager.clear();

        // Retrieve using ClientRepository
        Optional<Client> result = clientRepository.findById(id);

        // Assert results
        assertThat(result).isPresent();
        assertThat(result.get()).isInstanceOf(Company.class);

        Company foundCompany = (Company) result.get();
        assertThat(foundCompany.getName()).isEqualTo("Acme Inc.");
        assertThat(foundCompany.getCompanyIdentifier()).isEqualTo("acm-123");
    }

    @Test
    public void testSaveCompanyWithClientRepository() {
        // Create a Company
        Company company = Company.builder()
                .name("Acme Inc.")
                .phone("+33612345678")
                .email("contact@acme.com")
                .companyIdentifier("acm-123")
                .build();

        // Save using ClientRepository
        Client savedClient = clientRepository.save(company);
        entityManager.flush();
        entityManager.clear();

        // Verify saved entity
        assertThat(savedClient).isNotNull();
        assertThat(savedClient.getId()).isNotNull();
        assertThat(savedClient).isInstanceOf(Company.class);

        Company savedCompany = (Company) savedClient;
        assertThat(savedCompany.getName()).isEqualTo("Acme Inc.");
        assertThat(savedCompany.getCompanyIdentifier()).isEqualTo("acm-123");

        // Verify retrieval with CompanyRepository
        Optional<Company> foundWithCompanyRepo = companyRepository.findById(savedClient.getId());
        assertThat(foundWithCompanyRepo).isPresent();
        assertThat(foundWithCompanyRepo.get().getName()).isEqualTo("Acme Inc.");
    }

    @Test
    public void testFindAllClientsIncludesCompany() {
        // Create a Company
        Company company = Company.builder()
                .name("Acme Inc.")
                .phone("+33612345678")
                .email("contact@acme.com")
                .companyIdentifier("acm-123")
                .build();

        // Persist entity
        entityManager.persist(company);
        entityManager.flush();
        entityManager.clear();

        // Retrieve all clients
        List<Client> allClients = clientRepository.findAll();

        // Assert results include our Company
        assertThat(allClients).isNotEmpty();
        assertThat(allClients.stream().anyMatch(c ->
                c instanceof Company && c.getName().equals("Acme Inc.")
        )).isTrue();
    }
}
