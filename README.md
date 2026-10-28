# SupportFlow AI

**An AI-assisted customer support backend built with Java and Spring Boot.**

SupportFlow AI is an evolving backend project for managing customer-support tickets. The current milestone focuses on a clean REST API, persistence, validation, and pagination. AI-powered triage and retrieval are planned next; they are not yet implemented.

## Current features

- Create support tickets through a REST endpoint.
- Retrieve a ticket by ID.
- List tickets with page-based pagination and newest-first sorting.
- Filter tickets by status.
- Validate incoming request data and return structured API errors.
- Separate HTTP handling, service contracts/business logic, and persistence using a layered architecture.
- Persist ticket data in PostgreSQL with Spring Data JPA and Hibernate.
- Expose an application health endpoint through Spring Boot Actuator, if Actuator is enabled in the active configuration.

## Tech stack

- **Language:** Java 21
- **Framework:** Spring Boot
- **API:** Spring MVC / REST
- **Persistence:** Spring Data JPA, Hibernate
- **Database:** PostgreSQL
- **Build:** Maven Wrapper
- **Testing:** JUnit 5, Mockito, Spring MVC Test / MockMvc (test suite is being built out)

## API overview

Base URL for local development: `http://localhost:8080`

| Method | Endpoint | Purpose |
|---|---|---|
| `POST` | `/api/v1/tickets` | Create a ticket; returns `201 Created` and a `Location` header |
| `GET` | `/api/v1/tickets/{id}` | Retrieve a ticket by ID |
| `GET` | `/api/v1/tickets` | List tickets with pagination and an optional status filter |

### Create a ticket

```http
POST /api/v1/tickets
Content-Type: application/json
```

```json
{
  "subject": "Password reset email not received",
  "description": "The customer cannot receive the password reset email."
}
```

A successful request returns `201 Created`, a JSON ticket response, and a `Location` header identifying the newly created resource.

### Retrieve a ticket

```http
GET /api/v1/tickets/1
```

If the ID does not exist, the API returns `404 Not Found`.

### List and filter tickets

```http
GET /api/v1/tickets?page=0&size=10
GET /api/v1/tickets?status=OPEN&page=0&size=10
```

- `page` is zero-based and defaults to `0`.
- `size` defaults to `10` and is intended to be limited to `1–100`.
- `status` is optional. Supported values are `OPEN`, `IN_PROGRESS`, `RESOLVED`, and `CLOSED`.
- Results are sorted by creation time, newest first.

Ticket priorities currently include `LOW`, `MEDIUM`, `HIGH`, and `URGENT`. Categories include `GENERAL`, `ACCOUNT`, `BILLING`, `TECHNICAL`, and `DELIVERY`.

## Architecture

The code follows a layered structure:

```text
HTTP client (Postman / frontend)
          |
          v
TicketController  -- request mapping, HTTP status, response headers
          |
          v
TicketService     -- service contract
          |
          v
TicketServiceImpl -- application logic and transaction boundaries
          |
          v
TicketRepository  -- Spring Data JPA repository
          |
          v
PostgreSQL        -- persistent ticket data
```

DTOs form the API boundary so persistence entities do not need to be exposed directly. A global exception handler translates common application and validation errors into structured HTTP responses.

## Run locally

### Prerequisites

- JDK 21
- PostgreSQL running locally or reachable from your machine
- Git

### 1. Clone the repository

```bash
git clone https://github.com/GarvJaiswal/supportflow-ai.git
cd supportflow-ai
```

### 2. Create a PostgreSQL database

Create a database for the application (for example, `supportflow`) using PostgreSQL or pgAdmin. Use the database name and credentials that match your local configuration.

### 3. Configure local credentials safely

Do **not** commit database passwords, API keys, tokens, or other secrets. Keep local settings in an ignored file such as `src/main/resources/application-local.properties`, or load credentials from environment variables. Activate the `local` Spring profile in your IntelliJ run configuration if your project uses that profile.

A typical datasource configuration uses these property names (adapt them to your project):

```properties
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/supportflow}
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.open-in-view=false
```

Set `DB_PASSWORD` in your local environment, or use your existing ignored local properties file. Never put real credentials in this README or in a tracked configuration file. Ensure the active local configuration points at your own database.

### 4. Run the application

On Windows PowerShell:

```powershell
$env:SPRING_PROFILES_ACTIVE = "local"
.\mvnw.cmd spring-boot:run
```

On macOS/Linux:

```bash
SPRING_PROFILES_ACTIVE=local ./mvnw spring-boot:run
```

If you configure the profile directly in IntelliJ, you can omit setting `SPRING_PROFILES_ACTIVE` in the terminal.

> Note: this project currently uses Hibernate schema generation with `ddl-auto=update` for development. Before production use, introduce and review database migrations rather than relying on automatic schema updates.

## Run tests

On Windows PowerShell:

```powershell
.\mvnw.cmd test
```

On macOS/Linux:

```bash
./mvnw test
```

The controller tests use a mocked service to check HTTP behavior without needing a running PostgreSQL instance. Database integration tests should be added separately to verify persistence against PostgreSQL.

## Roadmap

- Finish and expand automated controller and service tests.
- Add JWT-based authentication and authorization.
- Add ticket classification and priority suggestions using Spring AI.
- Add semantic ticket search with embeddings and pgvector.
- Add OpenAPI documentation and a repeatable API test collection.
- Containerize the app and database setup with Docker Compose.
- Add CI checks with GitHub Actions and deploy the application.

## Security notes

- Never commit `application-local.properties`, `.env` files, passwords, API keys, JWT secrets, or production credentials.
- Use environment variables or a secrets manager for deployment configuration.
- If a secret has ever been pushed to a remote repository, rotate it. Deleting the file in a later commit does not remove the secret from Git history.
- Do not expose Actuator's sensitive endpoints publicly in a deployed environment.

## Project status

This project is under active development. The current focus is building a reliable Java/Spring Boot backend foundation before layering in AI functionality.
