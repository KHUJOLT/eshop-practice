# Eshop Practice Backend

A learning project implementing an online store backend with Java and Spring Boot.

## Tech Stack

- Java 21
- Spring Boot 4.1.1
- Spring Security with JWT authentication
- Spring Data JPA
- PostgreSQL 16
- Flyway
- Docker Compose
- JUnit and Mockito
- Maven Wrapper

## Features

- User registration and login
- BCrypt password hashing
- JWT authentication
- Role-based access control: USER and ADMIN
- Product and category CRUD
- Product filtering by name and category
- Pagination and sorting
- Request validation
- Consistent API error responses
- Versioned database migrations

## Requirements

- JDK 21
- Docker with Docker Compose

A separate Maven installation is not required.

## Local Setup

Run the following commands from the project root.

### 1. Start PostgreSQL

```powershell
docker compose up -d
```

The database is available at `localhost:5432`.

### 2. Configure Environment Variables

Set these variables in your application run configuration:

| Variable | Description |
|---|---|
| `DB_PASSWORD` | Password matching the PostgreSQL container configuration |
| `JWT_SECRET` | Base64-encoded random signing key containing 32 bytes |

Optional variables:

| Variable | Default |
|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/eshop-khujolt` |
| `DB_USERNAME` | `eshop-khujolt_user` |
| `JWT_EXPIRATION` | `3600000` milliseconds |
| `PORT` | `8080` |

In IntelliJ IDEA, open **Run → Edit Configurations → Environment variables**.

Generate a JWT signing key in PowerShell:

```powershell
$key = New-Object byte[] 32
$rng = [System.Security.Cryptography.RandomNumberGenerator]::Create()
$rng.GetBytes($key)
[Convert]::ToBase64String($key)
$rng.Dispose()
```

Use the generated value as `JWT_SECRET`.

### 3. Start the Application

Run `EshopKhujoltBackendApplication` in IntelliJ IDEA.

Alternatively, set the environment variables in PowerShell and run:

```powershell
$env:DB_PASSWORD = Read-Host "Local database password"
$env:JWT_SECRET = Read-Host "JWT signing key"
.\mvnw.cmd spring-boot:run
```

The default profile is `dev`.

API base URL:

```text
http://localhost:8080
```

Flyway applies database migrations at startup. Hibernate validates the resulting schema using `ddl-auto=validate`.

## Profiles

| Profile | Purpose |
|---|---|
| `dev` | Local development with SQL logging enabled |
| `test` | Spring tests using a separate database |
| `prod` | Deployment with database credentials supplied through environment variables |

Activate the production profile with:

```text
SPRING_PROFILES_ACTIVE=prod
```

The `prod` profile requires:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `JWT_SECRET`

Docker Compose is started manually and is disabled within the application configuration.

## Testing

Start PostgreSQL before running tests.

Create the test database once:

```powershell
docker exec eshop-khujolt-postgres createdb -U eshop-khujolt_user eshop-khujolt-test
```

Set its password and run the tests:

```powershell
$env:TEST_DB_PASSWORD = Read-Host "Test database password"
.\mvnw.cmd test
```

Spring context tests activate the `test` profile. Mockito unit tests do not require a database.

The test profile uses a fixed signing key exclusively for testing.

Environment variables configured for the application in IntelliJ IDEA are not automatically available in terminal sessions or separate test run configurations.

## Build

With PostgreSQL running and `TEST_DB_PASSWORD` configured:

```powershell
.\mvnw.cmd package
```

This command runs the tests and creates the executable JAR in `target/`.

## API Endpoints

### Authentication

| Method | Endpoint | Access |
|---|---|---|
| POST | `/api/auth/register` | Public |
| POST | `/api/auth/login` | Public |

New users receive the `USER` role.

### Products

| Method | Endpoint | Access |
|---|---|---|
| GET | `/api/products` | Public |
| GET | `/api/products/{id}` | Public |
| POST | `/api/products` | ADMIN |
| PUT | `/api/products/{id}` | ADMIN |
| DELETE | `/api/products/{id}` | ADMIN |

Example query with filtering, pagination, and sorting:

```text
GET /api/products?categoryId=1&name=mouse&page=0&size=5&sort=price,asc
```

Page numbering starts at `0`.

### Categories

| Method | Endpoint | Access |
|---|---|---|
| GET | `/api/categories` | Public |
| GET | `/api/categories/{id}` | Public |
| POST | `/api/categories` | ADMIN |
| PUT | `/api/categories/{id}` | ADMIN |
| DELETE | `/api/categories/{id}` | ADMIN |

Categories containing products cannot be deleted.

### Admin

| Method | Endpoint | Access |
|---|---|---|
| GET | `/api/admin/hello` | ADMIN |

## Authentication

Log in through `/api/auth/login` and include the returned token in protected requests:

```http
Authorization: Bearer <token>
```

Roles are stored in the JWT when it is issued. After changing a user's role in the database, log in again to obtain a new token.

Changing the signing key invalidates previously issued tokens.

## Database Migrations

Migration files are located in:

```text
src/main/resources/db/migration
```

Current migrations:

| Migration | Purpose |
|---|---|
| `V1__initial_schema.sql` | Create users, categories, and products |
| `V2__add_product_constraints.sql` | Require product categories, validate prices and stock, and index category references |

New databases execute both migrations automatically.

An existing database matching the V1 schema requires a one-time baseline at version `1`. After verifying the database connection, schema, and backup, temporarily enable:

```properties
spring.flyway.baseline-on-migrate=true
```

After successful initialization, restore:

```properties
spring.flyway.baseline-on-migrate=false
```

Do not modify migrations that have already been applied. Add subsequent schema changes as new versioned files.

## Configuration and Secrets

Do not commit real passwords, JWT signing keys, or database backups.

The repository's Docker Compose credentials are intended for local development. Use separate credentials for deployment.

A `.env` file is not automatically loaded by the Spring Boot application. Supply application variables through the shell, IDE, or deployment environment.

## Roadmap

- Shopping cart
- Order placement and order history
- Transactional stock management
- PostgreSQL integration tests with Testcontainers
- Continuous integration
- API documentation
- Storefront and administration interface