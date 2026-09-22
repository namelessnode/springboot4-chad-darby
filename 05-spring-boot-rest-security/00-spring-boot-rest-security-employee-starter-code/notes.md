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
