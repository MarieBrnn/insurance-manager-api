# insurance-manager-api
A REST API for managing clients (person and company) and their insurance contracts.

## Prerequisites

- Java 17 or higher
- Maven 3,8 or higher

## Installation and execution

1. Clone the repository :
```bash
git clone https://github.com/MarieBrnn/insurance-manager-api.git
cd insurance-manager-api
```

2. Build the project :
```bash
mvn clean install
```

3. Run the application :
```bash
mvn spring-boot:run
```

4. The API is now accessible at : http/localhost:8080

## Main Features
- Client management (persons and companies)
- Creation and management of insurance contracts
- Calculation of the total cost of active contracts per client
- Data validation (dates, emails, phone number, company identifiers)

## API Endpoints

### Clients

- `POST /api/clients/persons` : Create a person
- `POST /api/clients/companies` : Create a company
- `GET /api/clients` : Retrieve all clients
- `GET /api/clients/{id}` : Retrieve client by ID
- `PUT /api/clients/persons/{id}` : Update a person
- `PUT /api/clients/companies/{id}` : Update a company
- `DELETE /api/clients/{id}` : Delete a client (also terminates their contracts)

### Contracts
- `POST /api/contracts/clients/{clientId}` : Create a contract for a client
- `GET /api/contracts/clients/{clientId}` : Retrieve active contracts for a client
- `GET /api/contracts/clients/{clientId}?updatedAfter=2023-01-01T00:00:00` : Filter contracts by update date
- `GET /api/contracts/clients/{clientId}/totalCost` : Calculate the sum of costs for active contracts
- `PUT /api/contracts/{contractId}/cost` : Update a contract's cost amount

## Proof of Functionality

### Comprehensive Testing Strategy
The API has been thoroughly tested to ensure correct functionality: 
1. **Comprehensive test suite with over 90% code coverage :**
    - Unit tests for model, repositories, services, controllers and DTOs
    - Integration tests for end-to-end workflows
2. **Key business rules verified :**
    - Client inheritance (Person/Company) works correctly
    - Contract management (creation, update, filtering)
    - Automatic termination of contracts when a client is deleted
    - Data validation (emails, phone numbers, dates, cost amounts)
3. **Manual verification throug Postman confirms :**
    - All endpoints return correct status codes and responses
    - Error handling provides meaningful messages
    - Business rules are applied consistently



## Architecture and Design Choices 

This REST API follows a layered architecture adhering to SOLID principles : 

1. **Entity Layer :** Domain models with JPA annotations, using inheritance for client hierarchy  
2. **Repository Layer :** Spring Data JPA repositories with custom queries for efficient data retrieval.
2. **Service Layer :** Business logic encapsulation, transaction management, and domain rule enforcement (e.g., contract termination rules). 
3. **Controller Layer :** REST endpoints following HTTP standards, with appropriate status codes and error handling.
4. **DTO Later :** Data Transfer Objects with validation annotations, providing a clean API contract and separation from internal entities.
5. **Validations :** Implements server-side validation via Bean Validation to ensure data integrity. 

This architecture promotes maintainability through separation of concerns, testability via dependency injection, and scalability by keeping components focused and cohesive. Performance is optimized through targeted SQL queries for critical operations like calculating contract costs, while the inheritance strategy balances storage efficiency with query performance.
