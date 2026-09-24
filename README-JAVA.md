# PhoneBook — Java Spring Boot Backend

This version converts the original ASP.NET Core backend to Java Spring Boot while keeping the existing Vue frontend and `/api` contract.

## Containers
- `phonebook-java-postgres` — NEW PostgreSQL database, host port 5434
- `phonebook-java-backend` — NEW Spring Boot API, internal port 8080
- `phonebook-java-frontend` — NEW frontend/Nginx, host port 8082

Existing .NET and Python PhoneBook containers are not modified, stopped, renamed, or deleted.

## Start
```bash
docker compose up --build -d
```

Open:
- http://localhost:8082
- http://localhost:8082/api/health

Swagger is not bundled; the frontend uses the API directly.

Demo account:
`testuser@example.com` / `Test@12345`

The Java startup initializer creates the schema if needed and tops the demo account up to 1000 contacts.

## Stop only this Java stack
```bash
docker compose down
```

Do not use `docker compose down -v` unless you intentionally want to remove this Java database volume.
