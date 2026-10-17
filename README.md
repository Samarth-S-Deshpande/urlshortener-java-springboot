# URL Shortener — Java Spring Boot

A URL Shortener application that converts long URLs into compact, shareable links. Built to demonstrate Java backend development, REST API design, database persistence, caching, and containerization.

## Features

- Generate random six-character alphanumeric short codes
- Create short URLs through a REST API
- Redirect users to the original URL
- Persist URL records and click counts in MySQL
- Cache URL lookups using Redis
- Track clicks and retrieve URL statistics
- Validate input and handle application errors
- Run the application and its dependencies using Docker Compose
- Monitor application health using Spring Boot Actuator

## Tech Stack

- **Language:** Java 17
- **Framework:** Spring Boot
- **API:** REST, HTTP
- **Database:** MySQL
- **ORM:** Spring Data JPA, Hibernate
- **Caching:** Redis
- **Build Tool:** Maven
- **Containerization:** Docker, Docker Compose
- **Development Tools:** IntelliJ IDEA, Git, GitHub, Postman

## Architecture

1. The client submits a long URL to the Spring Boot REST API.
2. The service generates a short code and stores the URL in MySQL.
3. Redis caches URL lookups to reduce repeated database reads.
4. When a short link is opened, the service resolves the code and returns an HTTP redirect.
5. The click count is updated, and URL statistics can be retrieved through the API.

## API Endpoints

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/urls` | Create a short URL |
| GET | `/{code}` | Redirect to the original URL |
| GET | `/api/urls/{code}/stats` | Retrieve URL statistics |
| GET | `/actuator/health` | Check application health |

## Run Locally

### Prerequisites

- Java 17
- Docker Desktop with Docker Compose
- Git

### Start the application

Configure the database password using an environment variable, then start the services:

```powershell
$env:DB_PASSWORD = "your-local-password"
docker compose up -d --build
```

The application is available at:

`http://localhost:8081/`

The frontend lets you enter a long URL, generate a short link, copy it, and open it.

## Example API Request

```http
POST /api/urls
Content-Type: application/json
```

```json
{
  "originalUrl": "https://www.example.com"
}
```

The API returns the generated short code, short URL, and original URL.

## Engineering Concepts Demonstrated

- RESTful API development
- Layered application architecture
- Database persistence with JPA
- Cache-aside caching with Redis
- URL validation and error handling
- HTTP redirect handling
- Docker-based local development

## Future Improvements

- Automated unit and integration tests
- Database migration management
- CI/CD pipeline
- Public cloud deployment
- API rate limiting and abuse protection

## Author

**Samarth Deshpande**

Java Backend Development | Spring Boot | REST APIs | MySQL | Redis