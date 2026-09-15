# Spring Boot Course — Cumulative Review

This file is the short revision layer across all course sections. Detailed code, complete property explanations, runtime observations, and troubleshooting remain in each project's `notes.md`.

## Version snapshots

| Section and project | Spring Boot | Java | Build tool | Detailed notes |
|---|---:|---:|---|---|
| 01 — `springBootApp` | 4.1.1 | 26 | Maven Wrapper 3.9.16 | [Project notes](01-spring-boot-basics/springBootApp/notes.md) |

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

## Quick recall

1. Which two properties determine the port and application-wide URL prefix?
2. Why can an exposed `/actuator/info` endpoint return `{}`?
3. What is the difference between excluding an Actuator endpoint from web exposure and setting its access to `none`?
4. When should a group of `@Value` fields become `@ConfigurationProperties`?
5. What does `contextLoads()` fail to test?

Answers and runnable examples are in the [Section 01 project notes](01-spring-boot-basics/springBootApp/notes.md).

