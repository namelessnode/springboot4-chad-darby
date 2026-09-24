# Spring Boot Course — Chad Darby

This repository is a module-by-module learning journal for the Spring Boot course. Numbered section folders preserve the course order, each runnable project keeps its detailed notes beside its source, and [`COURSE_REVIEW.md`](COURSE_REVIEW.md) collects the most useful ideas for quick revision.

## How the repository is organized

```text
chadDarby/
├── README.md
├── COURSE_REVIEW.md
├── AGENTS.md
└── NN-section-name/
    └── projectName/
        ├── pom.xml
        ├── src/
        └── notes.md
```

- **Section folders** use course order: `01-...`, `02-...`, `03-...`, `04-...`.
- **Project notes** contain detailed code, properties, commands, observed results, mistakes, and recall questions.
- **Course review** connects concepts across projects without duplicating all project details.

## Course progress

| Section | Project | Main topics | Status |
|---|---|---|---|
| 01 — Spring Boot Basics | [`springBootApp`](01-spring-boot-basics/springBootApp/) | Application startup, Spring MVC endpoints, external properties, embedded server settings, Actuator, and context testing | Complete |
| 02 — Spring Boot Core | [`coach`](02-spring-boot-core/coach/) | IoC, dependency injection, bean selection, global lazy initialization, scopes, lifecycle callbacks, Java configuration, and REST controllers | Complete |
| 03 — Hibernate and Spring Data JPA CRUD | [`cruddemo-student`](03-spring-boot-hibernate-jpa-crud/01-cruddemo-student/) | MySQL setup, entity mapping, generated IDs, manual DAO CRUD, transactions, HQL/JPQL, updates/deletes, Hibernate SQL diagnostics, and automatic schema management | Complete |
| 04 — Spring REST CRUD | [`01-spring-boot-rest-crud`](04-springboot-rest-crud/01-spring-boot-rest-crud/) | Spring MVC routing, Jackson 3 serialization, path variables, custom exceptions, global controller advice, `ResponseEntity`, `@ResponseStatus`, and HTTP status codes | In progress |
| 04 — Spring REST CRUD | [`02-spring-boot-rest-crud-employee`](04-springboot-rest-crud/02-spring-boot-rest-crud-employee/) | Layered REST/JPA CRUD, service transactions, generated IDs, PUT versus PATCH, Jackson partial updates, and transactional deletion | In progress |
| 04 — Spring REST CRUD | [`03-spring-boot-rest-crud-employee-with-jpa-repository`](04-springboot-rest-crud/03-spring-boot-rest-crud-employee-with-jpa-repository/) | Spring Data JPA repository proxies, inherited CRUD methods, `JpaRepository` generics, derived query names, query keywords, and return types | In progress |
| 04 — Spring REST CRUD | [`04-spring-boot-rest-crud-employee-with-spring-rest`](04-springboot-rest-crud/04-spring-boot-rest-crud-employee-with-spring-rest/) | Spring Data REST generated CRUD, repository URL customization, HAL links, paging/sorting, query method search, and request parameter annotations | In progress |
| 04 — Spring REST CRUD | [`05-spring-boot-rest-crud-employee-swagger`](04-springboot-rest-crud/05-spring-boot-rest-crud-employee-swagger/) | Springdoc OpenAPI 3.1, generated JSON/YAML API descriptions, Swagger UI, custom documentation paths, and interactive API exploration | In progress |
| 05 — Spring Boot REST Security | [`00-spring-boot-rest-security-employee-starter-code`](05-spring-boot-rest-security/00-spring-boot-rest-security-employee-starter-code/) | Default and in-memory users, JDBC authentication, custom security schemas and queries, enterprise identity sources, HTTP Basic, role rules, and CSRF | In progress |

## Section 01 — Spring Boot Basics

### `springBootApp`

A first Spring Boot web application with three GET endpoints, custom values read from `application.properties`, an application-specific port and context path, and Spring Boot Actuator.

- [Detailed project notes](01-spring-boot-basics/springBootApp/notes.md)
- [Maven configuration](01-spring-boot-basics/springBootApp/pom.xml)
- [Application properties](01-spring-boot-basics/springBootApp/src/main/resources/application.properties)

## Section 02 — Spring Boot Core

### `coach`

A Spring Core learning project built around a `Coach` interface, four component implementations, and a `SwimCoach` registered through `@Bean`. It demonstrates the application context, constructor and setter injection, explicit scan roots, bean selection, global and selective lazy initialization, singleton and prototype scopes, lifecycle callbacks, Java-based configuration, and REST request flow.

- [Detailed project notes](02-spring-boot-core/coach/notes.md)
- [Maven configuration](02-spring-boot-core/coach/pom.xml)
- [Application properties](02-spring-boot-core/coach/src/main/resources/application.properties)

## Section 03 — Hibernate and Spring Data JPA CRUD

### `cruddemo-student`

The first data-persistence project connects Spring Boot to the local `student_tracker` MySQL database and maps the `student` table to a Jakarta Persistence entity. The project now includes a manual `StudentDAO`/`StudentDAOImpl`, constructor-injected `EntityManager`, transactional inserts and updates, primary-key retrieval, ordered HQL and JPQL queries, typed results, named parameters, managed-versus-detached entity behavior, `merge()` and dirty checking, single-entity removal, JPQL bulk deletion with an affected-row count, MySQL `AUTO_INCREMENT` experiments, targeted Hibernate SQL/bind-value logging, and the five `spring.jpa.hibernate.ddl-auto` schema modes with production migration guidance.

- [Detailed project notes](03-spring-boot-hibernate-jpa-crud/01-cruddemo-student/notes.md)
- [Maven configuration](03-spring-boot-hibernate-jpa-crud/01-cruddemo-student/pom.xml)
- [Student entity](03-spring-boot-hibernate-jpa-crud/01-cruddemo-student/src/main/java/com/example/cruddemo/entity/Student.java)
- [DAO contract](03-spring-boot-hibernate-jpa-crud/01-cruddemo-student/src/main/java/com/example/cruddemo/dao/StudentDAO.java)
- [DAO implementation](03-spring-boot-hibernate-jpa-crud/01-cruddemo-student/src/main/java/com/example/cruddemo/dao/StudentDAOImpl.java)
- [Application properties](03-spring-boot-hibernate-jpa-crud/01-cruddemo-student/src/main/resources/application.properties)
- [Course SQL scripts](03-spring-boot-hibernate-jpa-crud/00-starter-sql-scripts/00-starter-sql-scripts/)

## Section 04 — Spring REST CRUD

### `01-spring-boot-rest-crud`

The first Spring REST project starts a Spring MVC application and currently exposes `/test/hello`, `GET /api/students`, and `GET /api/students/{studentId}`. The lessons trace startup and request dispatch, then demonstrate POJO-to-JSON serialization, in-memory data initialized with `@PostConstruct`, GET-specific mappings, URI variables and conversion, duplicate-mapping rules, custom exceptions, structured error bodies, and the refactoring of local handlers into global `@ControllerAdvice`. The notes compare `@ControllerAdvice` with `@RestControllerAdvice`, distinguish JSON status data from the real HTTP status, and compare `ResponseEntity` with `@ResponseStatus`. Invalid indexes return `404`; non-numeric indexes return `400` through the current fallback handler.

- [Detailed project notes](04-springboot-rest-crud/01-spring-boot-rest-crud/notes.md)
- [Maven configuration](04-springboot-rest-crud/01-spring-boot-rest-crud/pom.xml)
- [Application entry point](04-springboot-rest-crud/01-spring-boot-rest-crud/src/main/java/com/example/rest/RestApplication.java)
- [Demo REST controller](04-springboot-rest-crud/01-spring-boot-rest-crud/src/main/java/com/example/rest/controller/DemoRestController.java)
- [Student REST controller](04-springboot-rest-crud/01-spring-boot-rest-crud/src/main/java/com/example/rest/controller/StudentRestController.java)
- [Global student exception handler](04-springboot-rest-crud/01-spring-boot-rest-crud/src/main/java/com/example/rest/controller/StudentRestExceptionHandler.java)
- [Student POJO](04-springboot-rest-crud/01-spring-boot-rest-crud/src/main/java/com/example/rest/entity/Student.java)
- [Student-not-found exception](04-springboot-rest-crud/01-spring-boot-rest-crud/src/main/java/com/example/rest/entity/StudentNotFoundException.java)
- [Student error response](04-springboot-rest-crud/01-spring-boot-rest-crud/src/main/java/com/example/rest/entity/StudentErrorResponse.java)
- [Application properties](04-springboot-rest-crud/01-spring-boot-rest-crud/src/main/resources/application.properties)

### `02-spring-boot-rest-crud-employee`

This project connects REST endpoints to the `employee_directory` MySQL database through a controller-service-DAO architecture. The current lesson code implements GET, POST, PUT, PATCH, and DELETE; demonstrates JSON request-body binding, generated IDs, map-based partial updates with Spring Boot's Jackson 3 `JsonMapper`, and ID-based transactional deletion. The detailed notes preserve the current missing-ID limitation and distinguish observed behavior from code-derived behavior.

- [Detailed project notes](04-springboot-rest-crud/02-spring-boot-rest-crud-employee/notes.md)
- [Maven configuration](04-springboot-rest-crud/02-spring-boot-rest-crud-employee/pom.xml)
- [Employee REST controller](04-springboot-rest-crud/02-spring-boot-rest-crud-employee/src/main/java/com/example/cruddemo/rest/EmployeeRestController.java)
- [Service contract](04-springboot-rest-crud/02-spring-boot-rest-crud-employee/src/main/java/com/example/cruddemo/service/EmployeeService.java)
- [Service implementation](04-springboot-rest-crud/02-spring-boot-rest-crud-employee/src/main/java/com/example/cruddemo/service/EmployeeServiceImpl.java)
- [DAO contract](04-springboot-rest-crud/02-spring-boot-rest-crud-employee/src/main/java/com/example/cruddemo/dao/EmployeeDao.java)
- [DAO implementation](04-springboot-rest-crud/02-spring-boot-rest-crud-employee/src/main/java/com/example/cruddemo/dao/EmployeeDaoImpl.java)
- [Employee entity](04-springboot-rest-crud/02-spring-boot-rest-crud-employee/src/main/java/com/example/cruddemo/entity/Employee.java)
- [Application properties](04-springboot-rest-crud/02-spring-boot-rest-crud-employee/src/main/resources/application.properties)
- [Employee database script](04-springboot-rest-crud/spring-boot-employee-sql-script/employee-directory.sql)

### `03-spring-boot-rest-crud-employee-with-jpa-repository`

This project replaces the hand-written `EmployeeDao`/`EmployeeDaoImpl` with `EmployeeRepository extends JpaRepository<Employee, Integer>`. It demonstrates how Spring Boot discovers repository interfaces, how Spring Data creates a runtime proxy and routes inherited CRUD methods to its JPA base implementation, why `findById()` is inherited while employee-specific methods such as `findByEmail()` must be declared, and how the derived-query naming grammar combines entity properties with keywords such as `And`, `Containing`, and `IgnoreCase`. The notes include a practical keyword reference, return-type rules, transaction behavior, the boundary between derived methods and `@Query`, and observed clean-build and read-only HTTP verification.

- [Detailed project notes](04-springboot-rest-crud/03-spring-boot-rest-crud-employee-with-jpa-repository/notes.md)
- [Maven configuration](04-springboot-rest-crud/03-spring-boot-rest-crud-employee-with-jpa-repository/pom.xml)
- [Employee repository](04-springboot-rest-crud/03-spring-boot-rest-crud-employee-with-jpa-repository/src/main/java/com/example/cruddemo/repository/EmployeeRepository.java)
- [Service implementation](04-springboot-rest-crud/03-spring-boot-rest-crud-employee-with-jpa-repository/src/main/java/com/example/cruddemo/service/EmployeeServiceImpl.java)
- [Employee REST controller](04-springboot-rest-crud/03-spring-boot-rest-crud-employee-with-jpa-repository/src/main/java/com/example/cruddemo/rest/EmployeeRestController.java)
- [Employee entity](04-springboot-rest-crud/03-spring-boot-rest-crud-employee-with-jpa-repository/src/main/java/com/example/cruddemo/entity/Employee.java)
- [Application properties](04-springboot-rest-crud/03-spring-boot-rest-crud-employee-with-jpa-repository/src/main/resources/application.properties)
- [Employee database script](04-springboot-rest-crud/spring-boot-employee-sql-script/employee-directory.sql)

### `04-spring-boot-rest-crud-employee-with-spring-rest`

This project adds Spring Data REST to a JPA repository and removes the application controller/service layer for basic CRUD. Its active URL is `/rest/members`, with a default page size of two. The notes explain generated collection/item routes, HAL and HATEOAS, search-method boundaries, page/size/sort rules, and `@Param` versus MVC `@RequestParam` and `@PathVariable`. Read-only HTTP checks confirmed the renamed path, five stored employees, paging, sorting, HAL links, and the old path's `404`. The project remains in progress; custom query examples and write operations are not implemented or live-tested.

- [Detailed Spring Data REST notes](04-springboot-rest-crud/04-spring-boot-rest-crud-employee-with-spring-rest/notes.md)
- [Maven configuration](04-springboot-rest-crud/04-spring-boot-rest-crud-employee-with-spring-rest/pom.xml)
- [Employee repository](04-springboot-rest-crud/04-spring-boot-rest-crud-employee-with-spring-rest/src/main/java/com/example/cruddemo/repository/EmployeeRepository.java)
- [Employee entity](04-springboot-rest-crud/04-spring-boot-rest-crud-employee-with-spring-rest/src/main/java/com/example/cruddemo/entity/Employee.java)
- [Application properties](04-springboot-rest-crud/04-spring-boot-rest-crud-employee-with-spring-rest/src/main/resources/application.properties)

### `05-spring-boot-rest-crud-employee-swagger`

This project keeps the Spring Data REST employee resources and adds Springdoc's Web MVC UI starter. `/rest/members` is still the actual employee API; `/docs` and `/docs.yaml` describe its operations, while `/swagger` opens Swagger UI for browsing and sending real requests. The detailed notes explain the dependency's community ownership, the startup/request flow, custom URL properties, observed HTTP responses, and the current test boundary.

- [Detailed Springdoc notes](04-springboot-rest-crud/05-spring-boot-rest-crud-employee-swagger/notes.md)
- [Maven configuration](04-springboot-rest-crud/05-spring-boot-rest-crud-employee-swagger/pom.xml)
- [Employee repository](04-springboot-rest-crud/05-spring-boot-rest-crud-employee-swagger/src/main/java/com/example/cruddemo/repository/EmployeeRepository.java)
- [Employee entity](04-springboot-rest-crud/05-spring-boot-rest-crud-employee-swagger/src/main/java/com/example/cruddemo/entity/Employee.java)
- [Application properties](04-springboot-rest-crud/05-spring-boot-rest-crud-employee-swagger/src/main/resources/application.properties)

## Section 05 — Spring Boot REST Security

### `00-spring-boot-rest-security-employee-starter-code`

This project preserves the progression from Boot's development user to in-memory users and now to JDBC-backed authentication. The active `DemoJdbcSecurity` publishes a `JdbcUserDetailsManager` connected through Boot's `DataSource` and maps Spring Security's expected result shape onto custom `members` and `roles` tables. The earlier in-memory manager and custom request rules remain in the source as commented lesson history. The notes cover default and custom schemas, enabled-account handling, role-prefix rules, normalized enterprise role models, external identity sources, and why multiple user-manager beans are not automatically combined.

- [Detailed security notes](05-spring-boot-rest-security/00-spring-boot-rest-security-employee-starter-code/notes.md)
- [Active JDBC security configuration](05-spring-boot-rest-security/00-spring-boot-rest-security-employee-starter-code/src/main/java/com/luv2code/springboot/cruddemo/security/DemoJdbcSecurity.java)
- [Custom JDBC security schema](05-spring-boot-rest-security/00-spring-boot-rest-security-employee-starter-code/sql-scripts/06-setup-spring-security-demo-database-bcrypt-custom-table-names.sql)
- [Commented in-memory and request-rule lesson](05-spring-boot-rest-security/00-spring-boot-rest-security-employee-starter-code/src/main/java/com/luv2code/springboot/cruddemo/security/DemoSecurity.java)
- [Maven configuration](05-spring-boot-rest-security/00-spring-boot-rest-security-employee-starter-code/pom.xml)

## How to study from this repository

1. Start with this page to select the next section or project.
2. Run the project and reproduce the examples in its `notes.md`.
3. Use the active-recall questions before rereading the answers.
4. Use [`COURSE_REVIEW.md`](COURSE_REVIEW.md) for quick revision across completed sections.
5. Return to a project's notes when you need exact code, properties, commands, or troubleshooting details.

## Adding the next module

When the course moves to the next module:

1. Create the next numbered folder, for example `04-next-course-module`.
2. Place each runnable project inside that section.
3. Add a `notes.md` to each meaningful project.
4. Update the progress table and section links in this file.
5. Add newly learned concepts to `COURSE_REVIEW.md` and link them back to the detailed notes.
