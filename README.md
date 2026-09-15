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

## Section 01 — Spring Boot Basics

### `springBootApp`

A first Spring Boot web application with three GET endpoints, custom values read from `application.properties`, an application-specific port and context path, and Spring Boot Actuator.

- [Detailed project notes](01-spring-boot-basics/springBootApp/notes.md)
- [Maven configuration](01-spring-boot-basics/springBootApp/pom.xml)
- [Application properties](01-spring-boot-basics/springBootApp/src/main/resources/application.properties)

## How to study from this repository

1. Start with this page to select the next section or project.
2. Run the project and reproduce the examples in its `notes.md`.
3. Use the active-recall questions before rereading the answers.
4. Use [`COURSE_REVIEW.md`](COURSE_REVIEW.md) for quick revision across completed sections.
5. Return to a project's notes when you need exact code, properties, commands, or troubleshooting details.

## Adding the next module

When the course moves to the next module:

1. Create the next numbered folder, for example `02-spring-core`.
2. Place each runnable project inside that section.
3. Add a `notes.md` to each meaningful project.
4. Update the progress table and section links in this file.
5. Add newly learned concepts to `COURSE_REVIEW.md` and link them back to the detailed notes.

