# URL Shortener

A REST API for URL shortening built with Java and Spring Boot. The service stores links in PostgreSQL, generates short codes, and redirects users to the original URL. Links can have an optional expiration date.

## Features

- Six-character short codes containing letters and digits, generated with `SecureRandom`.
- Redirects to the original URL with `302 Found`.
- Optional link expiration.
- `404 Not Found` for unknown codes.
- `410 Gone` for expired or disabled links.
- Persistent storage in PostgreSQL through Spring Data JPA.

## Tech Stack

| Component | Technology |
| --- | --- |
| Language | Java 21 |
| Framework | Spring Boot 4.1.1 |
| HTTP API | Spring Web MVC |
| Data storage | PostgreSQL, Spring Data JPA |
| Validation | Jakarta Validation |
| Access configuration | Spring Security |
| Boilerplate reduction | Lombok |
| Build tool | Maven |

Spring Data Redis is included as a dependency but is not currently used. Click analytics have not been implemented yet.

## Run with Docker

Docker Compose builds the application and starts it together with PostgreSQL. You do not need Java or Maven installed locally.

### Prerequisites

- Docker with the Compose plugin, or Docker Desktop.
- The `Dockerfile`, `compose.yaml`, and `.dockerignore` files in the repository root, next to `pom.xml`.
- Port `8080` available on your computer.

### Start the Application

Clone the repository and open its directory:

```bash
git clone https://github.com/nvrsocial/url-shortener.git
cd url-shortener
```

Build the application image and start both services in the background:

```bash
docker compose up --build -d
```

The first build downloads the base images and Maven dependencies, so it may take a few minutes. Compose waits for PostgreSQL to pass its health check before starting the application. The application then needs a little time to finish starting.

Check the services and application logs:

```bash
docker compose ps
docker compose logs -f app
```

Press `Ctrl+C` to stop following the logs. The containers will keep running.

The API is available at `http://localhost:8080`. There is no web interface at the root URL; use the API endpoints below.

### Try the API

Create a short link:

```bash
curl -i -X POST http://localhost:8080/api/urls \
  -H 'Content-Type: application/json' \
  -d '{"originalUrl":"https://github.com/nvrsocial"}'
```

On Windows PowerShell, use `curl.exe` for the curl executable, or send the request with:

```powershell
Invoke-RestMethod -Method Post -Uri 'http://localhost:8080/api/urls' -ContentType 'application/json' -Body '{"originalUrl":"https://github.com/nvrsocial"}'
```

Copy the `code` from the response and open `http://localhost:8080/<code>` in your browser to test the redirect.

### Stop or Rebuild

Stop and remove the containers while keeping database data:

```bash
docker compose down
```

After changing the application code, rebuild and start it again:

```bash
docker compose up --build -d
```

To reset the demo completely, including all stored links:

```bash
docker compose down -v
```

The `-v` option deletes the database volume and its data.

### Configuration

The Compose file supplies database settings through environment variables, so a local `application.properties` file is not required for Docker. The application connects to PostgreSQL using the service name `db` rather than `localhost`.

The example database credentials and `ddl-auto=update` setting are intended for local demonstrations. Redis is not required by the current implementation.

### Troubleshooting

- **Port 8080 is already in use:** stop the application using that port, or change the mapping in `compose.yaml` to `"8081:8080"` and use `http://localhost:8081`. The response's `shortUrl` still uses the hardcoded port `8080`, so build the URL using port `8081` and the returned `code`.
- **The API is not responding:** check `docker compose ps` and `docker compose logs app db`; the application may still be starting or may have failed to build or connect.
- **Database authentication fails after changing credentials:** the existing database volume retains the original credentials. Restore them, or reset the demo with `docker compose down -v` if you can discard the stored links.

## Run Locally without Docker

You will need JDK 21, an installed version of Maven, and PostgreSQL. Docker is also required to start the database using the command below.

### 1. Clone the Repository

```bash
git clone https://github.com/nvrsocial/url-shortener.git
cd url-shortener
```

### 2. Start PostgreSQL

```bash
docker run --name url-shortener-db \
  -e POSTGRES_DB=url_shortener \
  -e POSTGRES_USER=url_shortener \
  -e POSTGRES_PASSWORD=local_password \
  -p 5432:5432 \
  -v url-shortener-data:/var/lib/postgresql/data \
  -d postgres:17
```

These settings are intended for local development. If PostgreSQL is already installed, create a database and use your own connection settings in the next step.

### 3. Configure the Application

The file `src/main/resources/application.properties` is excluded from Git. Create the directory:

```bash
mkdir -p src/main/resources
```

Add the following to `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/url_shortener
spring.datasource.username=url_shortener
spring.datasource.password=local_password
spring.jpa.hibernate.ddl-auto=update
server.port=8080
```

`ddl-auto=update` lets Hibernate create and update the table for local development. Use database migrations for production environments.

### 4. Run the Service

```bash
mvn spring-boot:run
```

The application will be available at `http://localhost:8080`.

The repository includes `mvnw` and `mvnw.cmd`, but the `.mvn` directory containing the Wrapper configuration is missing. These instructions therefore use an installed version of Maven.

## API

### Create a Short Link

```http
POST /api/urls
Content-Type: application/json
```

```json
{
  "originalUrl": "https://github.com/nvrsocial",
  "expiresAt": "2099-12-01T12:00:00"
}
```

| Field | Required | Constraints |
| --- | --- | --- |
| `originalUrl` | Yes | A nonblank string, up to 1024 characters |
| `expiresAt` | No | A future date in `yyyy-MM-dd'T'HH:mm:ss` format, without a time zone |

If `expiresAt` is omitted or set to `null`, the link does not expire. Expiration checks use the server's local time.

Example request without expiration:

```bash
curl -i -X POST http://localhost:8080/api/urls \
  -H 'Content-Type: application/json' \
  -d '{"originalUrl":"https://github.com/nvrsocial"}'
```

A successful request returns `201 Created`. Example response body; the generated code, ID, and timestamp will vary:

```json
{
  "id": 1,
  "code": "aB3xY9",
  "originalUrl": "https://github.com/nvrsocial",
  "shortUrl": "localhost:8080/aB3xY9",
  "createdAt": "2026-10-08T12:00:00",
  "expiresAt": null
}
```

Currently, `shortUrl` contains the fixed address `localhost:8080` without a scheme. Add `http://` to open the link. In other environments, use your server's address and the returned `code`.

### Follow a Short Link

```http
GET /{code}
```

Replace the example code with the code returned when creating a link:

```bash
curl -i http://localhost:8080/aB3xY9
```

A successful request returns `302 Found` with the original URL in the `Location` header.


## Current Limitations

- All requests are allowed without authentication; CSRF protection is disabled.
- `originalUrl` is validated for blank values and length, but its URL format and scheme are not checked.
- Code uniqueness is enforced by a database constraint; retrying generation after a collision is not implemented yet.
- Disabled links are rejected during redirects, but there is no dedicated API for changing `active`.
- The short link address is hardcoded; Redis caching and click analytics have not been implemented yet.
