# Logistics Dashboard API

Spring Boot 3 / Java 25 application providing dashboard summaries and all
persisted record details. The dashboard endpoints require a JWT bearer token.

## Configure and run

Set these environment variables before starting the application:

- `APP_AUTH_USERNAME`: login username.
- `APP_AUTH_PASSWORD_BCRYPT`: BCrypt hash of the login password.
- `JWT_SECRET_BASE64`: Base64-encoded random key of at least 32 bytes.

Generate a suitable JWT secret with:

```powershell
 $bytes = [byte[]]::new(32)
 [System.Security.Cryptography.RandomNumberGenerator]::Fill($bytes)
 [Convert]::ToBase64String($bytes)
```

Generate a BCrypt password hash using a BCrypt tool/library, then run:

```powershell
$env:APP_AUTH_USERNAME = "dashboard-user"
$env:APP_AUTH_PASSWORD_BCRYPT = "<bcrypt-hash>"
$env:JWT_SECRET_BASE64 = "<base64-secret>"
mvn spring-boot:run
```

The application stores records in a local H2 database at `./data/logistics`.
Hibernate creates or updates the `dashboard_records` table on startup. The
dashboard reads records from this table; it does not insert sample data. The
table remains empty until your logistics data integration populates it.

For local development only, launch the VS Code configuration **Run Logistics
Dashboard (Java 25)**. It activates the `local` profile, which binds the API
to `127.0.0.1` and bypasses authentication so the dashboard can be tested
without auth environment variables. The local login endpoint accepts any
non-blank username and password. Do not activate this profile outside local
development.

## API

### Login

`POST /api/auth/login`

```json
{
  "username": "dashboard-user",
  "password": "your-password"
}
```

Returns an access token and its expiry in seconds.

### Dashboard summary

`GET /api/dashboard/summary`

Example response:

```json
{
  "total": 12,
  "pending": 3,
  "inProgress": 4,
  "completed": 5
}
```

### Dashboard details

`GET /api/dashboard/details`

Returns all persisted records ordered newest first, including `id`, `tally`,
`vehicle`, `rps`, `weight`, `packages`, `assignedEmployee`, `dockName`,
`vehicleStatus`, `recordStatus`, and `createdAt`.

Pass the token from the login response in the `Authorization: Bearer <token>`
header for both dashboard endpoints. The summary lifecycle statuses are
`PENDING`, `IN_PROGRESS`, and `COMPLETED`; vehicle status is a separate record
field.
