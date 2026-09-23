# Spring Security for the employee REST API

## Lesson snapshot — 2026-09-22

This project uses Spring Boot **4.0.0**, targets **Java 25** in `pom.xml` (the observed test process used Java 26.0.1), and depends on Spring Security, Spring MVC, Spring Data JPA, Jackson, and MySQL. The lesson replaces Boot's single development user with three in-memory users, then defines method-and-path authorization for the employee API. The project is **in progress**: its current `cors` line leaves CSRF protection active, so write requests do not yet behave as the role table alone suggests.

Start here: [course map](../../README.md) · [cumulative review](../../COURSE_REVIEW.md).

## The mental model

**Authentication** answers “Who sent this request?” **Authorization** answers “May that user perform this operation?” Spring Security also supplies protections such as CSRF checking. The HTTP request goes through security filters before Spring MVC dispatches it to `EmployeeRestController`.

```text
client request
  -> security filters: CSRF check, HTTP Basic authentication, role authorization
  -> DispatcherServlet -> EmployeeRestController
  -> EmployeeServiceImpl -> EmployeeRepository -> MySQL
```

The checks are separate. A valid password establishes identity; it does not bypass CSRF or grant a missing role. A client can therefore see a failure even with Susan's correct credentials.

## Project map and startup path

| Source | Responsibility |
|---|---|
| [`pom.xml`](pom.xml) | Boot 4.0.0, Java 25 target, security/web/JPA dependencies |
| [`CruddemoApplication.java`](src/main/java/com/luv2code/springboot/cruddemo/CruddemoApplication.java) | `@SpringBootApplication` starts the context and scans the `com.luv2code.springboot.cruddemo` package tree |
| [`DemoSecurity.java`](src/main/java/com/luv2code/springboot/cruddemo/security/DemoSecurity.java) | Creates the user store and the HTTP security filter chain |
| [`EmployeeRestController.java`](src/main/java/com/luv2code/springboot/cruddemo/rest/EmployeeRestController.java) | Maps `/api/employees` and item URLs to CRUD handlers |
| [`EmployeeServiceImpl.java`](src/main/java/com/luv2code/springboot/cruddemo/service/EmployeeServiceImpl.java) | Delegates employee work to the repository |
| [`EmployeeRepository.java`](src/main/java/com/luv2code/springboot/cruddemo/dao/EmployeeRepository.java) | Spring Data JPA employee storage; **not** the authentication user store |
| [`application.properties`](src/main/resources/application.properties) | MySQL connection and older Boot user settings |
| [`CruddemoApplicationTests.java`](src/test/java/com/luv2code/springboot/cruddemo/CruddemoApplicationTests.java) | Empty `contextLoads()` test; checks startup only |

At startup Spring finds `DemoSecurity` because it is below the application class's package. `@Configuration` marks it as a source of bean definitions. Spring calls its two `@Bean` factory methods and registers their returned objects in the application context: an `InMemoryUserDetailsManager` and a `SecurityFilterChain`. The configuration class need not be injected into the controller. Spring Security itself consumes the beans by type. A `@Bean` method is a recipe called by Spring when it creates the bean; later authentication calls methods on the returned manager, not the factory method again. These beans have singleton scope by default. [Spring `@Bean` reference](https://docs.spring.io/spring-framework/reference/core/beans/java/bean-annotation.html)

`@Component`, `@Service`, and `@Repository` register instances of their annotated classes. A `@Bean` method registers its **return value**, which is useful when constructing and configuring a library type such as `InMemoryUserDetailsManager`.

## From default credentials to custom users

These are the stages discussed in this lesson:

| Stage | Active user source | Result |
|---|---|---|
| Only the security starter | Boot's development user store | Username `user`; a generated password is printed at startup |
| `spring.security.user.*` properties, before the custom bean | Boot's development user store configured by properties | Username `scott`; password from properties; no generated-password message |
| **Current source**, with the manager bean | `InMemoryUserDetailsManager` in `DemoSecurity.java:18-24` | `john`, `mary`, and `susan`; Boot backs off its user store |

The `spring.security.user.name` and `spring.security.user.password` lines are still active property assignments in `application.properties:8-9`, but they do not create another login user while the custom `UserDetailsService` bean exists. After a fresh application start, `scott` is absent from the current manager. [Boot 4.0 security reference](https://docs.spring.io/spring-boot/4.0/reference/web/spring-security.html)

| User | Example lesson password | Roles declared by `roles(...)` | Actual authorities |
|---|---|---|---|
| `john` | Lesson password in `DemoSecurity.java` | EMPLOYEE | `ROLE_EMPLOYEE` |
| `mary` | Lesson password in `DemoSecurity.java` | EMPLOYEE, MANAGER | `ROLE_EMPLOYEE`, `ROLE_MANAGER` |
| `susan` | Lesson password in `DemoSecurity.java` | EMPLOYEE, MANAGER, ADMIN | `ROLE_EMPLOYEE`, `ROLE_MANAGER`, `ROLE_ADMIN` |

`User.builder().username(...).password(...).roles(...).build()` creates a `UserDetails` value: a security account record with identity, stored password, authorities, and account flags. `new InMemoryUserDetailsManager(john, mary, susan)` stores those records in process memory. It does not write them to MySQL, and they are reconstructed at every startup. The `Employee` table stores business data, not these login accounts. [Spring Security `UserDetailsService`](https://docs.spring.io/spring-security/reference/7.0/servlet/authentication/passwords/user-details-service.html)

During a Basic-authenticated request, `BasicAuthenticationFilter` reads the `Authorization` header. Spring Security then looks up the username through the manager and checks the supplied password against the stored one. A successful authentication produces an `Authentication` with authorities for the authorization check. [HTTP Basic](https://docs.spring.io/spring-security/reference/7.0/servlet/authentication/passwords/basic.html)

### Password prefix `{noop}`

In `DemoSecurity.java:20-22`, the password starts with `{noop}`, which selects the no-operation password encoder; the suffix is stored as plain text. The prefix is an encoder identifier, not part of what the user types. The general stored format is `{id}encodedPassword`. Other supported identifiers include `{bcrypt}`, `{pbkdf2}`, `{scrypt}`, and `{argon2}`; some have versioned IDs. A prefix must match a configured encoder, and placing `{bcrypt}` before a plain password is invalid because the suffix must be a BCrypt hash. For production, generate a real adaptive password hash outside source code and store that hash. The current hard-coded lesson passwords are for local learning only. [Password storage](https://docs.spring.io/spring-security/reference/7.0/features/authentication/password-storage.html)

## What the filter-chain method builds

`DemoSecurity.java:27-44` defines the current HTTP security rules. Spring supplies `HttpSecurity` as a builder argument. `http.build()` returns a configured `SecurityFilterChain`; the `@Bean` annotation publishes that chain so Spring Security can apply its filters to incoming requests. This method runs while the application context is built, not once per HTTP request. [Security architecture](https://docs.spring.io/spring-security/reference/7.0/servlet/architecture.html)

`authorizeHttpRequests(configurer -> ...)` supplies a Java lambda. `configurer` is merely the local parameter name for an authorization rule builder. Each `requestMatchers(HttpMethod.X, path).hasRole(role)` pairs a method and path with a required authority. `hasRole("EMPLOYEE")` checks for `ROLE_EMPLOYEE`. Rules are checked in declaration order, and the first match applies. [Authorize requests](https://docs.spring.io/spring-security/reference/7.0/servlet/authorization/authorize-http-requests.html)

| Current matcher | Controller mapping | Required role | Users satisfying the role rule |
|---|---|---|---|
| `GET /api/employees` | `findAll()` | EMPLOYEE | John, Mary, Susan |
| `GET /api/employees/**` | `getEmployee(id)` | EMPLOYEE | John, Mary, Susan |
| `POST /api/employees` | `addEmployee()` | MANAGER | Mary, Susan |
| `PUT /api/employees` | `updateEmployee()` | MANAGER | Mary, Susan |
| `PATCH /api/employees/**` | `patchEmployee(id)` | MANAGER | Mary, Susan |
| `DELETE /api/employees/**` | `deleteEmployee(id)` | ADMIN | Susan |

The `/**` part matches an item path such as `/api/employees/1`. The controller uses `@RequestMapping("/api")` plus `@GetMapping("/employees/{employeeId}")` and similar mappings, producing the full paths above. Matchers authorize requests; they do not create controller endpoints. There is no terminal `.anyRequest()` rule in the current code, so requests outside these matchers have no explicit authorization mapping. That can matter when Spring dispatches an error to `/error`.

`http.httpBasic(Customizer.withDefaults())` turns on HTTP Basic with standard options. A REST client chooses **Basic Auth** and sends a username/password in the `Authorization` request header. **No Auth** sends no credentials. OAuth2 is not configured here. The explicit `SecurityFilterChain` replaces Boot's default web security chain; declaring the user-store bean separately replaces Boot's default user-store configuration. [Boot 4.0 security reference](https://docs.spring.io/spring-boot/4.0/reference/web/spring-security.html)

### CORS and CSRF are different

The current source says:

```java
http.cors(csrf -> csrf.disable());
```

Despite the lambda parameter name `csrf`, the method called is **`cors`**. This disables Spring Security's CORS integration, which concerns browser requests from a different origin. It does **not** disable CSRF checking. CSRF protection remains active for state-changing methods such as POST, PUT, PATCH, and DELETE. GET requests normally pass that check. [CORS](https://docs.spring.io/spring-security/reference/7.0/servlet/integrations/cors.html) · [CSRF](https://docs.spring.io/spring-security/reference/7.0/servlet/exploits/csrf.html)

**Suggested lesson correction, not implemented here:** If the intended exercise is a Basic-authenticated API for non-browser clients, replace that line with `http.csrf(csrf -> csrf.disable());` or `http.csrf(AbstractHttpConfigurer::disable);`. For a browser application that uses cookies, retain CSRF protection and send a valid CSRF token instead. Disabling CORS does not disable browser origin restrictions. Any decision to disable CSRF should follow the actual client and authentication design.

## Requests, observations, and troubleshooting

The configured port is the Boot default `8080`; no `server.port` or context path is set. The full collection URL is `http://localhost:8080/api/employees`. A browser address bar performs GET; use a REST client for POST, PUT, PATCH, or DELETE. For an isolated Basic-auth check, use `curl.exe` (PowerShell's `curl` alias can behave differently):

```powershell
curl.exe -i -u john http://localhost:8080/api/employees
curl.exe -i -u susan http://localhost:8080/api/employees
curl.exe -i -u scott http://localhost:8080/api/employees
```

Observed on **2026-09-22**, with the current source and MySQL running:

| Check | Result | What it establishes |
|---|---|---|
| `./mvnw.cmd test` | Failed before Maven starts: `Cannot index into a null array` | Wrapper launcher problem on this Windows host |
| `mvn test` | BUILD SUCCESS; 1 `contextLoads()` test | Context created; log named `inMemoryUserDetailsManager` as authentication user source |
| Anonymous `GET /api/employees` | HTTP 401 | Credentials required |
| Basic John `GET /api/employees` | HTTP 200 | John can read collection |
| Basic Susan `GET /api/employees` | HTTP 200 | Susan can read collection |
| Basic Scott `GET /api/employees` | HTTP 401 | Scott is absent from current in-memory store |
| Basic Susan `POST /api/employees` with `{}` | HTTP 401; no response body | Current write path did not succeed; this does not prove Susan lacks MANAGER |

The POST was deliberately sent with an empty object while CSRF remains enabled; the request did not report success. Based on the active code and Spring's documented defaults, CSRF is the likely first rejection before the controller. The **final 401** may be produced when the error dispatch to `/error` encounters no matching authorization rule; this second step was not independently traced. Therefore do not memorize “CSRF always returns 401”: the usual CSRF denial is 403, and this project's observed final status is 401. `contextLoads()` does not exercise endpoint status codes or authorization decisions.

| Symptom | Check |
|---|---|
| Browser does not prompt again | Inspect `Authorization` and `Cookie` request headers; browsers can reuse Basic credentials or a valid session. Incognito starts with separate browser state. A complete restart normally loses the server's in-memory session; Basic credentials may still be resent. |
| `scott` no longer logs in | Custom `InMemoryUserDetailsManager` replaces Boot's property-configured development user store. |
| GET succeeds, write fails with correct Susan credentials | Inspect CSRF configuration first; `http.cors(...)` did not disable it. Also inspect the final error dispatch/status. |
| 401 | Authentication was missing or rejected, or an error dispatch may have been denied; inspect the response and security logs. |
| 403 | An authenticated request failed authorization, or CSRF rejected an unsafe request. |
| PATCH/DELETE URL appears wrong | Use `/api/employees/{id}` with a real ID; `/api/employees:id` is not the mapped path. |

## Memory aids and active recall

Remember the setup in three parts: **users** (`InMemoryUserDetailsManager`), **request rules** (`authorizeHttpRequests`), and **transport/protection** (`httpBasic`, CSRF, CORS). During a request: **identify -> check safety -> check role -> run controller**. The exact internal filter ordering is managed by Spring Security.

1. What does `@Bean` publish here? **The returned manager or filter chain**, not a new controller method.
2. Who calls the `@Bean` methods? **Spring while building the application context.**
3. Who calls the manager later? **Spring Security during username/password authentication.**
4. Why does `scott` stop working? **The custom user-store bean makes Boot's single-user auto-configuration back off.**
5. Does `{noop}` turn authentication off? **No. It means the stored password is plain text.**
6. Can `{bcrypt}` be put before an unencoded password? **No. The suffix must be a real BCrypt hash.**
7. What does `hasRole("ADMIN")` check? **`ROLE_ADMIN`.**
8. Why does Susan have permission for MANAGER operations? **She has `ROLE_MANAGER` as well as `ROLE_ADMIN`.**
9. What does `HttpSecurity` do? **Builds the request security configuration.**
10. What does `http.build()` return? **A `SecurityFilterChain` bean used for future HTTP requests.**
11. Does `http.cors(csrf -> csrf.disable())` disable CSRF? **No. It calls `cors`.**
12. Why can GET succeed while POST fails? **GET normally needs no CSRF token; POST does while CSRF is enabled.**
13. Does a passing `contextLoads()` test prove Susan can POST? **No. It sends no HTTP request.**

## Official references

- [Spring Boot 4.0: Spring Security auto-configuration](https://docs.spring.io/spring-boot/4.0/reference/web/spring-security.html)
- [Spring Security 7.0: servlet filter architecture](https://docs.spring.io/spring-security/reference/7.0/servlet/architecture.html)
- [Spring Security 7.0: authorize HTTP requests](https://docs.spring.io/spring-security/reference/7.0/servlet/authorization/authorize-http-requests.html)
- [Spring Security 7.0: HTTP Basic](https://docs.spring.io/spring-security/reference/7.0/servlet/authentication/passwords/basic.html)
- [Spring Security 7.0: password storage](https://docs.spring.io/spring-security/reference/7.0/features/authentication/password-storage.html)
- [Spring Security 7.0: CSRF](https://docs.spring.io/spring-security/reference/7.0/servlet/exploits/csrf.html)
- [Spring Framework 7: `@Bean`](https://docs.spring.io/spring-framework/reference/core/beans/java/bean-annotation.html)

---

## JDBC-backed users — 2026-09-23

### Lesson snapshot and active source

The project still uses Spring Boot **4.0.0**, targets **Java 25**, and connects to the `employee_directory` MySQL database. Today's change replaces the active in-memory user store with a JDBC-backed user store. [`DemoJdbcSecurity.java`](src/main/java/com/luv2code/springboot/cruddemo/security/DemoJdbcSecurity.java#L10) is active; every line of [`DemoSecurity.java`](src/main/java/com/luv2code/springboot/cruddemo/security/DemoSecurity.java#L1) is currently commented out.

That distinction has two effects:

1. `JdbcUserDetailsManager` now looks for login accounts in database tables instead of rebuilding John, Mary, and Susan in memory.
2. The earlier custom `SecurityFilterChain`, HTTP-method rules, explicit HTTP Basic call, and CSRF setting are also commented out. Spring Boot's default web security configuration applies until a new active `SecurityFilterChain` bean is supplied. Do not describe yesterday's role table as the active request configuration for this snapshot.

No SQL schema file for the security tables is present in this project. The required tables and rows must therefore have been created outside this source tree unless a later lesson adds a migration or initialization script.

### Mental model: the storage location changed

Both managers provide user account information in the shape Spring Security understands. The main difference is where they store and retrieve it.

| Manager | Storage | Survives restart? | Typical use |
|---|---|---:|---|
| `InMemoryUserDetailsManager` | Java map in application memory | No; users are reconstructed from code | Lessons, tests, small demos |
| `JdbcUserDetailsManager` | Relational database reached through JDBC | Yes | Persistent database users using Spring Security's JDBC schema |

```text
Postman sends username/password
  -> Spring Security authentication filter
  -> AuthenticationManager / DaoAuthenticationProvider
  -> UserDetailsManager.loadUserByUsername(username)
  -> JdbcUserDetailsManager runs SQL through DataSource
  -> users row supplies password and enabled flag
  -> authorities rows supply permissions
  -> password check
  -> authorization check
  -> controller, if allowed
```

The manager **loads** account information; a `DaoAuthenticationProvider` performs the password comparison. A successful login produces an `Authentication` containing the user's granted authorities.

### `UserDetails`, `UserDetailsService`, and `UserDetailsManager`

These names describe different levels of the same design:

| Type | Responsibility |
|---|---|
| `UserDetails` | One account: username, stored password, enabled/account flags, and authorities |
| `UserDetailsService` | Read operation: `loadUserByUsername(...)` |
| `UserDetailsManager` | Extends `UserDetailsService` and adds operations such as `createUser`, `updateUser`, `deleteUser`, `changePassword`, and `userExists` |
| `JdbcUserDetailsManager` | Concrete `UserDetailsManager` that implements those operations with JDBC |

The bean method intentionally declares the interface as its return type:

```java
@Bean
public UserDetailsManager userDetailsManager(DataSource dataSource) {
    return new JdbcUserDetailsManager(dataSource);
}
```

Spring calls this factory method while building the application context. The `DataSource` argument is dependency injection into a `@Bean` method: Spring finds the existing `DataSource` bean and passes it in. The returned `JdbcUserDetailsManager` is then registered under the default bean name `userDetailsManager`. Spring Security later finds it through the `UserDetailsService` parent interface. [Spring Security `UserDetailsManager` API](https://docs.spring.io/spring-security/reference/7.0/api/java/org/springframework/security/provisioning/UserDetailsManager.html)

### What the `DataSource` object is

`javax.sql.DataSource` is a standard interface for obtaining database connections. It is not the database, a table, an SQL query, or one permanent connection. In this project:

1. `spring-boot-starter-data-jpa` brings JDBC and the preferred HikariCP connection pool.
2. Spring Boot reads the active `spring.datasource.*` properties in [`application.properties`](src/main/resources/application.properties#L4).
3. Boot creates a pooled `DataSource` bean for MySQL.
4. Spring injects that same connection provider into `JdbcUserDetailsManager`; JPA also uses it for employee data.

The database can therefore contain two separate concerns:

- `employee` contains application business data.
- `users` and `authorities` contain Spring Security login and permission data.

Sharing a `DataSource` does not make an employee automatically a security user. These tables have different purposes. [Spring Boot SQL database configuration](https://docs.spring.io/spring-boot/4.0/reference/data/sql.html)

### Why Spring Security uses `users` and `authorities`

With its default JDBC queries, Spring Security expects this logical schema:

```sql
users(username, password, enabled)
authorities(username, authority)
```

The standard table is named **`authorities`**, not `authorize`.

| Table/column | Question it answers |
|---|---|
| `users.username` | Which account is being requested? |
| `users.password` | What encoded password must be checked? |
| `users.enabled` | Is this account allowed to authenticate? |
| `authorities.username` | Which account receives this permission? |
| `authorities.authority` | Which role or permission does the account have? |

One user is stored once in `users` but can have several `authorities` rows:

```text
users
  susan | encoded-password | enabled

authorities
  susan | ROLE_EMPLOYEE
  susan | ROLE_MANAGER
  susan | ROLE_ADMIN
```

This separation represents a one-to-many relationship: one login account can have many permissions. `JdbcUserDetailsManager` uses default queries equivalent to:

```sql
select username, password, enabled
from users
where username = ?;

select username, authority
from authorities
where username = ?;
```

The default table and column names are conventions of Spring Security's ready-made JDBC implementation, not universal database rules. An application with an existing schema can configure custom user and authority queries. [Spring Security JDBC authentication](https://docs.spring.io/spring-security/reference/7.0/servlet/authentication/passwords/jdbc.html) · [default security schema](https://docs.spring.io/spring-security/reference/7.0/servlet/appendix/database-schema.html)

### Roles, authorities, and `ROLE_`

Spring stores and checks **authorities** as strings. A role is a convention built on top of an authority.

| Authorization expression | Authority it checks by default |
|---|---|
| `hasRole("EMPLOYEE")` | `ROLE_EMPLOYEE` |
| `hasAuthority("EMPLOYEE")` | `EMPLOYEE` exactly |
| `hasAuthority("ROLE_EMPLOYEE")` | `ROLE_EMPLOYEE` exactly |

Therefore, when the request rule uses `hasRole("EMPLOYEE")`, the simplest JDBC row is `ROLE_EMPLOYEE`. Do not call `hasRole("ROLE_EMPLOYEE")`; `hasRole` adds the prefix itself. The in-memory builder's `.roles("EMPLOYEE")` also creates the authority `ROLE_EMPLOYEE` automatically.

`ROLE_` is not required for every possible permission. Values such as `EMPLOYEE_READ` or `invoice:approve` are valid authorities when the rule uses `hasAuthority(...)` with the exact same value. Spring Security also allows role-prefix customization, and `JdbcUserDetailsManager` can prepend a configured role prefix while reading database values. For this lesson, storing `ROLE_EMPLOYEE`, `ROLE_MANAGER`, and `ROLE_ADMIN` keeps the database aligned with the existing `hasRole(...)` rules. [Spring Security request authorization](https://docs.spring.io/spring-security/reference/7.0/servlet/authorization/authorize-http-requests.html)

### What happens to the property user and in-memory users

The active `UserDetailsManager` bean makes Spring Boot's development-user auto-configuration back off. The existing `spring.security.user.*` values do not insert a database row and do not add another login beside JDBC. That property-defined account works only if the relevant Boot auto-configuration is active.

John, Mary, and Susan from yesterday also do not remain available merely because their source file still exists. Their entire configuration is commented out. A username works now only when the active authentication source can load it, which in this lesson means an appropriate row in `users` plus authority rows in `authorities`.

### What if both user managers are enabled?

If both configuration classes are active, Spring creates two beans implementing `UserDetailsService`:

```text
inMemoryUserDetailsManager
userDetailsManager -> JdbcUserDetailsManager
```

Spring Security does not automatically merge the accounts or perform an in-memory-then-JDBC search. Its automatic global username/password setup requires a single `UserDetailsService`; with two, it logs that the global authentication manager will not use either service automatically.

With the earlier `httpBasic(...)` filter chain enabled and no explicitly configured authentication provider, the expected consequence in this Spring Security generation is a failure while the security filter chain tries to obtain an authentication manager, potentially preventing startup. If another explicit `AuthenticationManager` or provider happens to exist, startup may succeed, but Postman will use only the stores wired into that manager. An unwired username/password request is normally rejected with HTTP 401.

This two-manager scenario was **discussed but not run on 2026-09-23**. Treat the exact startup exception or HTTP status as documentation-derived expectation until a focused experiment verifies the combined configuration. `@Primary` could resolve an ordinary injection ambiguity, but it does not express the desired multi-store authentication order. Supporting both stores intentionally requires explicit authentication-provider or delegating-service configuration.

### Current and historical behavior must not be mixed

| Snapshot | Active user store | Active request rules |
|---|---|---|
| 2026-09-22 lesson | In-memory John/Mary/Susan | Custom method/path roles, Basic auth, lesson CSRF configuration |
| 2026-09-23 source | JDBC `users`/`authorities` tables | Boot default web security because the whole earlier class is commented |

The current source still contains the earlier lesson as comments, which is useful history but does not create beans. Java does not execute commented annotations, classes, or methods.

### Verification boundary for today's notes

These notes were derived from the active source, project configuration, and Spring Security 7.0/Spring Boot 4.0 documentation. No build, application startup, database query, or Postman request was run for the 2026-09-23 JDBC snapshot. Consequently:

- The active bean graph and expected default JDBC schema are code/documentation-derived.
- The existence and contents of local `users` and `authorities` tables were not verified.
- The hypothetical two-manager startup/result was not observed.
- Yesterday's HTTP observations remain valid only for yesterday's in-memory snapshot.

### Common mistakes and fixes

| Symptom or mistake | Cause/check |
|---|---|
| Valid in-memory username now receives 401 | In-memory configuration is commented; create the account in the JDBC tables or reactivate one deliberate user store. |
| JDBC username exists but login fails | Check `enabled`, password encoding prefix/hash, and that authority rows exist. |
| Login succeeds but `hasRole("MANAGER")` denies access | Confirm the loaded authority is `ROLE_MANAGER`, including case. |
| A row contains `MANAGER` but the rule uses `hasRole("MANAGER")` | Store `ROLE_MANAGER`, configure a read-time prefix, or deliberately use `hasAuthority("MANAGER")`. |
| `users` table does not exist | `new JdbcUserDetailsManager(dataSource)` does not automatically create an external MySQL schema. Create it through a SQL script/migration. |
| Employee rows are mistaken for login accounts | `employee` is business data; the standard JDBC manager queries `users` and `authorities`. |
| Both managers are enabled to get fallback behavior | Two beans are not automatically combined; configure the authentication providers explicitly. |
| Yesterday's URL role rules appear not to work | Their `SecurityFilterChain` is commented out in the current source. |

### Active recall

1. What does `UserDetails` represent? **One security account and its authorities/account flags.**
2. What extra capability does `UserDetailsManager` add to `UserDetailsService`? **Create, update, delete, password-change, and existence operations.**
3. What does `DataSource` provide? **Database connections, usually from a pool.**
4. Who creates the `DataSource` here? **Spring Boot, from the JDBC driver and `spring.datasource.*` configuration.**
5. What does `JdbcUserDetailsManager` do with it? **Runs JDBC queries to load and manage security users and authorities.**
6. Why are there two tables? **One stores account credentials/status; the other allows each account to have multiple authorities.**
7. Does the JDBC manager use the `employee` table by default? **No. It expects `users` and `authorities`.**
8. Does `new JdbcUserDetailsManager(dataSource)` create those MySQL tables? **No.**
9. What does `hasRole("ADMIN")` look for? **`ROLE_ADMIN`.**
10. Must every authority start with `ROLE_`? **No; the prefix is required by the default role shortcut, while `hasAuthority` performs an exact authority check.**
11. Does the property-defined user get copied into `users`? **No.**
12. If both managers are beans, does Spring automatically search both? **No.**
13. Are yesterday's custom request matchers active now? **No; the entire earlier configuration class is commented.**

### Official references for this lesson

- [Spring Security 7.0: JDBC authentication](https://docs.spring.io/spring-security/reference/7.0/servlet/authentication/passwords/jdbc.html)
- [Spring Security 7.0: default database schema](https://docs.spring.io/spring-security/reference/7.0/servlet/appendix/database-schema.html)
- [Spring Security 7.0: `UserDetailsService`](https://docs.spring.io/spring-security/reference/7.0/servlet/authentication/passwords/user-details-service.html)
- [Spring Security 7.0: `UserDetailsManager` API](https://docs.spring.io/spring-security/reference/7.0/api/java/org/springframework/security/provisioning/UserDetailsManager.html)
- [Spring Security 7.0: `DaoAuthenticationProvider`](https://docs.spring.io/spring-security/reference/7.0/servlet/authentication/passwords/dao-authentication-provider.html)
- [Spring Security 7.0: request authorization](https://docs.spring.io/spring-security/reference/7.0/servlet/authorization/authorize-http-requests.html)
- [Spring Boot 4.0: SQL databases and `DataSource`](https://docs.spring.io/spring-boot/4.0/reference/data/sql.html)
- [Spring Boot 4.0: security auto-configuration](https://docs.spring.io/spring-boot/4.0/reference/web/spring-security.html)

Course navigation: [course map](../../README.md) · [cumulative review](../../COURSE_REVIEW.md).
