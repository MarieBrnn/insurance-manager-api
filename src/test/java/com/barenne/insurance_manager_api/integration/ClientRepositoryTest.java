package com.barenne.insurance_manager_api.integration;

import com.barenne.insurance_manager_api.model.Person;
import com.barenne.insurance_manager_api.repository.ClientRepository;
import com.barenne.insurance_manager_api.repository.PersonRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

@DataJpaTest
@ActiveProfiles("test")
public class ClientRepositoryTest {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private PersonRepository personRepository;

    @Test
    public void testSavePerson() {
        Person person = new Person();
        person.setName("TestName");
        person.setEmail("test@email.com");
        person.setPhone("+41123456789");
        person.setBirthDate(LocalDate.of(1995, 1, 1));

        //Save
        Person savedPerson = personRepository.save(person);

        //Ensure the person has been persisted
        assertThat(savedPerson.getId()).isNotNull();
        assertThat(savedPerson.getName()).isEqualTo("TestName");

        //Ensure the person can be fetched
        Person foundPerson = personRepository.findById(savedPerson.getId()).orElse(null);
        assertThat(foundPerson).isNotNull();
        assertThat(foundPerson.getName()).isEqualTo("TestName");
    }
}
