# Banking Application API

This project is a Spring Boot banking application API that allows for account management and transactions, serving new and existing banking customers.

## Features

- User Authentication and Authorization
- Create and view accounts
- Make transactions between accounts
- View monthly statements
- Transfer money between accounts

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven

### Running the application

1. Clone the repository
2. Navigate to the project directory
3. Run `mvn spring-boot:run`

The application will start on `http://localhost:8080`

### API Documentation

Once the application is running, you can view the API documentation at:

`http://localhost:8080/swagger-ui.html`

## Project Structure

The project follows a standard Spring Boot structure:

- `src/main/java/com/cbdg/interview/application/`
   - `config/`: Configuration classes (OpenAPIConfig, SecurityConfig)
   - `controller/`: REST API controllers (AccountController, TransactionController)
   - `exception/`: Custom exception classes and global exception handler
   - `model/`: Entity classes (Account, Transaction)
   - `repository/`: Data access layer (AccountRepository, TransactionRepository)
   - `service/`: Business logic layer (AccountService, TransactionService)

## Database

The application uses an in-memory H2 database. You can access the H2 console at:
`http://localhost:8080/h2-console`

Use the following credentials:
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (leave blank)

## Authentication

The application uses OAuth 2.0 for authentication. A mock OAuth2 server is configured for testing purposes.

## Usage

Here are some example API calls:

1. Create an account:
   POST /api/accounts
   {
   "accountNumber": "1234567890",
   "accountType": "SAVINGS",
   "balance": 1000,
   "customerId": 1
   }

2. Get account details:
   GET /api/accounts/1

3. Make a transaction:
   POST /api/transactions/transfer?fromAccountId=1&toAccountId=2&amount=100

4. Get monthly statement:
   GET /api/transactions/statement/1?startDate=2023-06-01T00:00:00&endDate=2023-06-30T23:59:59

## Testing

To run the tests, execute:

mvn test
