package com.barenne.insurance_manager_api.service;

import com.barenne.insurance_manager_api.model.Client;
import com.barenne.insurance_manager_api.model.Company;
import com.barenne.insurance_manager_api.model.Person;

import java.util.List;

public interface ClientService {
    // Generic methods for all clients
    List<Client> getAllClients();
    Client getClientById(Long id);
    void deleteClient(Long id);

    // Person specific methods
    Person createPerson(Person person);
    Person updatePerson(Long id, Person personDetails);

    //Company specifi methods
    Company createCompany(Company company);
    Company updateCompany(Long id, Company companyDetails);
}
