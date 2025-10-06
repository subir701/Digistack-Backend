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

- Available endpoints:
    - `/api/keywords/fetch` - POST keyword data queries
    - `/api/keywords/history` - POST get cache keyword history within 12 hours

***

## Contact and Support

For any questions or issues, please open a GitHub ticket or contact the development team.

***

This concludes the project README summarizing the system, tools, and roadmap.
Would you like me to generate a ready-to-use Dockerfile or a sample batch processing design next?

