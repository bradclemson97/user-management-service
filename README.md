# User Management Service

## 1. Overview
The **User Management Service** is a core microservice built with **Spring Boot 3.2.5**. It serves as the primary entry point for managing user identity, profile details, and coordinating access control across the application ecosystem.

This service is part of a distributed system and interacts directly with the following microservices:

* **Access Control Manager (ACM)**: Manages roles, permissions, and capabilities.
    > Repository: [https://github.com/bradclemson97/access-control-manager](https://github.com/bradclemson97/access-control-manager)
* **Keycloak Manager**: Handles identity provider integration and credential storage.
    > Repository: [https://github.com/bradclemson97/keycloak-manager](https://github.com/bradclemson97/keycloak-manager)

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
| `KEYCLOAK_ISSUER_URI` | Keycloak realm issuer URI for JWT validation | `http://localhost:9000/realms/system` |

## 6. Development & Build

### Prerequisites
* JDK 17
* Docker (for PostgreSQL and Keycloak)
* Maven (included wrapper)

To install Docker without Docker-Desktop:
```bash
brew install docker colima.
colima start
brew install docker-compose 
mkdir -p ~/.docker/cli-plugins  
ln -sfn $(brew --prefix)/opt/docker-compose/bin/docker-compose ~/.docker/cli-plugins/docker-compose
```

### 6.1 Start Infrastructure

Copy the local deployment `docker-compose.yml` from user-management-infra and place at the root of the `IdeaProjects` directory.

```bash
cd ~/IdeaProjects
docker compose up -d
```

This starts:
- **PostgreSQL 15** on `localhost:5432` — database `userdb`, credentials `postgres`/`password`
- **Keycloak 24** on `localhost:9000` — admin credentials `admin`/`admin`

Wait for Keycloak to report healthy (≈30–60 s) before starting the service. You can check with:

```bash
docker compose ps
```

> **Note**: The `system` realm must be created manually in Keycloak before JWT validation will work. See [Section 9](#9-manual-setup-steps).

### 6.2 Build & Run

* **Build the project**:
    ```bash
    ./mvnw clean package
    ```
* **Run locally**:
    ```bash
    ./mvnw spring-boot:run
    ```
* **Stop running**:
    ```bash
    lsof -ti :8080 | xargs kill -9
    ```

## 7. API Documentation
Detailed API endpoints and request/response models are available via Swagger UI once the service is running:
`http://localhost:8080/swagger-ui.html`

## 8. Architecture Diagrams
User Management Entity Relationship
<img width="7155" height="4071" alt="User Manament ERD" src="https://github.com/user-attachments/assets/2dceb944-2f86-4616-b597-60feeb4660a7" />

User Creation Process Visualisation 
<img width="7175" height="4700" alt="User Creation Process Visualization" src="https://github.com/user-attachments/assets/3c19854d-bfff-41f6-b85e-6459ca095d5d" />

## 9. Manual Setup Steps

These steps must be completed once after the services are first started. They cannot be automated because they depend on runtime state in Keycloak and external configuration.

### 9.1 Create the Keycloak Realm

Before any authentication can work, the `system` realm must exist in Keycloak.

1. Navigate to `http://localhost:9000` and sign in as `admin` / `admin`.
2. Click the realm dropdown (top-left) and select **Create realm**.
3. Set **Realm name** to `system` and click **Create**.

### 9.2 Bootstrap the Superuser in Keycloak

The Flyway migrations automatically create the superuser record in both the `user_management` and `access_control` databases with the fixed UUID `a0000000-0000-0000-0000-000000000001`. However, the matching Keycloak account must be created manually.

**Option A — via the Keycloak Manager API (local development)**

Call the Keycloak Manager's create-user endpoint, passing the pre-assigned system user ID so that all three systems share the same identity:

```
POST http://localhost:8210/v1/user
Content-Type: application/json

{
  "systemUserId": "a0000000-0000-0000-0000-000000000001",
  "email": "superuser@system.local",
  "firstName": "Super",
  "lastName": "User"
}
```

The response contains a generated temporary passphrase — save this, it is the superuser's initial login credential.

> **Production deployment:** The `keycloak-manager` service is not exposed outside the Docker network and its API requires a Bearer token, so Option A cannot be used directly. Instead, SSH into the server and create the user directly via kcadm.sh:
>
> ```bash
> # SSH into the production server
> ssh <user>@app.bradleyclemson.com
>
> # Authenticate kcadm (credentials are in user-management-infra/.env.prod)
> docker exec keycloak /opt/keycloak/bin/kcadm.sh config credentials \
>   --server http://localhost:8080 --realm master \
>   --user <KEYCLOAK_ADMIN> --password <KEYCLOAK_ADMIN_PASSWORD>
>
> # Create the superuser with the pre-assigned system user ID
> docker exec keycloak /opt/keycloak/bin/kcadm.sh create users -r system \
>   -s username=superuser@system.local \
>   -s email=superuser@system.local \
>   -s firstName=Super \
>   -s lastName=User \
>   -s enabled=true \
>   -s 'attributes.systemUserId=["a0000000-0000-0000-0000-000000000001"]'
>
> # Set a temporary password (note it down for first login)
> docker exec keycloak /opt/keycloak/bin/kcadm.sh set-password -r system \
>   --username superuser@system.local \
>   --new-password <choose-a-temporary-password> \
>   --temporary
> ```

**Option B — via the Keycloak Admin Console**

1. Navigate to `http://localhost:9000` and sign in as the Keycloak admin (`admin` / `admin`)
2. Select the correct realm (e.g. `system`).
3. Add systemUserId as an attribute in Realm Settings > User Profile, save display name as `${systemUserId}`.
4. Go to **Users** > **Add user**.
5. Set **Username** to `superuser@system.local`, **Email** to `superuser@system.local`, **First name** to `Super`, **Last name** to `User`.
6. Set the attribute **systemUserId** to `a0000000-0000-0000-0000-000000000001`. This must match the claim the security library reads (configured via `access-control.security.system-user-id-claim`).
7. Under the **Credentials** tab, set a temporary password (e.g. `admin` and note it down.

### 9.3 Register the UI Client in Keycloak

The `user-management-ui` requires an OIDC client registered in Keycloak before it can authenticate users.

1. In the Keycloak Admin Console, go to **Clients** > **Create client**.
2. Set **Client ID** to `user-management-ui`.
3. Enable **Client authentication** (confidential client).
4. Under **Valid redirect URIs**, add `http://localhost:3000/auth/callback`.
5. Under **Valid post logout redirect URIs**, add `http://localhost:3000`.
6. Under **Web origins**, add `http://localhost:3000`.
7. Save, then go to the **Credentials** tab and copy the **Client secret**.

> **Production deployment:** Replace every `http://localhost:3000` URI above with `https://app.bradleyclemson.com` (i.e. redirect URI becomes `https://app.bradleyclemson.com/auth/callback`, post-logout URI becomes `https://app.bradleyclemson.com`). The Keycloak Admin Console is accessible at `https://app.bradleyclemson.com/admin`.

### 9.4 Add the `systemUserId` Protocol Mapper to the UI Client

The `user-management-ui` client must include the `systemUserId` user attribute as a claim in its JWTs so the security library can look up the authenticated user in ACM.

1. In the Keycloak Admin Console, go to **Clients** and open `user-management-ui`.
2. Go to the **Client scopes** tab and click the dedicated scope (e.g. `user-management-ui-dedicated`).
3. Click **Add mapper** > **By configuration** > **User Attribute**.
4. Fill in the fields:

| Field | Value |
|---|---|
| Name | `systemUserId` |
| User Attribute | `systemUserId` |
| Token Claim Name | `systemUserId` |
| Claim JSON Type | `String` |
| Add to ID token | On |
| Add to access token | On |
| Add to userinfo | On |

5. Click **Save**.

After saving, newly issued JWTs from this client will contain `"systemUserId": "<uuid>"` as a top-level claim.

### 9.5 Register the Keycloak Manager Service Client

The `keycloak-manager` service calls the Keycloak Admin REST API using the OAuth2 client credentials grant. You must register its service account client and grant it permission to manage users.

**Create the client:**

1. In the Keycloak Admin Console, go to **Clients** > **Create client**.
2. Set **Client ID** to `system-manager-service`.
3. Set **Client authentication** to **On** (confidential).
4. Disable **Standard flow** and **Direct access grants** — only **Service accounts roles** is needed.
5. Save, then go to the **Credentials** tab and note (or set) the **Client secret**. The default configured value is `pdjofnwondhwinskwe`; update `KEYCLOAK_MANAGER_SECRET` in keycloak-manager's environment if you use a different value.

**Grant the service account admin permissions:**

6. Go to the **Service accounts roles** tab of the `system-manager-service` client. 
7. Look for the filter dropdown at the top-left of the dialog — it will say "Filter by realm roles"
8. Change it to "Filter by clients"
9. Now search for and assign **manage-users** and **manage-realm** — you'll see it listed under realm-management.

The service account for `system-manager-service` can now create and delete users in the `system` realm.

**Alternatively, using `kcadm.sh`:**

```bash
# Create the client
docker exec keycloak /opt/keycloak/bin/kcadm.sh config credentials \
  --server http://localhost:8080 --realm master --user admin --password admin

docker exec keycloak /opt/keycloak/bin/kcadm.sh create clients -r system \
  -s clientId=system-manager-service \
  -s enabled=true \
  -s clientAuthenticatorType=client-secret \
  -s secret=pdjofnwondhwinskwe \
  -s serviceAccountsEnabled=true \
  -s publicClient=false \
  -s standardFlowEnabled=false \
  -s directAccessGrantsEnabled=false

# Grant manage-users and manage-realm to the service account
docker exec keycloak /opt/keycloak/bin/kcadm.sh add-roles \
  -r system \
  --uusername service-account-system-manager-service \
  --cclientid realm-management \
  --rolename manage-users

docker exec keycloak /opt/keycloak/bin/kcadm.sh add-roles \
  -r system \
  --uusername service-account-system-manager-service \
  --cclientid realm-management \
  --rolename manage-realm
```

### 9.6 Create the `system-users` Group

All users created through the application are automatically added to the `system-users` group in Keycloak. The group must exist before the first user can be created.

1. In the Keycloak Admin Console, go to **Groups** > **Create group**.
2. Set **Name** to `system-users`.
3. Click **Create**.

**Alternatively, using `kcadm.sh`:**

```bash
docker exec keycloak /opt/keycloak/bin/kcadm.sh create groups -r system -s name=system-users
```

### 9.7 Configure the UI Environment

In the `user-management-ui` repository, create a `.env` file from the provided example:

```bash
cp .env.example .env
```

Then open `.env` and fill in the values:

```
PORT=3000
SESSION_SECRET=<generate a long random string>
KEYCLOAK_BASE_URL=http://localhost:9000
KEYCLOAK_REALM=system
KEYCLOAK_CLIENT_ID=user-management-ui
KEYCLOAK_CLIENT_SECRET=<paste the client secret from step 9.3>
APP_BASE_URL=http://localhost:3000
UMS_BASE_URL=http://localhost:8080
```

### 9.8 Set the Keycloak login theme

1. In the Keycloak Admin Console, go to **Realm Settings** → **Themes**
2. Set Login theme to **gov-uk**
3. Save
4. Check:  **Realm Settings** → **Login** → **Email as username** is checked/enabled  

### 9.9 Start the UI

```bash
cd ~/IdeaProjects/user-management-ui
npm install
npm run dev       # development (hot-reload)
# or
npm run build && npm start   # production
```

The UI will be available at `http://localhost:3000`.

### 10.0 First Login as Superuser

1. Open `http://localhost:3000` in a browser.
2. You will be redirected to the Keycloak login page.
3. Sign in with `superuser@system.local` and the temporary passphrase obtained in step 9.2.
4. Keycloak will prompt you to set a permanent password on first login.
5. After authentication you will be redirected back to the user list.

The superuser has all roles and capabilities pre-assigned via the ACM bootstrap migration, so every protected endpoint in UMS and ACM is accessible immediately after login.

To logout: http://localhost:3000/auth/logout 