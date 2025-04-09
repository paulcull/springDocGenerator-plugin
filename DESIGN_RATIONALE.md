# Design Rationale for SpringDoc SDK Generator Plugin Suite

## Goal

The primary goal of this plugin suite is to simplify and automate the generation of client SDKs and associated documentation directly from OpenAPI specifications within a Maven build process, particularly for projects using Spring Boot.

## Modular Architecture

The plugin is divided into distinct modules to separate concerns and provide flexibility:

1.  **`springdoc-sdk-plugin-core` (`springdoc-core:generate-docs`)**: Focuses on processing an OpenAPI spec to generate *documentation-related* assets. This includes:
    *   Generating static documentation resources (HTML, CSS, JS) based on Freemarker templates (`<templateMappings>`).
    *   Generating necessary Spring Boot `@Configuration` classes (`DocumentationConfig`, `DocumentationController`, `SwaggerUIConfig`) to host these static resources and provide convenient endpoints/redirects within the consuming application (`<injectHostingEndpoints>`).
    *   Adding the generated configuration classes to the Maven compile path.

2.  **`springdoc-sdk-plugin-sdk-generator` (`springdoc-sdk:generate-sdk`)**: Focuses on generating client *SDK source code* for various languages.
    *   This module acts primarily as a wrapper around the standard [OpenAPI Generator](https://openapi-generator.tech/).
    *   It translates Maven plugin configuration (`<language>`, `<configOptions>`, etc.) into arguments for the underlying generator.
    *   It places the generated SDK source code into a specified output directory.

3.  **`springdoc-sdk-plugin-docs-generator` (`springdoc-docs:generate-docs`)**: Intended to provide alternative or specialized documentation generation capabilities (details TBD).

This modularity allows users to pick and choose the generation capabilities they need (e.g., only SDK generation, only core documentation hosting, or both).

## Key Design Decisions

### 1. Runtime Dependency Management (SDK Generator)

**Decision:** The `sdk-generator` module **does not** automatically inject runtime dependencies (like HTTP clients, JSON libraries) required by the generated SDK into the consuming project's `pom.xml`.

**Rationale:**
*   **Maven Best Practices:** Automatically modifying a project's declared dependencies from within a plugin is generally considered an anti-pattern. The project's `pom.xml` should remain the single source of truth for its direct compile and runtime requirements.
*   **Avoiding Hidden Dependencies:** Automatic injection makes it difficult to understand the project's actual dependency tree and manage versions effectively.
*   **Preventing Conflicts:** The plugin cannot reliably know which version of a dependency (e.g., Jackson, OkHttp) the consuming project requires for its *other* functionalities. Injecting a different version could lead to hard-to-diagnose classpath conflicts (e.g., `NoSuchMethodError`).
*   **Complexity & Brittleness:** Maintaining the logic within the plugin to determine the correct dependencies and versions for every possible generator language/library combination is extremely complex and prone to breaking as the underlying OpenAPI Generator evolves.

**Compromise - Dependency Management:**
*   To ease version management for the user, the plugin suite's parent POM (`springdoc-sdk-plugin/pom.xml`) includes a `<dependencyManagement>` section. This section provides recommended, compatible versions for common libraries often required by generated SDKs (e.g., `spring-boot-starter-webflux`, `jackson-databind`).
*   Users still need to explicitly add the required `<dependency>` entries to their service's `pom.xml`, but they can omit the `<version>` tag, allowing Maven to pick up the version managed by the plugin's parent POM. This strikes a balance between Maven best practices and user convenience.

### 2. Logging in Generated Code (Core Module)

**Decision:** Logging statements were removed from the Freemarker templates (`*.ftl`) used by the `core` module to generate the `DocumentationConfig.java` class.

**Rationale:**
*   **Classpath Conflicts:** Initial attempts included SLF4J logging, which caused compilation errors in consuming projects because the specific logging implementation (Logback, Log4j2, JUL via bridge) and its availability on the classpath couldn't be guaranteed by the plugin. Spring Boot setups can vary. Even switching to `java.util.logging` (JUL) caused resolution issues.
*   **Non-Essential Functionality:** The primary purpose of the generated `DocumentationConfig` is WebMvc setup (resource handlers, redirects). Logging within this specific, simple generated class was deemed non-critical.
*   **Robustness:** Removing the logging eliminates a potential source of build failures in diverse project environments, making the plugin more reliable. The plugin itself still uses Maven's standard logging (`getLog()`) to report its own progress during the build. 