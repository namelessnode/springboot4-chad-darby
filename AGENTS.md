# Chad Darby Spring Boot Course — Agent Instructions

## Build and Run

The instructor folder contains numbered course sections, and each section contains one or more independently runnable projects. Run commands from the project being studied; for the current project:

```powershell
Set-Location .\01-spring-boot-basics\springBootApp
.\mvnw.cmd test
.\mvnw.cmd spring-boot:run
.\mvnw.cmd clean package
java -jar .\target\springBootApp-0.0.1-SNAPSHOT.jar
```

Use each project's Maven Wrapper (`mvnw.cmd` on Windows) so the build uses the version recorded by that project. Before a live smoke test, check whether the configured port is already in use.

## Repository Purpose and Structure

This folder is a learning journal organized as `udemy/<instructor>/<numbered-section>/<course-project>/`. Create section directories with a two-digit course-order prefix and a kebab-case topic, such as `01-spring-boot-basics`, `02-spring-core`, and `03-spring-mvc`.

Each runnable project should remain independently buildable and should have its own `notes.md` beside its build file. The instructor root contains the navigation and cumulative revision files that connect all sections.

Current structure:

```text
chadDarby/
├── README.md                       # Course map, progress, and links
├── COURSE_REVIEW.md                # Concise concepts accumulated across sections
├── AGENTS.md                       # Instructions for future agents
└── 01-spring-boot-basics/
    └── springBootApp/
        ├── pom.xml                 # Spring Boot version, Java version, and dependencies
        ├── src/main/java/          # Application and lesson code
        ├── src/main/resources/     # application.properties and other resources
        ├── src/test/java/          # JUnit/Spring Boot tests
        └── notes.md                # Detailed project-specific revision notes
```

Treat `target/` and IDE metadata such as `.idea/` as generated or local-only content. Do not document generated class files as source code.

## Required Workflow for Learning Notes

Before creating or updating a project's `notes.md`:

1. Read this `AGENTS.md`, the complete existing `notes.md`, and the end of the notes again before editing.
2. Inspect the current `pom.xml` or `build.gradle`, application configuration, all lesson source files, and relevant tests.
3. Trace real code paths. Do not infer behavior from filenames or from a course topic alone.
4. Record the Spring Boot and Java versions because defaults and property names can change between versions.
5. For version-sensitive behavior, verify against the matching official Spring documentation. Prefer primary sources.
6. Run the smallest meaningful build or test. When the lesson concerns runtime behavior, also perform focused HTTP smoke tests if practical.
7. Preserve existing learning history. Append a clearly dated section when adding a later lesson; do not replace earlier notes unless correcting an identified error.
8. Read the root `README.md` and `COURSE_REVIEW.md`, then synchronize them as described below.

## Root Documentation Synchronization

Whenever a section or project is created, renamed, moved, completed, or given new notes, check both root documentation files before finishing:

### `README.md` — navigation and progress

- Add or correct the numbered section and project link.
- Summarize the project's main topics in a few phrases.
- Record an honest status such as `Planned`, `In progress`, or `Complete`.
- Keep entries in course order and verify every relative link.

### `COURSE_REVIEW.md` — cumulative understanding

- Add concepts that are new to the course-level revision guide.
- Distill the idea into a compact mental model, rule, or comparison and link to the detailed project notes.
- Update version snapshots when a new project uses different Java, Spring Boot, database, or tool versions.
- Avoid copying the full project notes. Project `notes.md` files remain the source of detailed code, commands, observations, and troubleshooting.
- If a notes change adds no course-level concept, still check this file and leave it unchanged rather than inventing duplicate content.

For a section containing several small projects, add a section-level `README.md` when it materially improves navigation. Link it from the root `README.md`; do not create empty placeholder notes.

## Notes Structure

Write notes for active recall, not as a transcript. Include the sections that apply:

- **Lesson snapshot:** what was built, versions, dependencies, and the learning goal.
- **Mental model:** why the feature exists and how its parts connect.
- **Project map:** important files, classes, annotations, methods, and responsibilities.
- **Code path:** startup and request/data flow in execution order.
- **Configuration reference:** property, value, default, effect, and whether it is active or commented out.
- **URLs or commands:** complete runnable examples and expected results.
- **Observed verification:** command/date/result; separate observed behavior from documentation-derived behavior.
- **Alternatives and tradeoffs:** label examples that are not implemented in the current project.
- **Common mistakes:** symptoms, causes, and fixes.
- **Recall prompts:** short questions with answers or compact memory aids.
- **Official references:** direct links to the documentation version used.
- **Course links:** link back to the root `README.md` and `COURSE_REVIEW.md` so navigation works in both directions.

When explaining a setting, connect all parts explicitly. For example, show how `server.port`, the context path, a controller mapping, and an Actuator base path combine into the final URL.

## Writing Conventions

- Start with the big picture, then explain individual annotations, properties, and code.
- Use concrete snippets from the project and include expected output where it improves recall.
- Distinguish these states precisely: default framework behavior, active project configuration, commented example, command-line override, and suggested future code.
- Explain similar concepts side by side when confusion is likely, such as Actuator access versus HTTP exposure versus information contributors.
- Keep examples safe to copy. Never place credentials, tokens, personal secrets, or sensitive Actuator output in notes.
- Use tables for configuration and comparisons, and short diagrams when they clarify a multi-step flow.
- Cite local source paths with line numbers when practical, and link official documentation for framework claims.
- Preserve useful detail: exact property names, annotations, commands, URLs, response examples, failure modes, and memory aids.
- Do not copy course transcripts, assessment dumps, or large passages from documentation. Explain the material in original language.

## Java and Spring Conventions

Follow the style already present in each project:

- `PascalCase` for classes and `camelCase` for methods, fields, and local variables.
- Keep the main `@SpringBootApplication` class in a parent package so component scanning reaches project components.
- Prefer constructor injection for new application dependencies. When documenting existing field injection, explain it as the current lesson implementation.
- Group related custom settings with `@ConfigurationProperties` as projects grow; `@Value` is reasonable for demonstrating one or two values.
- Add focused tests for new behavior. A `contextLoads()` test proves context startup only; it does not prove endpoint responses.

## Boundaries

- **Always:** inspect `git status`, preserve unrelated user changes, run relevant tests, update the root navigation/review files when required, verify relative links, and report exactly what was verified.
- **Ask first:** add dependencies, restructure existing course projects, change application behavior beyond the requested lesson, or delete learning history.
- **Never:** edit generated output under `target/`, commit secrets, claim an unrun check passed, or commit/push unless the user explicitly requests it.
