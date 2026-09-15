# Spring Boot Course — Cumulative Review

This file is the short revision layer across all course sections. Detailed code, complete property explanations, runtime observations, and troubleshooting remain in each project's `notes.md`.

## Version snapshots

| Section and project | Spring Boot | Java | Build tool | Detailed notes |
|---|---:|---:|---|---|
| 01 — `springBootApp` | 4.1.1 | 26 | Maven Wrapper 3.9.16 | [Project notes](01-spring-boot-basics/springBootApp/notes.md) |
| 02 — `coach` | 4.1.1 | 26 | Maven Wrapper 3.9.16 | [Project notes](02-spring-boot-core/coach/notes.md) |

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

[Compare all three dependency-injection styles](02-spring-boot-core/coach/notes.md#7-dependency-injection-styles).

### Resolving multiple implementations

Injection by interface works automatically while there is one matching bean. With several `Coach` beans, Spring needs another selection rule:

- `@Qualifier` narrows candidates for one injection point.
- `@Primary` marks the normal default for unqualified single-bean injection.
- `List<Coach>` or `Map<String, Coach>` can intentionally receive multiple implementations.

Default component bean names normally use lower camel case, such as `CricketCoach` → `cricketCoach`. Qualifier values are case-sensitive. A qualifier is local, so fixing one controller does not resolve ambiguous dependencies in other controller beans.

[Review the four-bean failure, qualifier convention, and alternatives](02-spring-boot-core/coach/notes.md#8-multiple-beans-ambiguity-qualifiers-and-primary-beans).

### REST controller request flow

`@RestController` is effectively `@Controller` plus `@ResponseBody`. Spring MVC maps the incoming GET path to a handler method; the controller calls its already-injected coach bean; the returned `String` is written into the HTTP response body.

The dependency is selected while Spring creates the controller bean, not once per HTTP request.

[Review the request sequence and current endpoints](02-spring-boot-core/coach/notes.md#9-restcontroller-controller-and-the-http-request-path).

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

Answers and runnable examples are in the [Section 01 project notes](01-spring-boot-basics/springBootApp/notes.md) and [Section 02 project notes](02-spring-boot-core/coach/notes.md).
