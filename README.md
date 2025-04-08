# SpringDoc SDK Plugin

A Maven plugin for generating SDKs and documentation from OpenAPI specifications.

## Features

- Generate SDKs in multiple languages (Java, TypeScript, etc.)
- Generate documentation in HTML format
- Customizable templates and configurations
- Support for additional properties and attributes

## Quick Start

1. Add the plugin to your project's `pom.xml`:

```xml
<plugin>
    <groupId>com.example</groupId>
    <artifactId>springdoc-sdk-plugin-sdk-generator</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <executions>
        <execution>
            <goals>
                <goal>generate-sdk</goal>
            </goals>
            <configuration>
                <openApiSpec>${project.basedir}/src/main/resources/openapi.yaml</openApiSpec>
                <generatorName>java</generatorName>
                <outputDirectory>${project.build.directory}/generated-sources/openapi</outputDirectory>
            </configuration>
        </execution>
    </executions>
</plugin>
```

2. Run the plugin:
```bash
mvn clean install
```

## Configuration Options

### SDK Generator

- `openApiSpec`: Path to the OpenAPI specification file (required)
- `generatorName`: Name of the generator to use (required)
- `outputDirectory`: Directory where the generated SDK will be placed (default: `${project.build.directory}/generated-sources/openapi`)
- `templateDir`: Directory containing custom templates (optional)
- `additionalProperties`: Additional properties for the generator (optional)

### Documentation Generator

- `openApiSpec`: Path to the OpenAPI specification file (required)
- `docsOutputDirectory`: Directory where the generated documentation will be placed (default: `${project.build.directory}/generated-docs`)
- `templateDir`: Directory containing custom templates (optional)
- `attributes`: Attributes for the documentation generation (optional)

## Example Project

For a complete example of how to use the plugin, see the [springdoc-sdk-plugin-example](../springdoc-sdk-plugin-example) project. It demonstrates:

- Setting up a project with the plugin
- Generating both Java and TypeScript SDKs
- Using the generated SDKs in your code

## Building

To build the plugin, run:

```bash
mvn clean install
```

## Testing

To run the tests, run:

```bash
mvn test
```

## License

This project is licensed under the MIT License - see the LICENSE file for details. 