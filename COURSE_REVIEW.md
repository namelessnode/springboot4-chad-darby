# Spring Boot Course — Cumulative Review

This file is the short revision layer across all course sections. Detailed code, complete property explanations, runtime observations, and troubleshooting remain in each project's `notes.md`.

## Version snapshots

| Section and project | Spring Boot | Java | Build tool | Detailed notes |
|---|---:|---:|---|---|
| 01 — `springBootApp` | 4.1.1 | 26 | Maven Wrapper 3.9.16 | [Project notes](01-spring-boot-basics/springBootApp/notes.md) |
| 02 — `coach` | 4.1.1 | 26 | Maven Wrapper 3.9.16 | [Project notes](02-spring-boot-core/coach/notes.md) |
| 03 — `cruddemo-student` | 4.1.1 | Target 25; verified on 26.0.1 | Wrapper 3.9.16; installed Maven fallback | [Project notes](03-spring-boot-hibernate-jpa-crud/01-cruddemo-student/notes.md) |
| 04 — `01-spring-boot-rest-crud` | 4.1.1 | Target 25; verified on 26.0.1 | Wrapper 3.9.16; installed Maven fallback | [Project notes](04-springboot-rest-crud/01-spring-boot-rest-crud/notes.md) |
| 04 — `02-spring-boot-rest-crud-employee` | 4.1.1 | Target 25; verified on 26.0.1 | Wrapper 3.9.16; installed Maven fallback | [Project notes](04-springboot-rest-crud/02-spring-boot-rest-crud-employee/notes.md) |
| 04 — `03-spring-boot-rest-crud-employee-with-jpa-repository` | 4.1.1 | Target 25; verified on 26.0.1 | Wrapper 3.9.16; installed Maven fallback | [Project notes](04-springboot-rest-crud/03-spring-boot-rest-crud-employee-with-jpa-repository/notes.md) |
| 04 — `04-spring-boot-rest-crud-employee-with-spring-rest` | 4.1.1 | Target 25; verified on 26.0.1 | Wrapper launcher failed; installed Maven test passed | [Project notes](04-springboot-rest-crud/04-spring-boot-rest-crud-employee-with-spring-rest/notes.md) |
| 04 — `05-spring-boot-rest-crud-employee-swagger` | 4.1.1 | Target 25; verified on 26.0.1 | Installed Maven test passed; HTTP docs/UI checks passed | [Project notes](04-springboot-rest-crud/05-spring-boot-rest-crud-employee-swagger/notes.md) |
| 05 — `00-spring-boot-rest-security-employee-starter-code` | 4.0.0 | Target 25; verified on 26.0.1 | Wrapper failed; installed Maven test passed | [Project notes](05-spring-boot-rest-security/00-spring-boot-rest-security-employee-starter-code/notes.md) |

## 01 — Spring Boot Basics

### Application startup

`@SpringBootApplication` marks the main configuration class. `SpringApplication.run(...)` creates the Spring application context, applies auto-configuration, scans the application's package tree for components, and starts the embedded server for this web application.

**Recall rule:** keep the main application class in a parent package so controllers and other components in its subpackages are discovered.

[Review the startup and component-scanning details](01-spring-boot-basics/springBootApp/notes.md#4-application-startup-and-component-scanning).

### Building a complete application URL

```text
http://localhost:{server.port}{server.servlet.context-path}{endpoint-path}
```

For the first project, `/firstEndpoint` becomes:

```text
http://localhost:8081/springBootApp/firstEndpoint
```

`spring.application.name` supplies a logical application identity. It does not change the URL; `server.port` and `server.servlet.context-path` do that.

[Review the controller and request flow](01-spring-boot-basics/springBootApp/notes.md#5-controller-and-request-flow).

### Externalized configuration

`application.properties` keeps settings outside Java code. Spring loads the values into its `Environment`; `@Value("${property.name}")` can inject an individual value. A placeholder without a default fails during context creation when the property is missing, while `${property.name:fallback}` supplies an intentional fallback.

**Selection rule:** use `@Value` for a small demonstration or isolated value. Prefer `@ConfigurationProperties` when several related settings should become one typed, validatable object.

[Review every property in the first project](01-spring-boot-basics/springBootApp/notes.md#6-applicationproperties-explained).

### Actuator's three controls

| Concern | Meaning | Example property |
|---|---|---|
| Access | Whether an endpoint exists and which operations are permitted | `management.endpoint.env.access=none` |
| Exposure | Whether an available endpoint is reachable over HTTP or JMX | `management.endpoints.web.exposure.include=health,info` |
| Info contribution | Which information sources add content to `/info` | `management.info.env.enabled=true` |

In Spring Boot 4.1.1, only `health` is exposed over HTTP by default. Custom `info.*` properties appear only when the `info` endpoint is exposed and the environment info contributor is enabled.

**Recall rule:** access controls the room, exposure controls its doorway, and contributors supply the information inside `/info`.

[Review Actuator configurations and endpoint URLs](01-spring-boot-basics/springBootApp/notes.md#8-actuator-access-exposure-and-information-content).

### What a context-load test proves

`@SpringBootTest` with an empty `contextLoads()` test verifies that Spring can build the application context using the current beans, dependencies, and required configuration. It does not prove controller mappings or response bodies; those need focused MVC or live HTTP tests.

[Review testing commands and observed results](01-spring-boot-basics/springBootApp/notes.md#10-testing-and-running-the-project).

## 02 — Spring Boot Core

### IoC, the application context, and beans

**Inversion of Control** means Spring's container controls the creation, assembly, and lifecycle of application objects. **Dependency injection** is the mechanism by which a bean declares what it needs and Spring supplies matching beans from the application context.

An ordinary object becomes a **Spring bean** when it is registered with and managed by the container. `@Component` makes a class eligible for scanning; `@Controller`, `@RestController`, `@Service`, and `@Repository` are more specific component stereotypes. An interface such as `Coach` is a contract, while its component implementations become the injectable beans.

**Recall rule:** the application context is the container; beans are the objects inside it; DI is how their relationships are assembled.

[Review the complete container mental model](02-spring-boot-core/coach/notes.md#3-mental-model-ioc-di-context-beans-and-components).

### Component scanning

`@SpringBootApplication` combines Boot configuration, auto-configuration, and component scanning. By default, scanning begins at the main application's package and includes subpackages. A sibling package is not discovered automatically.

Once `scanBasePackages` is explicitly supplied, list every required component-scan root. The `coach` project therefore lists both `com.example.coach` and `com.example.outsidebasepackage`.

**Failure clue:** an undiscovered controller can let the application start but still produce a request-time 404. If logs mention a missing static resource for an intended controller URL, first check whether any controller mapping was registered.

[Review the outside-package experiment and TRACE logs](02-spring-boot-core/coach/notes.md#5-component-scanning-and-the-outside-package-experiment).

### Choosing an injection style

| Style | Best fit | Main tradeoff |
|---|---|---|
| Constructor injection | Required dependencies; preferred default | Makes requirements explicit and supports `final` fields. |
| Setter injection | Optional or deliberately replaceable dependencies | The reference is assigned after construction and cannot carry the same `final` guarantee. |
| Field injection | Small demonstrations or existing legacy code | Hides dependencies, complicates plain unit tests, and permits incomplete manual construction. |

A Spring bean with one constructor does not need `@Autowired` on that constructor. Annotation-driven setter or field injection needs an injection marker such as `@Autowired`.

**Java rule:** a blank `final` field can be assigned in a constructor, but not later in a setter. That compile-time failure comes from Java, not Spring.

A no-argument constructor is not automatically called before a parameterized constructor. Java executes the selected constructor; `this(...)` explicitly chains to another constructor in the same class, while `super(...)` invokes a direct-superclass constructor. If neither is written, Java implicitly invokes `super()`. Adding an empty no-argument constructor to a class with a blank `final` dependency fails unless that constructor also assigns the field or delegates to one that does.

[Compare all three dependency-injection styles](02-spring-boot-core/coach/notes.md#7-dependency-injection-styles).

### Resolving multiple implementations

Injection by interface works automatically while there is one matching bean. With several `Coach` beans, Spring needs another selection rule:

- `@Qualifier` narrows candidates for one injection point.
- `@Primary` marks the normal default for unqualified single-bean injection when exactly one primary remains among the eligible candidates.
- `List<Coach>` or `Map<String, Coach>` can intentionally receive multiple implementations.

Default component bean names normally use lower camel case, such as `CricketCoach` → `cricketCoach`. Qualifier values are case-sensitive. A qualifier is local, so fixing one controller does not resolve ambiguous dependencies in other controller beans.

When cricket is primary but an injection point says `@Qualifier("tennisCoach")`, tennis is injected: the qualifier makes a local selection instead of using the normal default. Marking both cricket and tennis primary does not produce a winner for an unqualified `Coach`; Spring fails because more than one primary candidate remains.

[Review the four-bean failure, qualifier convention, and alternatives](02-spring-boot-core/coach/notes.md#8-multiple-beans-ambiguity-qualifiers-and-primary-beans).

### REST controller request flow

`@RestController` is effectively `@Controller` plus `@ResponseBody`. Spring MVC maps the incoming GET path to a handler method; the controller calls its already-injected coach bean; the returned `String` is written into the HTTP response body.

With direct injection, Spring selects and supplies the dependency while creating the controller bean. With the current constructor controller’s parameter-level `@Lazy`, Spring supplies a proxy during controller creation and resolves the real baseball target when the proxy is first used.

[Review the request sequence and current endpoints](02-spring-boot-core/coach/notes.md#9-restcontroller-controller-and-the-http-request-path).

### Eager beans, lazy beans, and lazy dependency proxies

Singleton beans are eager by default. Marking a provider bean `@Lazy` delays it only until something requests it; an eager singleton that directly depends on it can still force creation during startup.

Class-level `@Lazy` delays the consumer bean itself. Parameter-level `@Lazy` injects a proxy, allowing the consumer to be created while delaying the real dependency until the proxy is first used. Therefore, “controller logged at startup, coach logged on the first request” is the expected parameter-proxy pattern.

**Recall rule:** class-level lazy changes when the consumer exists; injection-point lazy changes when one dependency behind a proxy is resolved.

[Review the primary, constructor, and lazy-initialization experiments](02-spring-boot-core/coach/notes.md#14-lesson-update-2026-09-16-primary-constructors-and-lazy-initialization).

### Global lazy initialization

`spring.main.lazy-initialization=true` makes eligible beans lazy by default. It can reduce startup work, but it shifts construction and possible failures to first use. Required framework infrastructure still starts, and an explicitly eager or early-requested bean can still be created during startup.

**Recall rule:** global lazy initialization moves work from startup to demand; it does not remove the work.

### Singleton and prototype scope

| Scope | Container behavior | Key warning |
|---|---|---|
| Singleton | One shared instance per bean definition and Spring container; the default. | Keep shared mutable state and thread safety in mind. |
| Prototype | A new instance for every request made to the container. | Direct injection into a singleton occurs only when that singleton is created; it does not refresh the prototype on every method call. |

In the current scope exercise, the singleton `ScopeController` requests the primary prototype `CricketCoach` twice during construction. Spring creates two objects, so `firstCoach == secondCoach` is `false`. The controller then retains those two references. Use on-demand lookup such as `ObjectProvider` when a long-lived bean truly needs a fresh prototype repeatedly.

### Lifecycle callbacks and ownership

`@PostConstruct` runs after construction and dependency injection. For a container-managed singleton, `@PreDestroy` runs during normal application-context shutdown. It is not a garbage-collection callback, and a forced process termination may prevent it from running.

Spring caches and owns singleton lifetimes, so it can destroy them when the context closes. For prototype beans, Spring creates and initializes each instance and then hands it to the requester without keeping a prototype-instance cache. The requester owns cleanup; configured prototype destruction callbacks are not invoked automatically.

**Retention nuance:** although the bean factory does not cache a prototype for reuse, a dependent object can retain that prototype through an ordinary Java field.

### Component scanning versus explicit `@Bean` registration

| Registration style | Best fit | Default bean name |
|---|---|---|
| `@Component`, `@Service`, `@Repository`, or `@Controller` | Application-owned classes with straightforward construction | Normally the lower-camel-case class name. |
| `@Configuration` plus `@Bean` | Third-party classes, builders or factories, custom construction, and multiple configured instances | The bean method name unless `@Bean` supplies an explicit name. |

The current `SportConfig` method uses `@Bean("aqua")`, so `SwimCoachController` correctly requests `@Qualifier("aqua")`. `SwimCoach` needs no `@Component`: the object returned from the processed bean method becomes Spring-managed.

[Review global lazy initialization, scopes, lifecycle, and Java configuration](02-spring-boot-core/coach/notes.md#15-lesson-update-2026-09-17-global-lazy-initialization-scopes-lifecycle-and-bean).

## 03 — Hibernate and Spring Data JPA CRUD

### JPA, Hibernate, Spring Data JPA, and MySQL

Jakarta Persistence defines the standard annotations and persistence contracts. Hibernate implements those contracts and translates entity operations into SQL. Spring Data JPA builds repository abstractions above JPA, while JDBC and MySQL Connector/J carry database commands to MySQL.

**Recall rule:** JPA is the contract, Hibernate is the implementation, Spring Data JPA removes repository boilerplate, and MySQL stores the rows.

### Entity mapping

`@Entity` adds `Student` to the persistence model; it does not make each student a Spring bean. `@Table` names the physical table, `@Id` identifies the primary key, `@GeneratedValue` describes ID generation, and `@Column` names field-to-column mappings. Because `@Id` is placed on a field, this entity uses field access.

A public or protected no-argument constructor lets the provider instantiate an entity when loading data. Explicit physical table and column names make the schema contract visible and protect it from accidental changes caused by Java refactors or naming-strategy changes.

### Generated identifier strategies

| Strategy | Mental model |
|---|---|
| `IDENTITY` | The insert triggers a database identity or auto-increment value; this directly matches the current MySQL schema. |
| `SEQUENCE` | Obtain an ID from a database sequence or provider-supported sequence-style resource before inserting. |
| `TABLE` | Reserve IDs through a separate table that stores generator state. |
| `UUID` | Generate a UUID suitable for a `UUID` or `String` identifier. |
| `AUTO` | Delegate the choice to the persistence provider; do not assume it means identity/auto-increment. |

**Recall rule:** `AUTO` delegates; `IDENTITY` specifies.

Annotations describe the resource Hibernate should use; schema generation or migrations decide whether the resource is created. For an external MySQL database without an active `ddl-auto` setting, use scripts or migrations to create tables and other generator resources.

### Identifier allocation, JDBC batching, and ID types

Generator `allocationSize` reserves a block of future IDs to reduce generator round trips. `hibernate.jdbc.batch_size` groups compatible SQL statements. They solve different problems, and reserved values mean primary keys are not guaranteed to be gap-free.

A primitive `int` generated ID starts at `0`; an `Integer` starts at `null`, which represents “no database ID assigned yet” more clearly. The current `int` remains valid. A production `Long` ID is commonly paired with a SQL `BIGINT` column.

### Logger hierarchy

`logging.level.root` is the fallback threshold for every logger in the application and its dependencies. A logger-specific property such as `logging.level.org.springframework` overrides that fallback only for the named package hierarchy. The most specific configured logger wins.

For JPA troubleshooting, `logging.level.org.hibernate.SQL=DEBUG` shows Hibernate's generated SQL, while `logging.level.org.hibernate.orm.jdbc.bind=TRACE` shows values bound to JDBC placeholders. Bind logs can expose sensitive data, so keep them scoped to local diagnosis. Targeted logger categories are usually clearer than leaving the entire application at root `DEBUG`.

**Recall rule:** root is the application-wide baseline; package settings are targeted overrides.

### Manual DAO and repository semantics

`StudentDAO` defines the persistence contract; `StudentDAOImpl` supplies the `EntityManager` implementation. Callers inject the interface so they are not coupled to one implementation. `@Repository` is injected like any other component stereotype, but it also communicates data-access intent and makes the bean eligible for Spring persistence-exception translation.

`@Autowired` is optional on the current DAO constructor because it is the class's only constructor. `@Override` is a Java compiler check, not a Spring annotation.

### Transactions for writes and reads

`EntityManager.persist()` requires a write transaction, so `save()` is marked `@Transactional`. A basic `EntityManager.find()` without locking may execute outside an explicit transaction; this does not bypass JPA. Read transactions are still useful when several operations need one consistent unit of work, lazy relationships must stay managed, or locking is requested.

**Recall rule:** `@Transactional` defines the unit of work; it does not decide whether an operation uses JPA.

### Managed state, detached state, and updates

JPA has no mandatory explicit update operation for an entity that is already managed. Change its persistent fields inside the active persistence context, and Hibernate's dirty checking synchronizes those changes during flush/commit.

`merge()` serves a different purpose: it copies the state of a new or detached object into a managed entity and returns that managed instance. The passed object should not be assumed to become managed. In the current DAO, a primary-key lookup first enforces “update only if the row exists,” and `merge()` then copies the detached runner object's state.

**Recall rule:** modify managed state directly; merge detached state and continue with the returned managed instance when necessary.

The current runner has one caveat: if ID `5` does not exist, it invokes setters on `null` before reaching the DAO's existence check. Callers must handle the not-found case themselves.

### Removing one entity versus a bulk delete

`deleteById()` starts a transaction, finds a managed `Student`, and passes it to `EntityManager.remove()`. The entity becomes removed, and SQL deletion occurs during flush/commit. A missing ID is currently a quiet no-op.

`deleteAll()` instead executes JPQL `delete from Student`. It does not load every entity and `executeUpdate()` returns the number affected. Bulk delete is efficient, but it does not synchronize already-managed affected objects and does not apply JPA relationship cascades. The current simple `Student` entity has no relationships, but that distinction matters as the model grows.

**Recall rule:** `remove()` follows one entity's lifecycle; bulk JPQL operates directly on matching database rows.

JPQL bulk delete has `DELETE` semantics and does not reset MySQL `AUTO_INCREMENT`. It is not equivalent to `TRUNCATE TABLE`.

### MySQL identity counters and table-clearing commands

MySQL will not lower an `AUTO_INCREMENT` value below the maximum existing primary key. After IDs `1000` through `1002` exist, setting the table option to `1` still leaves the next generated ID above `1002`.

`DELETE` removes selected or all rows but normally keeps the counter. `TRUNCATE` removes every row and resets the empty table's counter while preserving the table definition. `DROP TABLE` removes the data and the table definition. Primary keys identify rows; they are not guaranteed gap-free business numbers.

### JPQL, HQL, parameters, and typed results

JPQL is the portable Jakarta Persistence query language. HQL is Hibernate's query language and supports JPQL-style entity queries plus Hibernate extensions. Both query the object model—`Student` and `lastName`—rather than the physical SQL names `student` and `last_name`.

`TypedQuery<Student>` means each query result item is a `Student`; `getResultList()` therefore returns `List<Student>`. `TypedQuery<List<Student>>` would incorrectly describe each result item as a list.

Dynamic values belong in named or positional parameters. Concatenating `"lastName B"` into `where lastName = ` produced `where lastName = lastName B`, which Hibernate rejected before executing SQL. The safe form is `where s.lastName = :lastName` followed by `setParameter("lastName", value)`.

**Recall rule:** query structure stays in the JPQL/HQL string; changing data values are bound separately.

### Hibernate schema management

`spring.jpa.hibernate.ddl-auto` tells Hibernate what to do with schema objects mapped by entities:

| Value | Compact meaning |
|---|---|
| `none` | Perform no automatic schema action. |
| `validate` | Check mappings against the schema and fail on mismatch without changing it. |
| `update` | Attempt incremental schema adjustment while preserving existing data. |
| `create` | Recreate mapped schema objects at startup. |
| `create-drop` | Recreate at startup and drop during orderly shutdown. |

This property does not create the MySQL server, database catalog, or user. The current project uses `update`; its nearby comment describes `create-drop`, but the property value controls actual behavior.

**Enterprise rule:** use Flyway/Liquibase or another reviewed migration process to change important shared schemas, then normally use `validate` or `none` in production. `update`, `create`, and `create-drop` remain useful for local development, prototypes, and isolated tests.

### What the current context test would do

The earlier update/delete experiments are now commented out. The active `CommandLineRunner` operation is `createAndSaveStudent(studentDAO)`, and `ddl-auto=update` is enabled. Therefore `@SpringBootTest` would start the application, potentially adjust mapped schema objects, and insert a student into the configured MySQL database.

**Testing rule:** `contextLoads()` is only harmless when startup code and schema settings are harmless. Disable lesson runners or use an isolated test database before running a full-context test.

[Review the complete setup, DAO, transaction, MySQL counter, and custom-query notes](03-spring-boot-hibernate-jpa-crud/01-cruddemo-student/notes.md).

## 04 — Spring REST CRUD

### The first request path

The first endpoint is `http://localhost:8080/test/hello`. Boot uses port `8080` and root context path `/` because neither setting is overridden. Spring then combines the controller-level `/test` mapping with the method-level `/hello` mapping.

**Recall rule:** server address + context path + class mapping + method mapping = complete endpoint URL.

### Startup and dispatch

`RestApplication.main()` calls `SpringApplication.run(...)`. `@SpringBootApplication` enables Boot configuration, auto-configuration, and component scanning. Because `DemoRestController` is below the `com.example.rest` package, it is discovered and registered as a singleton controller bean.

Embedded Tomcat accepts the HTTP request and passes it to Spring MVC's `DispatcherServlet`. The dispatcher uses handler-mapping and handler-adapter infrastructure to locate and invoke `DemoRestController.hello()`, then writes `Hello World` into the response body.

**Recall rule:** Tomcat receives; `DispatcherServlet` coordinates; the mapping selects; the controller handles; a message converter writes.

### `@RestController` and request mappings

`@RestController` is effectively `@Controller` plus default `@ResponseBody` semantics. Therefore a returned `String` becomes response content instead of a logical view name.

`@RequestMapping` can restrict path, HTTP method, parameters, headers, consumed media types, and produced media types. The hello endpoint specifies only paths, so it is not GET-only. The student controller now demonstrates `@GetMapping`, the GET-specific shortcut for `@RequestMapping(method = RequestMethod.GET)`.

### Message conversion and JSON binding

Spring MVC `HttpMessageConverter` implementations bridge Java values and HTTP bodies. The hello endpoint's `String` response uses string conversion. The student endpoints now prove Jackson serialization by returning `Student` and `List<Student>` values.

For POJO endpoints:

- **Serialization/marshalling** converts a Java object to JSON.
- **Deserialization/unmarshalling** converts JSON into a Java object.
- `@RequestBody` asks Spring to read the request body into the declared Java parameter type.
- `Content-Type` describes what the client sent; `Accept` describes what it can receive.

The Spring MVC starter brings Jackson transitively. This Boot 4.1.1 project resolves Jackson 3.1.5 and uses the Jackson 3 `JsonMapper` model; older Jackson 2 material commonly refers to `ObjectMapper`.

**Recall rule:** routing chooses the method; message conversion chooses the representation; Jackson performs JSON object mapping.

### Student data initialization and JSON responses

`StudentRestController` uses `@PostConstruct` to add three students after bean construction and dependency injection but before the singleton controller is placed into service. This is convenient demonstration data, not persistent storage; a new application context creates a new list.

`GET /api/students` returns `List<Student>`. Spring handles the controller return value, and Jackson serializes the list as a JSON array whose objects contain `firstName` and `lastName`, derived from the POJO accessors.

**Recall rule:** the controller returns Java values; the message converter and Jackson produce the HTTP JSON representation.

### URI templates and `@PathVariable`

In `/api/students/{studentId}`, `{studentId}` captures one path segment. `@PathVariable` binds the captured text to a Java method parameter, and Spring converts it to the declared type before method invocation.

`@PathVariable("studentId")`, `@PathVariable(name = "studentId")`, and `@PathVariable(value = "studentId")` are equivalent because `name` and `value` are aliases. With an explicit annotation name, the Java parameter may have another valid name, such as `int id`. Without an explicit name, the discoverable Java parameter name must match the URI variable.

**Recall rule:** `{studentId}` names the URL value; the annotation connects that value to the Java parameter; the Java type controls conversion.

### Mapping selection versus Java type conversion

Spring selects a handler from mapping conditions such as path and HTTP method before converting path variables. Two GET methods with the same effective `/students/{studentId}` mapping therefore conflict during application startup even if one parameter is `int` and the other is `String`.

The `{variable:regex}` convention can create distinct routes, such as a numeric `{studentId:\d+}` route and an alphabetic `{studentCode:[A-Za-z]+}` route. The regex distinguishes the route; type conversion still happens afterward. Distinct semantic paths such as `/students/id/{id}` and `/students/code/{code}` can be clearer.

The current method uses `students.get(studentId)`, so the value is a zero-based list index rather than a database ID. It now validates negative and out-of-range values before accessing the list. Runtime checks observed `400` for non-numeric `abcd` and a controlled `404` for invalid numeric indexes such as `-1` and `3`.

### REST exception handling

The current error flow separates four responsibilities:

1. `getStudent()` detects an invalid index.
2. `StudentNotFoundException` represents the application-specific failure.
3. An `@ExceptionHandler` method translates the exception into an HTTP response.
4. `StudentErrorResponse` defines the JSON error-body contract.

An empty `@ExceptionHandler` annotation can infer its mapped exception from the method's exception parameter. The type may instead be declared explicitly, for example `@ExceptionHandler(StudentNotFoundException.class)`. It does not need to appear in both places; retain the parameter when the method needs the exception message or cause.

For multiple exception mappings, annotation braces list alternatives: `@ExceptionHandler({A.class, B.class})`. The method may take one common parent such as `RuntimeException` or `Exception`, or omit the exception parameter if it does not need details. Java multi-catch syntax such as `A | B ex` is not valid in a method parameter, and two exception parameters do not mean “either one.” Separate handlers are usually clearer when the failures require different statuses or bodies.

`ResponseEntity<StudentErrorResponse>` means the HTTP body is a `StudentErrorResponse`; the wrapper also controls headers and the real HTTP status. A JSON field named `status` is only body data. The current code sets both the body field and `ResponseEntity` status to `404`, but only the latter controls the network response status.

**Status rule:** use `400` for invalid request input, `404` for an absent requested resource, `409` for a state conflict, and `500` for an unexpected server failure. Choose based on the failure's meaning to the client.

The handlers have now been moved from `StudentRestController` into a global `StudentRestExceptionHandler` annotated with `@ControllerAdvice`. The current broad `Exception` fallback demonstrates conversion errors, but it can also catch failures from other controllers, misclassify server defects as `400`, and expose raw internal messages. Larger applications normally use narrow expected-error mappings and a safe logged `500` fallback. Spring Framework 7 also offers `ProblemDetail`, `ErrorResponse`, and `ResponseEntityExceptionHandler` for standardized RFC 9457 error responses.

**Recall rule:** throw a meaningful Java exception inside the application; translate it at the web boundary into a safe body and an accurate HTTP status.

### Global controller advice and response status

`@ControllerAdvice` is a Spring component that shares `@ExceptionHandler`, `@InitBinder`, and `@ModelAttribute` methods across all or selected controllers. The bean is discovered when the context starts, but an exception-handler method runs only when a compatible exception occurs. Controller-local handlers are checked before global advice.

`@RestControllerAdvice` combines `@ControllerAdvice` with `@ResponseBody`. It is a convenient semantic fit for REST APIs, but it does not create an error DTO automatically. The current plain `@ControllerAdvice` works because its methods return `ResponseEntity<StudentErrorResponse>`, which already represents a response body, status, and optional headers.

Keep these three concerns separate:

| Mechanism | Effect |
|---|---|
| `response.setStatus(404)` | Sets ordinary data in the JSON DTO only. |
| `ResponseEntity.status(NOT_FOUND)` | Sets the real HTTP response status and supports dynamic status/headers. |
| `@ResponseStatus(NOT_FOUND)` | Declares a fixed real HTTP status on a handler method or exception class. |

When `@RestControllerAdvice` returns a DTO directly, `@ResponseStatus` can replace the status carried by `ResponseEntity`. It does not populate the DTO's `status` property: the current primitive `int` would remain `0` unless it is set or the DTO is redesigned. With plain `@ControllerAdvice`, a directly returned DTO also needs `@ResponseBody`; returning `ResponseEntity` avoids that requirement.

Advice may be scoped through `basePackages`/`value`, `basePackageClasses`, `assignableTypes`, or `annotations`. Multiple selectors use OR semantics. `@Order` controls precedence among advice beans; it does not select controllers. “Global” need not mean one huge file—split advice by domain or concern and avoid ambiguous overlaps.

**Recall rule:** advice controls where exception translation is shared; response-body semantics control how a Java value becomes content; `ResponseEntity` or `@ResponseStatus` controls the real HTTP status.

### Employee REST + JPA layering

The employee project introduces a layered request path:

```text
HTTP request -> EmployeeRestController -> EmployeeService -> EmployeeDao -> EntityManager -> MySQL
```

The controller owns HTTP concerns, the service owns use-case and transaction boundaries, the DAO owns persistence operations, and the entity maps Java state to the database table. Constructor injection keeps each dependency explicit and easy to replace in focused tests.

`@Service` is a specialized `@Component`: it gives the class service-layer meaning and makes it eligible for component scanning. It does not make every method transactional automatically.

**Recall rule:** a stereotype registers and describes the bean; `@Transactional` defines transaction behavior.

### Transactions at the service boundary

`EmployeeServiceImpl.save()` and `deleteById()` carry `@Transactional`, while the read methods do not. JPA write operations such as merge and remove need an active transaction. Ordinary reads can execute without a method-level transaction in this lesson, although real applications may use read-only transactions when a use case requires one consistent persistence context, lazy-loading access, or several coordinated reads.

The service layer is a common transaction boundary because one business operation may eventually coordinate several DAO calls. Spring normally applies `@Transactional` through a proxy around an externally invoked service method: begin transaction, run the method, commit on success, or roll back according to the rollback rules after failure.

**Recall rule:** make the transaction cover the complete business operation, not merely one SQL statement.

### `@RequestBody`, omitted fields, and generated IDs

`@RequestBody Employee employee` asks Spring MVC to read the HTTP body and use an HTTP message converter—Jackson for JSON—to construct and populate an `Employee`. The annotation requires a body by default; it does not require every possible JSON property.

The entity's generated ID uses nullable `Integer`, so JSON may omit `id` and the Java value remains `null`. The current project has no validation annotation requiring all four entity properties, and the SQL columns other than the ID are nullable. A Java class having four fields therefore does not mean every request must contain four properties.

For POST, the controller explicitly calls `employee.setId(null)`. Any client-supplied ID is ignored, and MySQL's `AUTO_INCREMENT` column supplies the value through `GenerationType.IDENTITY`. This project is not using a database sequence.

**Recall rule:** `@RequestBody` controls body conversion; validation annotations and database constraints separately control what values are acceptable.

### Why POST and PUT share `save()`

Both controller methods call the same service `save()`, which reaches `EntityManager.merge()`. Spring MVC knows whether POST or PUT was sent and selects the corresponding controller mapping; JPA sees only entity state and does not know which HTTP method initiated the call.

`merge()` copies state into a managed entity and returns that managed instance. A null/new identity can lead to an insert; an identity representing existing state can lead to an update. The current POST route forces a null ID. The current PUT route does not verify that an ID exists, so a missing or unknown ID can result in insert-like behavior rather than guaranteed update-only semantics.

**Recall rule:** HTTP routing chooses the controller operation; persistence state and ID determine what the ORM does afterward.

### PATCH, map presence, and `JsonMapper`

`PATCH /api/employees/{employeeId}` identifies the employee in the URL and carries only requested field changes in a `Map<String, Object>` body. This preserves property presence: `{}` contains no changes, while `{"email": null}` explicitly contains the `email` key with a null value. Binding directly to a fresh `Employee` would normally represent both omitted reference fields and explicit nulls as Java `null`, unless additional presence tracking is designed.

The controller rejects `id` in the patch map because the path is the authoritative identity and generated primary keys are not treated as mutable employee data. It then calls `jsonMapper.updateValue(existingEmployee, patchPayload)` to apply the supplied properties. `JsonMapper` belongs to Jackson 3, while Spring Boot auto-configures and manages the instance injected into the controller. `updateValue()` changes Java object state; transactional `save()` performs persistence afterward.

**Recall rule:** the URL chooses what to update, the patch document says what to change, Jackson applies the changes in memory, and the service transaction persists them.

### PUT versus PATCH

PUT conventionally communicates replacement of target state and is idempotent. PATCH communicates a set of partial modifications and is not inherently guaranteed to be idempotent, although repeating this project's simple property assignments normally reaches the same state. The same Java operations could be placed behind PUT, but doing so would make omitted-field meaning surprising to clients.

**Recall rule:** PUT describes the desired resource state; PATCH describes changes to the current state.

### Transactional DELETE by ID

The delete contract is now consistently named `deleteById(int id)` across controller, service, and DAO. The service method starts the transaction, and the DAO resolves the ID with `EntityManager.find(Employee.class, id)` before passing the resulting managed entity to `remove()`. This keeps entity lookup and removal in the same persistence context rather than relying on a controller-loaded entity remaining managed.

The controller's lesson-level not-found check remains, but current `findById()` uses `getSingleResult()`, which throws for no row instead of returning `null`. Missing-resource translation to `404` is therefore still future work.

**Recall rule:** pass identity across application layers; obtain the managed entity where the persistence operation executes.

### Current employee-project limitations

- `findById()` uses `getSingleResult()`. An unknown ID currently becomes HTTP `500`; a later REST exception layer should translate absence to `404`.
- PATCH currently accepts an untyped map without a dedicated validated request contract or explicit field allowlist beyond rejecting `id`.
- DELETE performs the retained controller pre-check and then a second lookup inside the DAO transaction; not-found handling can later move into the service operation.
- The controller has one unused DAO import, and several service methods can add `@Override` for compile-time checking and readability.

These are recorded observations rather than silently corrected lesson code.

### Current test boundary

The empty `@SpringBootTest` `contextLoads()` method proves that the application context starts. It does not send an HTTP request or verify a route, status, response body, POJO conversion, or exception mapping. On 2026-09-20, the context test passed with installed Maven. Earlier checks on temporary port `18080` captured the pre-handler `500`; checks on `18081` verified the first local handlers; checks on `18082` verified that the global advice still returns `200` for index `0`, `404` for indexes `-1` and `3`, and `400` for conversion failure with `abcd`.

The employee project's `contextLoads()` test also passed with installed Maven on 2026-09-20 and connected to the configured MySQL database. Focused smoke tests previously returned `200` with five rows from `GET /api/employees`, `200` from `GET /api/employees/1`, and the current `500` from `GET /api/employees/9999`. The test passed again after PATCH and after the ID-based DELETE correction. POST, PUT, and PATCH were not live-tested because they mutate the course database; DELETE was not live-tested because it is destructive.

[Review the REST foundations](04-springboot-rest-crud/01-spring-boot-rest-crud/notes.md) and the [manual employee REST/JPA notes](04-springboot-rest-crud/02-spring-boot-rest-crud-employee/notes.md).

### Replacing the manual DAO with `JpaRepository`

The repository project replaces `EmployeeDao` and `EmployeeDaoImpl` with one domain-specific interface:

```java
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
}
```

The persistence work did not disappear. Spring Data JPA now supplies the standard repository implementation, which still uses JPA and Hibernate to reach MySQL. The controller continues to depend on `EmployeeService`, while the service now injects `EmployeeRepository`; this keeps the web layer independent of the persistence adapter.

**Recall rule:** Spring Data removes repeated DAO implementation code; it does not remove JPA, Hibernate, transactions, SQL, or the database.

### Inherited methods and the runtime repository proxy

`JpaRepository<Employee, Integer>` specializes a generic repository contract: `Employee` is the domain type and `Integer` is its ID type. Methods such as `findAll()`, `findById()`, `save()`, `deleteById()`, `existsById()`, and `count()` are inherited, so the project interface does not redeclare them.

Spring Boot auto-configures repository scanning under the main application package. Spring Data reads the repository metadata and creates a runtime proxy implementing `EmployeeRepository`; standard operations are routed to its JPA base implementation. On 2026-09-20, clean startup logged `Found 1 JPA repository interface`.

**Recall rule:** method inheritance explains why the Java call compiles; repository scanning and proxy creation explain why an implementation object exists at runtime.

### Why `findByEmail()` must be declared

Every repository has an identifier, so `findById()` belongs to the generic CRUD contract. Not every entity has email, first name, or last name, so field-specific methods cannot be universal. Declaring `Optional<Employee> findByEmail(String email)` gives Java the signature and gives Spring Data a method name from which it can derive a query.

The current project repository remains empty; `findByEmail`, `findByFirstNameAndLastName`, and `findByEmailContainingIgnoreCase` are revision examples, not current application behavior.

### Derived query names are a grammar

Spring Data does not interpret arbitrary English. It parses recognized query subjects and keywords around real Java entity properties:

```text
findByFirstNameAndLastName
find             -> query subject
By               -> predicate delimiter
FirstName        -> Employee.firstName
And              -> logical conjunction
LastName         -> Employee.lastName
```

`findByEmailContainingIgnoreCase` combines the `email` property, a contains-style text predicate, and a case-insensitive modifier. Derived methods use Java property names such as `firstName`, not physical database names such as `first_name`.

Useful groups include `And`/`Or`, equality through no suffix or `Is`/`Equals`, range comparisons such as `Between` and `GreaterThan`, text predicates such as `Containing`, `StartingWith`, and `EndingWith`, null checks, `In`/`NotIn`, `True`/`False`, `IgnoreCase`, and `OrderBy...Asc/Desc`.

**Selection rule:** use derived names for short fixed predicates, `@Query` for a moderately complex fixed query, and Specifications/Criteria/Querydsl or a custom repository when filters are dynamic or the method name becomes a puzzle.

### Return types and `save()` semantics

Use `Optional<Employee>` when zero or one match is expected and a collection such as `List<Employee>` when many matches are valid. The return type does not create a database uniqueness constraint; an email that must be unique should be protected by the schema and tested as a business invariant.

There is normally no separate repository `update()` method. `save()` persists new state and merges existing state according to Spring Data JPA's entity-state detection. In the current entity, the nullable generated `Integer id` provides the normal new-state signal when it is `null`.

### Repository-project verification boundary

On 2026-09-20, installed Maven `3.9.16` completed `mvn clean test`: one test passed with no failures, errors, or skips. Startup discovered one repository interface and initialized Spring Data JPA `4.1.1` with Hibernate `7.4.5.Final`. Read-only checks on temporary port `18083` returned HTTP `200` with five employees from `GET /api/employees` and HTTP `200` with ID `1` from `GET /api/employees/1`; the server was stopped and the port was confirmed free.

The Maven Wrapper script still fails in the current Windows environment before Maven starts with `Cannot index into a null array`, so installed Maven remains the documented fallback. Write endpoints and custom derived-query examples were not executed. Startup also warned that local MySQL `5.7.19` is below this Hibernate version's supported minimum of MySQL `8.0.0`.

[Review the complete Spring Data JPA proxy, inherited-method, derived-query, keyword, return-type, and verification notes](04-springboot-rest-crud/03-spring-boot-rest-crud-employee-with-jpa-repository/notes.md).

### Spring Data REST: the repository becomes an HTTP resource

Spring Data JPA supplies the runtime `EmployeeRepository` implementation; Spring Data REST supplies the generic HTTP handlers around that repository. It needs a Spring Data repository but not JPA specifically. A manual `EntityManager` or `JdbcTemplate` DAO is not exported automatically. The current repository's `@RepositoryRestResource(path="members")` and `spring.data.rest.base-path=/rest` combine into `/rest/members`; the project has no application controller or service class. The path rename does not rename HAL relations: the current API root still links under `employees`, and collection data appears under `_embedded.employees`. [Detailed notes](04-springboot-rest-crud/04-spring-boot-rest-crud-employee-with-spring-rest/notes.md)

The standard collection supports GET/POST; an item supports GET/PUT/PATCH/DELETE. Exported custom **read** methods appear below `/search` and use GET. `@RestResource(path=...)` changes the query URL's final segment, `rel` changes a HAL link label, and `exported=false` hides the method. `@Modifying` changes JPA query execution, not the HTTP method. For custom commands or exact DTO responses, map a controller deliberately. A normal `@RestController` is outside Data REST's base path; `@RepositoryRestController` can participate under that path. [Detailed notes](04-springboot-rest-crud/04-spring-boot-rest-crud-employee-with-spring-rest/notes.md)

The active `default-page-size=2` is a fallback, so `?size=5` may override it. Page numbers begin at zero and `offset = page × size`; sorting happens before selecting the page. On 2026-09-21, `GET /rest/members` returned 2 of 5 employees with 3 total pages, while `page=1&size=3` returned the remaining 2. `Page<Employee>` plus `Pageable` gives custom search pagination and totals; `List<Employee>` without `Pageable` does not. `@Param("x")` names a repository query argument (and matches `:x` in explicit named queries), `@RequestParam("x")` binds a controller request parameter, and `@PathVariable("x")` binds `{x}` in a controller route. [Detailed notes](04-springboot-rest-crud/04-spring-boot-rest-crud-employee-with-spring-rest/notes.md)

**Recall rule:** Data REST is a quick repository-shaped CRUD API; a controller gives explicit control over business actions, URLs, and response bodies.

### Springdoc: description and interactive UI for existing routes

Spring Data REST registers this project's `/rest/members` API. The community-maintained Springdoc starter inspects registered HTTP mappings and produces an OpenAPI description; Swagger UI reads that description to display operations and send real HTTP requests. It can document Spring MVC controller routes as well when those routes exist. Springdoc does not itself create employee CRUD operations or expose arbitrary service methods. [Detailed Springdoc notes](04-springboot-rest-crud/05-spring-boot-rest-crud-employee-swagger/notes.md)

The active properties make `/swagger` the UI entry, `/docs` the OpenAPI JSON route, and `/docs.yaml` the YAML route. `spring.data.rest.base-path=/rest` affects the repository API, not these documentation URLs. The current `contextLoads()` test proves startup only; live read-only checks verified the documentation routes and found four generated path keys, including `/rest/members` and `/rest/members/{id}`. **Recall rule:** API route = what runs; OpenAPI document = what is described; Swagger UI = where you inspect and try it. [Detailed Springdoc notes](04-springboot-rest-crud/05-spring-boot-rest-crud-employee-swagger/notes.md)

## 05 — Spring Boot REST Security

Spring Security filters run before the employee controller. Authentication establishes the user; authorization checks that user's authorities. The project first used an `InMemoryUserDetailsManager` and now actively publishes a `JdbcUserDetailsManager`. Spring Boot builds a pooled `DataSource` from `spring.datasource.*`; the JDBC manager uses it to load account credentials/status from `users` and permissions from `authorities`. The employee table remains business data and does not automatically provide login accounts. [Detailed security notes](05-spring-boot-rest-security/00-spring-boot-rest-security-employee-starter-code/notes.md)

`UserDetailsService` loads an account, while `UserDetailsManager` extends it with account-management operations. `hasRole("MANAGER")` checks for `ROLE_MANAGER`; `hasAuthority("MANAGER")` checks the exact string `MANAGER`. The `ROLE_` prefix is therefore part of the role shortcut convention, not a requirement for every authority. The currently active JDBC bean also makes Boot's property-defined development user back off; it does not copy that user into the database. [Detailed security notes](05-spring-boot-rest-security/00-spring-boot-rest-security-employee-starter-code/notes.md)

The previous `DemoSecurity` class is now fully commented, including its in-memory users, custom `SecurityFilterChain`, request matchers, HTTP Basic call, and CSRF setting. Those lines document the earlier lesson but are not active beans. If both in-memory and JDBC managers are enabled, Spring Security does not automatically merge them or search them in sequence; an intentional multi-store design needs explicit provider/delegation configuration. **Recall rule:** manager = where security accounts come from; `DataSource` = how JDBC obtains connections; `users` = identity/status; `authorities` = permissions. [Detailed security notes](05-spring-boot-rest-security/00-spring-boot-rest-security-employee-starter-code/notes.md)

## Quick recall across sections

1. Which two properties determine the port and application-wide URL prefix?
2. Why can an exposed `/actuator/info` endpoint return `{}`?
3. What is the difference between excluding an Actuator endpoint from web exposure and setting its access to `none`?
4. When should a group of `@Value` fields become `@ConfigurationProperties`?
5. What does `contextLoads()` fail to test?
6. What is the relationship between IoC, the application context, beans, and DI?
7. Why is constructor injection preferred for a required dependency?
8. Why can a constructor-injected field be `final` while a setter-injected field cannot?
9. Why does an interface dependency become ambiguous after several implementations are registered as beans?
10. What is the difference between `@Qualifier` and `@Primary`?
11. Why must the original package be listed after explicit `scanBasePackages` values are supplied?
12. Why can a missing controller mapping appear in TRACE logs as a missing static resource?
13. Why does a qualifier select tennis even when cricket is primary?
14. Why do two primary beans of the same eligible type still cause ambiguity?
15. Why can a bean marked `@Lazy` still be constructed during startup?
16. What is the difference between class-level and constructor-parameter-level `@Lazy`?
17. Does `super()` call another constructor in the same class or in the superclass?
18. Why can adding an empty no-argument constructor break a blank `final` dependency field?
19. What does global lazy initialization change, and what does it leave eager?
20. Why did the prototype scope exercise return `false`?
21. Why does repeatedly calling the singleton scope controller not inject new prototypes each time?
22. What is the difference between `@PostConstruct`, `@PreDestroy`, and Java garbage collection?
23. Why does Spring automatically destroy singleton beans but not prototype beans?
24. What does the context retain for a prototype: an instance cache or a bean definition?
25. Why is the current `SwimCoach` bean named `aqua` instead of `SwimCoach`?
26. When is `@Bean` more suitable than `@Component`?
27. What is the relationship between JPA, Hibernate, Spring Data JPA, JDBC, and MySQL?
28. Why is an entity's no-argument constructor required?
29. Why does placing `@Id` on a field select field access?
30. How do explicit `@Column` names protect Java refactors?
31. What is the difference between `AUTO` and `IDENTITY`?
32. Which current SQL feature makes `IDENTITY` the correct strategy for `Student`?
33. What extra database resource does `SEQUENCE` use? What does `TABLE` use?
34. Why do generator annotations not guarantee that database objects are created?
35. What is the difference between generator `allocationSize` and JDBC batch size?
36. Why can generated primary keys contain gaps?
37. What state can `Integer id = null` represent that primitive `int` cannot?
38. What did the initial passing `contextLoads()` test verify before DAO runner experiments were activated?
39. Why inject `StudentDAO` instead of `StudentDAOImpl`?
40. What extra meaning does `@Repository` add beyond generic component registration?
41. Why does `save()` need a transaction while a basic `find()` does not require one?
42. Why did lowering `AUTO_INCREMENT` fail after IDs above `1000` existed?
43. How do `DELETE`, `TRUNCATE`, and `DROP TABLE` differ?
44. Which names do JPQL and HQL use: Java entity attributes or SQL columns?
45. What is the portability difference between JPQL and HQL?
46. Why does `TypedQuery<Student>` produce `List<Student>`?
47. Why is `TypedQuery<List<Student>>` incorrect for a normal entity query?
48. When can a `where` clause use a literal, and when should it use `setParameter()`?
49. Why did Hibernate report token `B` in `lastName = lastName B`?
50. Why can a `contextLoads()` test fail because of `CommandLineRunner` code?
51. What is the difference between a managed and detached entity?
52. When does dirty checking remove the need for `merge()`?
53. What object does `merge()` return, and why should later code use it?
54. Why does the current DAO find an existing student before merging?
55. Why does `deleteById()` find the entity before calling `remove()`?
56. How does JPQL bulk delete differ from removing entities one at a time?
57. What does `executeUpdate()` return?
58. Why can the current `contextLoads()` mutate the database rather than merely check startup?
59. What does `logging.level.org.hibernate.SQL=DEBUG` display?
60. What extra information does `logging.level.org.hibernate.orm.jdbc.bind=TRACE` display, and what is its risk?
61. What are the five supported `ddl-auto` values?
62. How do `update` and `create-drop` differ at startup and shutdown?
63. Which `ddl-auto` value verifies mappings without changing schema objects?
64. Why does `ddl-auto` not replace creation of the MySQL database and user?
65. Why are versioned migrations plus `validate` or `none` preferred for important production data?
66. What is the complete URL of the current REST endpoint?
67. Why does the final path contain both `/test` and `/hello`?
68. What three features are combined by `@SpringBootApplication`?
69. What are the roles of embedded Tomcat and the `DispatcherServlet`?
70. Why does `@RestController` write `Hello World` to the response body instead of treating it as a view name?
71. Why is the current `@RequestMapping("/hello")` not GET-only?
72. What is the difference between routing and HTTP message conversion?
73. What is the difference between serialization and deserialization?
74. How do marshalling and unmarshalling relate to those directions?
75. Why is Jackson available even though it is not declared directly in the project POM?
76. Does the current `String` response prove POJO-to-JSON serialization?
77. What does `@RequestBody` ask Spring to do?
78. Why does the current `contextLoads()` test not prove the endpoint response?
79. When does the current `@PostConstruct` method populate the student list?
80. Why does `List<Student>` become a JSON array without manually constructing JSON text?
81. What restriction does `@GetMapping` add compared with path-only `@RequestMapping`?
82. What does `{studentId}` represent in `/api/students/{studentId}`?
83. When can the name be omitted from `@PathVariable`?
84. Why may the Java parameter be named `id` in `@PathVariable("studentId") int id`?
85. What is the relationship between `name` and `value` in `@PathVariable`?
86. Why do identical GET paths with `int` and `String` parameters conflict during startup?
87. In what order do route matching, path-variable conversion, and method invocation occur?
88. What does `{variable:regex}` change, and what does it not change?
89. Why is the current `studentId` really a zero-based list index?
90. Why does `abcd` produce `400` while index `3` now produces `404`?
91. What responsibilities belong to the custom exception, error-response DTO, and exception-handler method?
92. How can an empty `@ExceptionHandler` annotation infer the exception it handles?
93. Must an exception type appear in both `@ExceptionHandler(...)` and the method parameter?
94. How are multiple exception types listed in `@ExceptionHandler`?
95. Why is `A | B ex` invalid as an exception-handler method parameter?
96. What does `StudentErrorResponse` mean inside `ResponseEntity<StudentErrorResponse>`?
97. Why does setting the JSON body's `status` field not set the real HTTP status?
98. When should handlers be moved into `@RestControllerAdvice`?
99. Why is a broad `Exception` handler that always returns `400` risky?
100. What standardized Spring 7 alternatives exist for REST error bodies?
101. When is a controller-advice bean registered, and when does one of its handler methods execute?
102. Which is checked first: a local `@ExceptionHandler` or global advice?
103. What two annotations are combined by `@RestControllerAdvice`?
104. Why does the current `@ControllerAdvice` work without class-level `@ResponseBody`?
105. Does `@RestControllerAdvice` automatically construct `StudentErrorResponse`?
106. How do a DTO's `status` field, `ResponseEntity.status(...)`, and `@ResponseStatus` differ?
107. What happens to an unset primitive `int status` field?
108. When is `ResponseEntity` more suitable than `@ResponseStatus`?
109. What does a directly returned DTO require under plain `@ControllerAdvice`?
110. Which attributes can restrict controller-advice scope?
111. Are multiple advice selectors combined with AND or OR?
112. Why does the class name `StudentRestExceptionHandler` not restrict the advice to student requests?
113. What responsibility belongs to the employee controller, service, DAO, and entity?
114. What does `@Service` add beyond the general `@Component` stereotype?
115. Does `@Service` automatically make its methods transactional?
116. Why is the service layer commonly chosen as the transaction boundary?
117. Why can the current find methods work without method-level `@Transactional`?
118. What does `@RequestBody Employee employee` ask Spring MVC to do?
119. Why may an employee POST body omit `id` even though the entity has four fields?
120. What happens if a POST client supplies an ID in the current implementation?
121. Why is this project's generated ID an identity/AUTO_INCREMENT value rather than a sequence?
122. Why can POST and PUT both delegate to the same `save()` method?
123. Does JPA know whether the incoming HTTP request used POST or PUT?
124. Why does `merge()` return an entity, and why should callers use that returned instance?
125. Why does the current PUT route not guarantee update-only behavior?
126. Why does an unknown employee ID currently return `500` instead of `404`?
127. Why does `EntityManager.remove()` require an entity instance rather than an integer ID?
128. What does the employee `contextLoads()` test prove, and what does it leave untested?
129. What intent does PATCH communicate that differs from PUT?
130. Why does the PATCH endpoint use both a path variable and a request body?
131. Why is `id` rejected from the current patch map?
132. Why is `Map<String, Object>` useful for detecting which JSON properties were supplied?
133. How does `{}` differ from `{"email": null}`?
134. Can PATCH receive an `Employee` instead of a map, and what information would be lost without presence tracking?
135. Is `JsonMapper` a Spring class or a Jackson class?
136. Why can Spring inject the current `JsonMapper` without an application-defined `@Bean` method?
137. What does `jsonMapper.updateValue()` do, and what does it not do?
138. Why does DELETE pass an integer ID through the controller, service, and DAO contracts?
139. Why does the DAO call `find()` inside the delete transaction before `remove()`?
140. Why do the passing context tests still not prove that PATCH and DELETE work over HTTP?
141. What project code was removed when `EmployeeDao` and `EmployeeDaoImpl` were replaced, and what persistence work still occurs?
142. What do the two generic arguments in `JpaRepository<Employee, Integer>` represent?
143. Why can the service call `findById()` without declaring it in `EmployeeRepository`?
144. What runtime object satisfies the `EmployeeRepository` dependency?
145. Why must `findByEmail()` be declared even though `findById()` is inherited?
146. What two audiences use a derived-query method declaration?
147. Why is `findByFirstNameAndLastName` a grammar rather than an English sentence?
148. Which method segment identifies a Java property, and which segment adds the logical conjunction?
149. What do `Containing` and `IgnoreCase` contribute to `findByEmailContainingIgnoreCase`?
150. Why should a derived method use `FirstName` instead of the database column spelling `first_name`?
151. When should a query return `Optional<Employee>` instead of `List<Employee>`?
152. Does an `Optional<Employee>` return type enforce a unique database value?
153. Why is there normally no separate `update()` method on `JpaRepository`?
154. At what point should a long derived name become `@Query` or a dynamic-query abstraction?
155. What did the current `contextLoads()` test and the two read-only HTTP checks prove, and what remains untested?

156. Why does `/rest/members` work without an application controller?
157. Does `path="members"` also rename the `_embedded` relation?
158. What are the collection and item HTTP routes supplied by Data REST?
159. Why should a custom delete-by-name operation not be exported as a GET search resource?
160. What does `@Modifying` change, and what does it not change?
161. With five employees, what does `page=1&size=3` return?
162. Why can `size=5` override `default-page-size=2`?
163. Which annotation reads `?familyName=...` in a controller, and which reads `{employeeId}`?
164. Which project component creates `/rest/members`, and which library generates its OpenAPI description?
165. What is the difference between `/rest/members`, `/docs`, and `/swagger`?
166. With `springdoc.api-docs.path=/docs`, where is the YAML document?
167. Will a new service method appear in Swagger UI without an HTTP mapping?
168. Why should you be careful when using **Try it out** for `DELETE`?
169. Which bean replaces Boot's single property-configured security user?
170. What is the difference between `@Configuration` and a method annotated `@Bean`?
171. Why can Susan satisfy `hasRole("MANAGER")`?
172. What does `{noop}` mean, and why is it for lessons only?
173. Why does `http.cors(csrf -> csrf.disable())` leave CSRF active?
174. What does `contextLoads()` fail to prove about the POST endpoint?
175. What is the difference between `UserDetailsService`, `UserDetailsManager`, and `JdbcUserDetailsManager`?
176. Who creates the `DataSource`, and what does the JDBC user manager use it for?
177. Why are security accounts split between `users` and `authorities`?
178. What authority does `hasRole("MANAGER")` check, and how does `hasAuthority("MANAGER")` differ?
179. Does declaring both in-memory and JDBC managers make Spring Security search both automatically?
180. Which parts of the previous security lesson became inactive when all of `DemoSecurity` was commented?

Answers and runnable examples are in the [Section 01 project notes](01-spring-boot-basics/springBootApp/notes.md), [Section 02 project notes](02-spring-boot-core/coach/notes.md), [Section 03 project notes](03-spring-boot-hibernate-jpa-crud/01-cruddemo-student/notes.md), the [Section 04 REST foundations](04-springboot-rest-crud/01-spring-boot-rest-crud/notes.md), the [Section 04 manual employee REST/JPA notes](04-springboot-rest-crud/02-spring-boot-rest-crud-employee/notes.md), the [Section 04 Spring Data JPA repository notes](04-springboot-rest-crud/03-spring-boot-rest-crud-employee-with-jpa-repository/notes.md), the [Section 04 Spring Data REST notes](04-springboot-rest-crud/04-spring-boot-rest-crud-employee-with-spring-rest/notes.md), the [Section 04 Springdoc notes](04-springboot-rest-crud/05-spring-boot-rest-crud-employee-swagger/notes.md), and the [Section 05 security notes](05-spring-boot-rest-security/00-spring-boot-rest-security-employee-starter-code/notes.md).
