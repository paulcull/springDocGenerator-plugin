# SpringDoc SDK Generator Plugin

A Maven plugin suite for generating client SDKs and associated documentation from OpenAPI specifications, tailored for Spring Boot projects.

**➡️ [Getting Started Guide](./docs/getting-started.md)**

## Overview

This project provides multiple Maven plugin modules to automate common tasks related to OpenAPI specifications:

*   **Core (`springdoc-sdk-plugin-core`)**: Generates documentation resources and Spring configuration for hosting.
*   **SDK Generator (`springdoc-sdk-plugin-sdk-generator`)**: Generates client SDK source code using OpenAPI Generator.
*   **Docs Generator (`springdoc-sdk-plugin-docs-generator`)**: Provides alternative documentation generation (Details TBD).

Choose the modules and goals that fit your needs.

## Design Philosophy

We aim for simplicity in the consuming project while adhering to Maven best practices. Key decisions include a modular approach and careful handling of dependencies.

Read more about the design choices in [DESIGN_RATIONALE.md](./DESIGN_RATIONALE.md).

## Detailed Module Usage Guides

More detailed instructions for specific modules can be found in the `docs` directory:

*   **Core Module (`springdoc-core:generate-docs`)**: 
    *   [Usage Guide](./docs/core-usage.md)
*   **SDK Generator Module (`springdoc-sdk:generate-sdk`)**: 
    *   [Usage Guide](./docs/sdk-generator-usage.md)
*   **Docs Generator Module (`springdoc-docs:generate-docs`)**: 
    *   [Usage Guide](./docs/docs-generator-usage.md) (Placeholder)

## Building the Plugin Suite

To build all plugin modules and install them into your local Maven repository:

*   Linux/macOS: 
    ```bash
    ./install-plugin.sh 
    ```
*   Windows:
    ```cmd
    install-plugin.bat
    ```
*   Or manually:
    ```bash
    mvn clean install -DskipTests
    ```

## License

This project is licensed under the MIT License - see the [LICENSE](./LICENSE) file for details.