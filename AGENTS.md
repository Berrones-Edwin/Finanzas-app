# AGENTS.md

Spring Boot REST backend for a personal finance app. Package root `com.bitly`, artifact `Bitly-Clone`. Java 25 / Spring Boot 4.0.6 (parent), Maven wrapper only (no system mvn assumed).

## Rules
- Do not run a dev server or run builds unless expressly requested.
- Do not delete files or folders without confirmation.
- Do not install dependencies without asking.
- Do not perform irreversible actions without confirmation.

## Local Resources & Skills
- **Skills Directory:** Consult and trigger custom skills in `./.opencode/skills/` based on the task to be performed:
  - Use `git-best-practices` for code hygiene, `.gitignore` validation, and Conventional Commit formatting.
  - Use `create-pull-request` when ready to push changes and open a PR.

## Branching & Workflow Rules
- **NEVER implement changes directly on `main` or `master`.**
- Before creating or modifying any code for a new feature, fix, or refactor:
  1. Check the current git branch (`git status` or `git branch --show-current`).
  2. If on `main` or `master`, automatically create and switch to a new branch following the format: `<type>/<short-kebab-description>` (e.g., `feat/add-user-login`, `fix/postgres-connection-leak`).
  3. Perform all work exclusively on the new feature branch.
- **Git Hygiene:** Never stage or commit temporary files, environment variables, or build outputs (`logs/`, `.vscode/`, `target/`, `.next/`, `.tmp/`, `.env*`, `application-local.*`).

## Run / build

- Start: `./mvnw spring-boot:run` (from project root). Hot reload via `.vscode` launch config also works.
- Build/test/package: `./mvnw test`, `./mvnw package`.

## Database — the big gotcha

- The app connects to **PostgreSQL**, not MySQL: `src/main/resources/application.properties` → `jdbc:postgresql://localhost:5432/financial_app`, user `postgres` / password `password`.
- `docker-compose.yml` ONLY starts a **MySQL** container by mistake/leftover; it is NOT used by the app. Do not follow it expecting the app to work. The real dev flow (from `improves/commands.md`) is: `sudo systemctl start postgresql`, then `./mvnw spring-boot:run`.
- Schema comes exclusively from Flyway migrations in `src/main/resources/db/migration/` (V1, V2...). Hibernate is `ddl-auto=validate` with Flyway `baseline-on-migrate=true` — never let Hibernate create or change tables; add new schema as a new `V*__...sql`.
- Migrations are PostgreSQL-specific (e.g. `GENERATED ALWAYS AS IDENTITY`, partial indexes) — will not run on MySQL.

## Tests

- Only one test exists: `src/test/java/com/bitly/BitlyCloneApplicationTests.java`, a `@SpringBootTest` context-load that needs a live PostgreSQL at the configured URL. There is no test DB config or Testcontainers — do not assume tests are isolated/hermetic.

## Caching (Caffeine)

Dashboard endpoints are cached (`@Cacheable`, `@EnableCaching`): caches defined in `CaffeineConfig` (`dashboard-summary`, `dashboard-by-account`, `dashboard-by-category`, `dashboard-montly-trends`), 2-min expiry, `maximumSize(1)`, plus a custom `CacheErrorHandler`. When adding/updating dashboards, be aware of the 2-min staleness window.

## API conventions

- All endpoints under `/api/v1/*` (e.g. `/api/v1/auth`, `/api/v1/accounts`, `/api/v1/transactions`, `/api/v1/budgets`, `/api/v1/dashboard`, `/api/v1/transfers`, `/api/v1/categories`, `/api/v1/users`).
- JWT auth (jjwt 0.13.0); `/api/v1/auth/login`, `/register` permit all; everything else requires a `Bearer` token. CSRF disabled, stateless sessions.
- Swagger/OpenAPI: `http://localhost:8080/swagger-ui/index.html` and `/v3/api-docs`.
- Manual HTTP smoke tests live in `api/*.http` (VS Code REST client) with pre-baked JWTs — handy, but the tokens eventually expire.
- `GlobalExceptionHandler` uses RFC 9457 Problem Details. DTOs are separate from entities; always map, never return entities.

## Style / conventions

- Lombok enabled (`@Slf4j` etc.) — check for it before writing manual getters/loggers.
- Logging guidance (from `improves/logging.md`): log meaningful business/security/error events at the **service** layer, not per-method in controllers; use INFO for completed ops, WARN for expected-but-notable (failed login, insufficient funds, budget exceeded), ERROR only for real system failures; never log passwords or full JWTs.
- Rotating file logs go to `logs/` (gitignored) via `logback-spring.xml`; `com.bitly` logs at DEBUG.