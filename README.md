## Project Overview

This project is a **Keyword Research Backend API** that provides keyword data enriched with metrics like competition, search volume, bid prices, and monthly trends. It efficiently handles user quotas and rate limits while caching keyword data at multiple levels (local cache, Redis server cache) for fast responses. The backend securely exposes REST endpoints to clients with JWT-based authentication and role-based access control.

***

## What We Built

- **Keyword Data Fetching API**:
Accepts keyword search requests and returns keyword metrics by fetching from cache or an external keyword data API.
- **Multi-level Caching**:
    - Local cache check (user-specific)
    - Redis cache for shared server-side caching with 12-hour TTL
    - Locking via Redis distributed locks to prevent cache stampedes
- **Quota and Rate Limiting**:
Enforces daily and per-minute request limits using Lua scripts in Redis for atomic counter and validation.
- **Global Usage Tracking**:
Tracks global monthly keyword usage metrics persisted to a database with resilience and retry logic.
- **Asynchronous Processing**:
Employs async service methods via `CompletableFuture` to handle parallelism, wrapped with Spring Security `DelegatingSecurityContextExecutor` to maintain authentication context.
- **Secure REST APIs**:
Spring Security-based JWT authentication with role-based endpoint restrictions and stateless session management.

***

***

## Tools and Versions Used

| Tool/Technology | Version / Info |
| :-- | :-- |
| Java | 17.0.16 |
| Spring Boot | 3.5.5 |
| Spring Security | 6.2.10 |
| Spring Data Redis | Integrated with Lettuce driver |
| Redis | Server 7.x |
| PostgreSQL | 14.19 |
| Hibernate ORM | 6.6.26.Final |
| Lombok | 1.18.x |
| Jackson Databind | 2.19.2 |
| Maven / Gradle | Gradle Wrapper |
| Docker | To be added (future upgrade) |


***
## Architecture
Cache-Aside + Token Bucket (Rate Limiting) + Asynchronous Queue Pattern

***

## 🌐 Frontend Repository

The frontend is hosted in a separate repository built using **React.js / TypeScript**.

👉 **[Visit DigiStack Frontend Repository »](https://github.com/your-frontend-repo-link)**

***

## Key Features

- **Robust Caching**: Reduces external API hits, speeds up responses.
- **Quota Enforcement through Redis Lua Scripts**: Accurate, atomic user request limits.
- **Async with Security Context Propagation**: Preserves JWT context seamlessly during async calls.
- **Distributed Locking**: Prevents duplicate external API calls for same keyword.
- **Global Monthly Usage Persistence**: Resilient DB writes with retries and rollback.
- **Comprehensive Security Configuration**: CORS, stateless JWT, role-based access control.

***

## Future Upgrades

- **Dockerization**:
Containerize entire backend stack (Spring Boot app, Redis, PostgreSQL) using Docker Compose or Kubernetes for scalability and ease of deployment.
- **Advanced Parallel Processing and Batching**:
Implement batch processing in service layer where multiple user keyword requests are combined into batch calls to external API, then distribute the response data back to individual requests efficiently.
- **Enhanced Error Handling and Circuit Breaking**:
Use Resilience4j or Spring Retry for external API calls to prevent outages or cascading failures.
- **Monitoring and Metrics**:
Add monitoring via Micrometer/Prometheus/Grafana for cache hit rates, quota usage, service health.
- **CI/CD Pipelines**:
Automate builds, tests, and deployments integrated with Docker images.

***

## Getting Started

### Prerequisites

- Java 17
- Redis server
- PostgreSQL database
- Gradle build system


### Running

```bash
./gradlew bootRun
```

##API endpoints:
### Keyword Cache

- `POST /api/keywords/fetch`
Fetch keyword data with quota checks and caching.
- `POST /api/keywords/history`
Retrieve user keyword cache history within the last 12 hours.


### User Quota

- `POST /api/quota/{userId}/validate`
Validate and consume quota for user.
- `POST /api/quota/{userId}/revert`
Revert previously consumed quota for user.
- `GET /api/quota/{userId}`
Get current quota usage for user.


### User

- `POST /api/users/register`
Register new user (with password encryption).
- `POST /api/users/login`
Authenticate user and generate JWT.
- `PUT /api/users/{id}/email`
Update user email.
- `GET /api/users/{email}`
Fetch user details by email.


### Monthly Usage

- `POST /api/monthly-usage`
Create monthly usage record.
- `PUT /api/monthly-usage/{id}/increment`
Increment usage credit.
- `GET /api/monthly-usage/{id}`
Get usage record by ID.
- `GET /api/monthly-usage/month/{month}`
Get usage by month.


### Global Counter

- `POST /api/global/increment`
Increment global keyword usage.
- `GET /api/global/usage`
Get current month global usage.


### External API Proxy

- `GET /api/external/keyword`
Proxy to external keyword API with rate limiting.

***


## Contact and Support

For any questions or issues, please open a GitHub ticket or contact the development team.


