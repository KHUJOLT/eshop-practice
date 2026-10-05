# Eshop Practice Backend

A learning project implementing an online store backend with Java and Spring Boot.

## Tech Stack

- Java 21 and Spring Boot 4.1.1
- Spring Security with JWT authentication
- Spring Data JPA
- PostgreSQL 16 and Flyway
- Docker Compose
- JUnit, Mockito, and Maven Wrapper

## Features

- User registration and login with BCrypt password hashing
- Role-based access control: USER and ADMIN
- Product and category CRUD
- Product filtering by name and category, pagination, and sorting
- Request validation and consistent API error responses
- Versioned database migrations
- Persistent shopping cart for each authenticated user
- Stock validation and cart totals calculated using current prices
- Browser interface for API testing and basic store management

## Requirements

- JDK 21
- Docker with Docker Compose

A separate Maven installation is not required. Run commands below from the project root in PowerShell.

## Local Setup

### 1. Start PostgreSQL

```powershell
docker compose up -d
```

PostgreSQL is available at `localhost:5432`.

### 2. Configure Environment Variables

| Variable | Description |
|---|---|
| `DB_PASSWORD` | Password matching the local PostgreSQL container configuration |
| `JWT_SECRET` | Base64-encoded random signing key containing 32 bytes |

Optional development settings:

| Variable | Default |
|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/eshop-khujolt` |
| `DB_USERNAME` | `eshop-khujolt_user` |
| `JWT_EXPIRATION` | `3600000` milliseconds |
| `PORT` | `8080` |

In IntelliJ IDEA, open **Run → Edit Configurations → Environment variables**.

Generate a signing key in PowerShell:

```powershell
$key = New-Object byte[] 32
$rng = [System.Security.Cryptography.RandomNumberGenerator]::Create()
$rng.GetBytes($key)
[Convert]::ToBase64String($key)
$rng.Dispose()
```

Use the generated value as `JWT_SECRET`.

### 3. Start the Application

Run `EshopKhujoltBackendApplication` in IntelliJ IDEA, or use PowerShell:

```powershell
$env:DB_PASSWORD = Read-Host "Local database password"
$env:JWT_SECRET = Read-Host "JWT signing key"
.\mvnw.cmd spring-boot:run
```

The default profile is `dev`. The API and browser interface are available at:

```text
http://localhost:8080/
```

Flyway applies migrations at startup. Hibernate validates the schema using `ddl-auto=validate`.

## Profiles

| Profile | Purpose |
|---|---|
| `dev` | Local development with SQL logging enabled |
| `test` | Spring tests using a separate database |
| `prod` | Deployment with database credentials supplied through environment variables |

Activate production with `SPRING_PROFILES_ACTIVE=prod`. Required variables are `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, and `JWT_SECRET`.

Docker Compose is started manually; automatic Compose integration is disabled in the application configuration.

## Browser Interface

Open `http://localhost:8080/` with the application running.

The interface supports registration, login, product browsing and search, shopping cart management, ADMIN product and category management, and inspection of API responses.

The page is located at `src/main/resources/static/index.html`.

Spring Security must allow public access to `/` and `/index.html`. Protected API endpoints still require authentication.

The JWT is stored in memory within the browser tab. Reloading the page requires logging in again. When served by the same application, the interface requires no separate frontend server or CORS configuration.

## Testing

### Shopping Cart Unit Tests

```powershell
.\mvnw.cmd "-Dtest=CartServiceTest" test
```

These Mockito tests do not require PostgreSQL or a Spring context. They verify that repeated additions increase quantity and recalculate totals, and that requests exceeding stock leave the existing quantity unchanged.

Database locking and HTTP authentication require separate integration tests.

### Full Test Suite

Start PostgreSQL and create the test database once:

```powershell
docker exec eshop-khujolt-postgres createdb -U eshop-khujolt_user eshop-khujolt-test
```

If it already exists, skip creation. Then run:

```powershell
$env:TEST_DB_PASSWORD = Read-Host "Test database password"
.\mvnw.cmd test
```

Spring context tests activate the `test` profile. The test profile uses a fixed signing key exclusively for testing.

Environment variables configured for the application in IntelliJ are not automatically available in terminal sessions or separate test run configurations.

## Build

With PostgreSQL running and `TEST_DB_PASSWORD` configured:

```powershell
.\mvnw.cmd package
```

This runs the tests and creates the executable JAR in `target/`.

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

Example filtering, pagination, and sorting:

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

### Shopping Cart

All cart endpoints require authentication. The current user is identified from the JWT subject; requests do not accept a user ID.

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/cart` | Retrieve the current user's cart |
| POST | `/api/cart/items` | Add a product or increase its quantity |
| PUT | `/api/cart/items/{productId}` | Set the product quantity |
| DELETE | `/api/cart/items/{productId}` | Remove a product |
| DELETE | `/api/cart` | Clear the cart |

Add an item:

```json
{
  "productId": 1,
  "quantity": 2
}
```

Update its quantity:

```json
{
  "quantity": 3
}
```

Quantities must be between 1 and 999. Repeated additions increase the existing quantity; PUT replaces it.

Cart changes validate quantities against current stock. The cart does not reserve stock. Prices and totals use current product prices. If stock drops below the quantity already in the cart, its item response reports `available: false`.

Each user has an independent cart that persists across application restarts. Clearing an empty cart returns `204 No Content`.

Deleting a product referenced by a cart is currently blocked by a foreign key constraint.

### Admin

| Method | Endpoint | Access |
|---|---|---|
| GET | `/api/admin/hello` | ADMIN |

## Authentication

Log in through `/api/auth/login` and include the returned token in protected requests:

```http
Authorization: Bearer <token>
```

Roles are stored in the JWT when it is issued. After changing a user's role in the database, log in again to obtain a new token. Changing the signing key invalidates previously issued tokens.

## Database Migrations

Migration files are located in `src/main/resources/db/migration`.

| Migration | Purpose |
|---|---|
| `V1__initial_schema.sql` | Create users, categories, and products |
| `V2__add_product_constraints.sql` | Require product categories, validate prices and stock, and index category references |
| `V3__add_cart_tables.sql` | Create carts and cart items with ownership, quantity, and uniqueness constraints |

New databases execute all migrations automatically.

An existing database matching the V1 schema requires a one-time baseline at version `1`. After verifying the database connection, schema, and backup, temporarily enable:

```properties
spring.flyway.baseline-on-migrate=true
spring.flyway.baseline-version=1
```

After successful initialization, restore:

```properties
spring.flyway.baseline-on-migrate=false
```

Do not modify migrations that have already been applied. Add subsequent schema changes as new versioned files.

## Configuration and Secrets

Do not commit real passwords, JWT signing keys, or database backups. Docker Compose credentials are intended for local development; use separate credentials for deployment.

A `.env` file is not automatically loaded by Spring Boot. Supply application variables through the shell, IDE, or deployment environment.

## Roadmap

- Order placement and order history
- Transactional stock management
- Product deactivation instead of physical deletion
- PostgreSQL integration tests with Testcontainers
- Continuous integration
- API documentation
- Further storefront and administration interface improvements
