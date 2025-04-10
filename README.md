# Banking Application API

This project is a simple banking application API that allows for account management and transactions.

## Features

- Create and view accounts
- Make transactions between accounts
- View monthly statements

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
