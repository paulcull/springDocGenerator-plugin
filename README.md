# SpringDoc SDK Plugin

A Maven plugin for generating SDKs and documentation from OpenAPI specifications.

## Features

- Generate SDKs in multiple languages (Java, TypeScript, etc.)
- Generate documentation in HTML format
- Customizable templates and configurations
- Support for additional properties and attributes
- Flexible documentation hosting configuration
- Customizable Swagger UI integration
- Automatic generation of documentation controller and configuration

## Quick Start

1. Add the plugin to your project's `pom.xml`:

```xml
<plugin>
    <groupId>com.example.springdoc</groupId>
    <artifactId>springdoc-sdk-plugin-core</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <configuration>
        <openApiSpec>${project.basedir}/src/main/resources/openapi.yaml</openApiSpec>
        <basePackage>${project.groupId}</basePackage>
        <templateDirectory>templates</templateDirectory>
        <resourcePaths>
            <resourcePath>static</resourcePath>
        </resourcePaths>
        <configClassName>DocumentationConfig</configClassName>
        <resourceHandlerPath>/docs/**</resourceHandlerPath>
        <resourceLocation>classpath:/static/docs/</resourceLocation>
        <redirectFrom>/docs</redirectFrom>
        <redirectTo>/docs/index.html</redirectTo>
        <swaggerUIConfig>
            <enabled>true</enabled>
            <path>/swagger-ui.html</path>
            <version>5.11.0</version>
            <config>{"deepLinking": true, "displayOperationId": true}</config>
        </swaggerUIConfig>
    </configuration>
</plugin>
```

2. Run the plugin:
```bash
mvn clean install
```

## Generated Classes

The plugin automatically generates the following classes in your project:

### DocumentationConfig
A Spring configuration class that sets up resource handling for documentation files.

### DocumentationController
A Spring controller that handles redirects for documentation endpoints. By default, it redirects:
- `/docs` to `/docs/index.html`
- `/docs/` to `/docs/index.html`

### SwaggerUIConfig (optional)
A configuration class for Swagger UI integration, generated when `swaggerUIConfig.enabled` is `true`.

## Configuration Options

### Required Parameters

- `openApiSpec`: Path to the OpenAPI specification file (required)
  - Can be a local file path or URL
  - Supports both YAML and JSON formats

### Optional Parameters

#### Basic Configuration
- `basePackage`: Base package for generated classes (default: `${project.groupId}`)
- `templateDirectory`: Directory containing custom templates (default: `templates`)
- `resourcePaths`: Array of resource paths to include (default: `["static"]`)
- `configClassName`: Name of the generated configuration class (default: `DocumentationConfig`)
- `injectHostingEndpoints`: Whether to generate documentation hosting configuration (default: `true`)

#### Documentation Hosting
- `resourceHandlerPath`: Path pattern for documentation resources (default: `/docs/**`)
- `resourceLocation`: Classpath location of documentation resources (default: `classpath:/static/docs/`)
- `redirectFrom`: Path to redirect from (default: `/docs`)
- `redirectTo`: Path to redirect to (default: `/docs/index.html`)

#### Swagger UI Configuration
- `swaggerUIConfig`: Configuration for Swagger UI integration
  - `enabled`: Enable/disable Swagger UI (default: `true`)
  - `path`: Path to Swagger UI (default: `/swagger-ui.html`)
  - `version`: Swagger UI version (default: `5.11.0`)
  - `config`: JSON configuration for Swagger UI (default: `{"deepLinking": true, "displayOperationId": true}`)

#### Template Customization
- `templateMappings`: Map of template names to output files
  - Example:
    ```xml
    <templateMappings>
        <index.html>index.html</index.html>
        <api-reference.html>api-reference.html</api-reference.html>
    </templateMappings>
    ```
- `templateVariables`: Custom variables to pass to templates
  - Example:
    ```xml
    <templateVariables>
        <customVar1>value1</customVar1>
        <customVar2>value2</customVar2>
    </templateVariables>
    ```

## Advanced Usage

### Custom Templates
1. Create a `templates` directory in your project
2. Add your custom templates (`.ftl` files)
3. Configure template mappings in your `pom.xml`

### Custom Resource Paths
```xml
<resourcePaths>
    <resourcePath>static</resourcePath>
    <resourcePath>docs</resourcePath>
    <resourcePath>assets</resourcePath>
</resourcePaths>
```

### Custom Swagger UI Configuration
```xml
<swaggerUIConfig>
    <enabled>true</enabled>
    <path>/api-docs</path>
    <version>5.11.0</version>
    <config>{
        "deepLinking": true,
        "displayOperationId": true,
        "defaultModelsExpandDepth": 1,
        "defaultModelExpandDepth": 1,
        "defaultModelRendering": "example",
        "displayRequestDuration": true,
        "docExpansion": "list",
        "filter": true,
        "maxDisplayedTags": null,
        "showExtensions": true,
        "showCommonExtensions": true,
        "tagsSorter": "alpha",
        "operationsSorter": "alpha",
        "showOperationIds": false,
        "syntaxHighlight": {
            "activate": true,
            "theme": "monokai"
        }
    }</config>
</swaggerUIConfig>
```

## Example Project

For a complete example of how to use the plugin, see the [springdoc-sdk-plugin-example](../springdoc-sdk-plugin-example) project. It demonstrates:

- Setting up a project with the plugin
- Generating documentation with custom templates
- Configuring documentation hosting
- Customizing Swagger UI integration
- Using the generated controller and configuration classes

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