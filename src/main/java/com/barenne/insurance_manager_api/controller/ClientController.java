package com.barenne.insurance_manager_api.controller;

import com.barenne.insurance_manager_api.dto.ClientDto;
import com.barenne.insurance_manager_api.dto.CompanyDto;
import com.barenne.insurance_manager_api.dto.DtoConverter;
import com.barenne.insurance_manager_api.dto.PersonDto;
import com.barenne.insurance_manager_api.model.Client;
import com.barenne.insurance_manager_api.model.Company;
import com.barenne.insurance_manager_api.model.Person;
import com.barenne.insurance_manager_api.service.ClientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService clientService;

    @Autowired
    public ClientController (ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public ResponseEntity<List<ClientDto>> getAllClients() {
        List<Client> clients = clientService.getAllClients();
        List<ClientDto> clientDtos = new ArrayList<>();

        for (Client client : clients) {
            if (client instanceof Person) {
                clientDtos.add(DtoConverter.convertToDto((Person) client));
            } else if (client instanceof Company) {
                clientDtos.add(DtoConverter.convertToDto((Company) client));
            }
        }
        return ResponseEntity.ok(clientDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientDto> getClientById(@PathVariable Long id) {
        Client client = clientService.getClientById(id);

        if (client instanceof Person) {
            return ResponseEntity.ok(DtoConverter.convertToDto((Person) client));
        } else if (client instanceof Company) {
            return ResponseEntity.ok(DtoConverter.convertToDto((Company) client));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/persons")
    public ResponseEntity<PersonDto> createPerson(@Valid @RequestBody PersonDto personDto) {
        Person person = DtoConverter.convertToEntity(personDto);
        Person createdPerson = clientService.createPerson(person);
        return new ResponseEntity<>(DtoConverter.convertToDto(createdPerson), HttpStatus.CREATED);
    }

    @PutMapping("/persons/{id}")
    public ResponseEntity<PersonDto> updatePerson(@PathVariable Long id, @Valid @RequestBody PersonDto personDto) {
        Person person = DtoConverter.convertToEntity(personDto);
        Person updatedPerson = clientService.updatePerson(id, person);
        return ResponseEntity.ok(DtoConverter.convertToDto(updatedPerson));
    }

    @PostMapping("/companies")
    public ResponseEntity<CompanyDto> createCompany(@Valid @RequestBody CompanyDto companyDto) {
        Company company = DtoConverter.convertToEntity(companyDto);
        Company createdCompany = clientService.createCompany(company);
        return new ResponseEntity<>(DtoConverter.convertToDto(createdCompany), HttpStatus.CREATED);
    }

    @PutMapping("/companies/{id}")
    public ResponseEntity<CompanyDto> updateCompany(@PathVariable Long id, @Valid @RequestBody CompanyDto companyDto) {
        Company company = DtoConverter.convertToEntity(companyDto);
        Company updatedCompany = clientService.updateCompany(id, company);
        return ResponseEntity.ok(DtoConverter.convertToDto(updatedCompany));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }














}
