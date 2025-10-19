package com.barenne.insurance_manager_api.service;

import com.barenne.insurance_manager_api.model.Client;
import com.barenne.insurance_manager_api.model.Company;
import com.barenne.insurance_manager_api.model.Person;
import com.barenne.insurance_manager_api.repository.ClientRepository;
import com.barenne.insurance_manager_api.repository.CompanyRepository;
import com.barenne.insurance_manager_api.repository.PersonRepository;
import com.barenne.insurance_manager_api.utils.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final PersonRepository personRepository;
    private final CompanyRepository companyRepository;
    private final ContractService contractService;

    @Autowired
    public ClientServiceImpl (
            ClientRepository clientRepository,
            PersonRepository personRepository,
            CompanyRepository companyRepository,
            ContractService contractService
    ) {
        this.clientRepository = clientRepository;
        this.personRepository = personRepository;
        this.companyRepository = companyRepository;
        this.contractService = contractService;
    }

    @Override
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    @Override
    public Client getClientById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", id));
    }

    @Override
    @Transactional
    public void deleteClient(Long id) {
        Client client = getClientById(id);

        //Update all the endDate of all contracts
        contractService.terminateAllClientContracts(id, LocalDate.now());

        //Delete the client
        clientRepository.deleteById(id);
    }

    @Override
    public Person createPerson(Person person) {
        return personRepository.save(person);
    }

    @Override
    @Transactional
    public Person updatePerson(Long id, Person personDetails) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Person", id));

        person.setName(personDetails.getName());
        person.setEmail(personDetails.getEmail());
        person.setPhone(personDetails.getPhone());

        return personRepository.save(person);
    }

    @Override
    public Company createCompany(Company company) {
        if (companyRepository.existsByCompanyIdentifier(company.getCompanyIdentifier())) {
            throw new IllegalArgumentException("A company with this identifier already exists.");
        }
        return companyRepository.save(company);
    }

    @Override
    @Transactional
    public Company updateCompany(Long id, Company companyDetails) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company", id));

        company.setName(companyDetails.getName());
        company.setEmail(companyDetails.getEmail());
        company.setPhone(companyDetails.getPhone());

        return companyRepository.save(company);
    }
}
