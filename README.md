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

- **Section folders** use course order: `01-...`, `02-...`, `03-...`.
- **Project notes** contain detailed code, properties, commands, observed results, mistakes, and recall questions.
- **Course review** connects concepts across projects without duplicating all project details.

## Course progress

| Section | Project | Main topics | Status |
|---|---|---|---|
| 01 — Spring Boot Basics | [`springBootApp`](01-spring-boot-basics/springBootApp/) | Application startup, Spring MVC endpoints, external properties, embedded server settings, Actuator, and context testing | Complete |
| 02 — Spring Boot Core | [`coach`](02-spring-boot-core/coach/) | IoC, dependency injection, bean selection, global lazy initialization, scopes, lifecycle callbacks, Java configuration, and REST controllers | Complete |
| 03 — Hibernate and Spring Data JPA CRUD | [`cruddemo-student`](03-spring-boot-hibernate-jpa-crud/01-cruddemo-student/) | MySQL setup, entity mapping, generated IDs, manual DAO CRUD, transactions, HQL/JPQL, updates/deletes, Hibernate SQL diagnostics, and automatic schema management | In progress |

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
