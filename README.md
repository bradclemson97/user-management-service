# User Management Service

## 1. Overview
The **User Management Service** is a core microservice built with **Spring Boot 3.2.5**. It serves as the primary entry point for managing user identity, profile details, and coordinating access control across the application ecosystem.

The service manages two distinct responsibilities:
1.  **Identity Orchestration**: Coordinating user creation between local storage, **Keycloak** (for authentication), and the **Access Control Manager** (for permissions).
2.  **Profile Management**: Maintaining detailed user information such as names and contact details.

## 2. Technical Stack
* **Java**: 17
* **Framework**: Spring Boot 3.2.5
* **Database**: PostgreSQL (Hibernate 6.5.2.Final)
* **Migration**: Flyway
* **Communication**: Spring Cloud OpenFeign
* **Mapping**: MapStruct 1.5.5.Final
* **Security**: Spring Security 6.2.2
* **API Docs**: Springdoc OpenAPI (Swagger UI) 2.5.0

## 3. Data Architecture
The service operates on the `user_management` schema, utilizing temporal tables for auditing and history tracking.

### Key Entities
* **User (`users`)**: Stores the `system_user_id` (UUID), which serves as the global unique identifier shared with Keycloak and ACM.
* **User Details (`user_details`)**: Stores profile information including `first_name`, `last_name`, and `primary_email`.
* **Temporal History**: Every table has a corresponding `_history` table (e.g., `users_history`) managed by a versioning trigger and the `sys_period` column.

## 4. Business Logic: User Creation Flow
The service implements a distributed transaction pattern (Saga-like) for creating users:

1.  **Local Persist**: The user is first saved to the `user_management` database within a `@Transactional` block.
2.  **Keycloak Integration**: The service calls the `keycloak-manager` client to create the identity and receive a temporary password.
3.  **ACM Integration**: The service calls the `access-control-manager` to initialize the user's permission record.
4.  **Error Handling (Compensation)**: If the ACM creation fails, a catch block triggers a rollback in Keycloak (`rollbackCreateUser`) to ensure the identity provider stays in sync with the application state.

## 5. Configuration & Environment
Configuration is primarily handled via `application.yml`.

### Key Properties
| Property | Description | Default Value |
| :--- | :--- | :--- |
| `SERVER_PORT` | Port for the service | `8080` |
| `DB_HOST` | PostgreSQL Database Host | `localhost` |
| `DB_PORT` | PostgreSQL Database Port | `5432` |
| `KEYCLOAK_MANAGER_URL` | Base URL for the Keycloak Manager | `http://localhost:8210` |
| `ACM_URL` | Base URL for the Access Control Manager | `http://localhost:8130` |

## 6. Development & Build

### Prerequisites
* JDK 17
* PostgreSQL
* Maven (included wrapper)

### Commands
* **Build the project**: 
    ```bash
    ./mvnw clean package
    ```
* **Run locally**:
    ```bash
    ./mvnw spring-boot:run
    ```

## 7. API Documentation
Detailed API endpoints and request/response models are available via Swagger UI once the service is running:
`http://localhost:8080/swagger-ui.html`

## 8. Architecture Diagrams
User Management Entity Relationship
<img width="7155" height="4071" alt="User Manament ERD" src="https://github.com/user-attachments/assets/2dceb944-2f86-4616-b597-60feeb4660a7" />

User Creation Process Visualisation 
<img width="7175" height="4700" alt="User Creation Process Visualization" src="https://github.com/user-attachments/assets/3c19854d-bfff-41f6-b85e-6459ca095d5d" />


