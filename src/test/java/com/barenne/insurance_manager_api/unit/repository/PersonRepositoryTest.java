package com.barenne.insurance_manager_api.unit.repository;

import com.barenne.insurance_manager_api.model.Client;
import com.barenne.insurance_manager_api.model.Person;
import com.barenne.insurance_manager_api.repository.ClientRepository;
import com.barenne.insurance_manager_api.repository.PersonRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class PersonRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private ClientRepository clientRepository;  // Pour tester les fonctionnalités héritées

    @Test
    public void testFindAllPersons() {
        // Create Persons
        Person person1 = Person.builder()
                .name("John Doe")
                .phone("+33612345678")
                .email("john@example.com")
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        Person person2 = Person.builder()
                .name("Jane Doe")
                .phone("+33612345679")
                .email("jane@example.com")
                .birthDate(LocalDate.of(1992, 2, 2))
                .build();

        // Persist entities
        entityManager.persist(person1);
        entityManager.persist(person2);
        entityManager.flush();
        entityManager.clear();

        // Find all persons using PersonRepository
        List<Person> persons = personRepository.findAll();

        // Assert results
        assertThat(persons).hasSize(2);
        assertThat(persons.stream().map(Person::getName)).contains("John Doe", "Jane Doe");
    }

    @Test
    public void testSaveAndFindPersonById() {
        // Create a Person
        Person person = Person.builder()
                .name("John Doe")
                .phone("+33612345678")
                .email("john@example.com")
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        // Save entity using PersonRepository
        Person savedPerson = personRepository.save(person);
        entityManager.flush();
        entityManager.clear();

        // Find by id using PersonRepository
        Person foundPerson = personRepository.findById(savedPerson.getId()).orElse(null);

        // Assert results
        assertThat(foundPerson).isNotNull();
        assertThat(foundPerson.getId()).isEqualTo(savedPerson.getId());
        assertThat(foundPerson.getName()).isEqualTo("John Doe");
        assertThat(foundPerson.getBirthDate()).isEqualTo(LocalDate.of(1990, 1, 1));
    }

    @Test
    public void testDeletePersonById() {
        // Create a Person
        Person person = Person.builder()
                .name("John Doe")
                .phone("+33612345678")
                .email("john@example.com")
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        // Save entity using PersonRepository
        Person savedPerson = personRepository.save(person);
        entityManager.flush();

        // Verify it exists
        assertThat(personRepository.findById(savedPerson.getId())).isPresent();

        // Delete entity using PersonRepository
        personRepository.deleteById(savedPerson.getId());
        entityManager.flush();
        entityManager.clear();

        // Verify it no longer exists
        assertThat(personRepository.findById(savedPerson.getId())).isEmpty();
    }

    // Tests that use ClientRepository with Person entities

    @Test
    public void testFindPersonByClientRepository() {
        // Create a Person
        Person person = Person.builder()
                .name("John Doe")
                .phone("+33612345678")
                .email("john@example.com")
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        // Persist entity
        Long id = entityManager.persistAndGetId(person, Long.class);
        entityManager.flush();
        entityManager.clear();

        // Retrieve using ClientRepository
        Optional<Client> result = clientRepository.findById(id);

        // Assert results
        assertThat(result).isPresent();
        assertThat(result.get()).isInstanceOf(Person.class);

        Person foundPerson = (Person) result.get();
        assertThat(foundPerson.getName()).isEqualTo("John Doe");
        assertThat(foundPerson.getBirthDate()).isEqualTo(LocalDate.of(1990, 1, 1));
    }

    @Test
    public void testSavePersonWithClientRepository() {
        // Create a Person
        Person person = Person.builder()
                .name("John Doe")
                .phone("+33612345678")
                .email("john@example.com")
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        // Save using ClientRepository
        Client savedClient = clientRepository.save(person);
        entityManager.flush();
        entityManager.clear();

        // Verify saved entity
        assertThat(savedClient).isNotNull();
        assertThat(savedClient.getId()).isNotNull();
        assertThat(savedClient).isInstanceOf(Person.class);

        Person savedPerson = (Person) savedClient;
        assertThat(savedPerson.getName()).isEqualTo("John Doe");
        assertThat(savedPerson.getBirthDate()).isEqualTo(LocalDate.of(1990, 1, 1));

        // Verify retrieval with PersonRepository
        Optional<Person> foundWithPersonRepo = personRepository.findById(savedClient.getId());
        assertThat(foundWithPersonRepo).isPresent();
        assertThat(foundWithPersonRepo.get().getName()).isEqualTo("John Doe");
    }

    @Test
    public void testFindAllClientsIncludesPerson() {
        // Create a Person
        Person person = Person.builder()
                .name("John Doe")
                .phone("+33612345678")
                .email("john@example.com")
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        // Persist entity
        entityManager.persist(person);
        entityManager.flush();
        entityManager.clear();

        // Retrieve all clients
        List<Client> allClients = clientRepository.findAll();

        // Assert results include our Person
        assertThat(allClients).isNotEmpty();
        assertThat(allClients.stream().anyMatch(c ->
                c instanceof Person && c.getName().equals("John Doe")
        )).isTrue();
    }
}
