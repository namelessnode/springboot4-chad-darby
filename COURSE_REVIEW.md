# Spring Boot Course — Cumulative Review

This file is the short revision layer across all course sections. Detailed code, complete property explanations, runtime observations, and troubleshooting remain in each project's `notes.md`.

## Version snapshots

| Section and project | Spring Boot | Java | Build tool | Detailed notes |
|---|---:|---:|---|---|
| 01 — `springBootApp` | 4.1.1 | 26 | Maven Wrapper 3.9.16 | [Project notes](01-spring-boot-basics/springBootApp/notes.md) |
| 02 — `coach` | 4.1.1 | 26 | Maven Wrapper 3.9.16 | [Project notes](02-spring-boot-core/coach/notes.md) |
| 03 — `cruddemo-student` | 4.1.1 | Target 25; verified on 26.0.1 | Wrapper 3.9.16; installed Maven fallback | [Project notes](03-spring-boot-hibernate-jpa-crud/01-cruddemo-student/notes.md) |

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

Answers and runnable examples are in the [Section 01 project notes](01-spring-boot-basics/springBootApp/notes.md), [Section 02 project notes](02-spring-boot-core/coach/notes.md), and [Section 03 project notes](03-spring-boot-hibernate-jpa-crud/01-cruddemo-student/notes.md).
