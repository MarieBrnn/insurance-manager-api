# insurance-manager-api
A REST API for managing clients (person and company) and their insurance contracts.

## Prerequisites

- Java 17 or higher
- Maven 3,8 or higher

## Installation and execution

1. Clone the repository : 

git clone https://github.com/MarieBrnn/insurance-manager-api.git
cd insurance-manager-api

2. Build the project :
mvn clean install

3. Run the application :
mvn spring-boot:run

4. The API is now accessible at : http/localhost:8080

## Main Features
- Client management (persons and companies)
- Creation and management of insurance contracts
- Calculation of the total cost of active contracts per client
- Data validation (dates, emails, phone number)

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
- `GET /api/contracts/clients/{clientId}?updatedAfter=2023-01-01T00:00:00` : Filter by update date
- `GET /api/contracts/clients/{clientId}/totalCost` : Calculate the sum of costs for active contracts
- `PUT /api/contracts/{contractId}/cost` : Update a contract's cost

## Proof of Functionality
The API has been tested using unit and integration tests that verify key functionalities : 
- Unit tests for services and repositories
- Integration tests that verify the complete request flow

Additionally, the API has been manually tested using Postman to ensure all endpoints work correctly, validations are applied and business rules are respected, such as automatic termination of contracts when a client is deleted. 

## Architecture and Design Choices 

This REST API follows a layered architecture adhering to SOLID principles : 

1. **Persistence Layer :** Uses Spring Data JPA with H2 database, prividing a lightwheight yet peristent solution. Client inheritance (Person/Company) is implemented via a JOINED strategy, optimizing storage space while maintaining data model integrity. 
2. **Service Layer :** Encapsulates business logic and manages rules such as contract termination when client is deleted. Each service is autonomous and responsible for a single domain. 
3. **Controller Layer :** Exposes RESTful endpoints conforming to best practive (appropriate HTTP verb, relevant response codes). Uses DTOs to separate external representation from internal entities.
4. **Validations :** Implements server-side validation via Bean Validation to ensure data integrity. 

This architecture offers a clean separation of concerns, facilitating maintenance and extension. Performance is optimized through target SQL queries for critical operations such as calculating contract costs. The design focuses on scalability and maintainability and maintainability while ensuring efficient handling of business requirements. 
