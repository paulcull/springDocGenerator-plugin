# SDK Generator Module Usage (`springdoc-sdk:generate-sdk`)

This module (`springdoc-sdk-plugin-sdk-generator`) provides the `springdoc-sdk:generate-sdk` goal, which leverages the underlying [OpenAPI Generator](https://openapi-generator.tech/) to create client SDKs from your OpenAPI specification.

## Functionality

1.  **Reads OpenAPI Spec**: Parses the specified OpenAPI specification file (YAML or JSON).
2.  **Invokes OpenAPI Generator**: Uses the specified `language` and `configOptions` to invoke the appropriate OpenAPI Generator engine.
3.  **Generates SDK Source Code**: Places the generated client SDK source files into the `<outputDirectory>`.
4.  **Adds Source Root (Implicitly)**: Standard Maven practice assumes directories like `target/generated-sources/*` are potential source roots. Depending on your Maven version and configuration, the generated SDK code in `<outputDirectory>` (if it follows conventions like `target/generated-sources/sdk`) should be automatically picked up for compilation. If not, you might need the `build-helper-maven-plugin` to explicitly add the source root.

## Configuration

Add the plugin to your `pom.xml`'s `<build><plugins>` section:

```xml
<plugin>
    <groupId>com.example</groupId> <!-- Plugin groupId -->
    <artifactId>springdoc-sdk-plugin-sdk-generator</artifactId> <!-- SDK generator module -->
    <version>1.0.0-SNAPSHOT</version> <!-- Use the appropriate plugin version -->
    <executions>
        <execution>
            <id>generate-client-sdk</id>
            <phase>generate-sources</phase> 
            <goals>
                <goal>generate-sdk</goal> 
            </goals>
            <configuration>
                <!-- === Required === -->
                
                <!-- Path or URL to your OpenAPI specification file -->
                <openApiSpec>${project.basedir}/path/to/your/openapi.yaml</openApiSpec>
                
                <!-- Directory where generated SDK source code will be placed -->
                <outputDirectory>${project.build.directory}/generated-sources/sdk</outputDirectory>
                
                <!-- Target language for the SDK -->
                <!-- See OpenAPI Generator docs for full list (e.g., java, typescript-axios, python) -->
                <language>java</language>

                <!-- === Generator Configuration (Usually Required) === -->
                
                <!-- Map of options specific to the chosen language generator -->
                <!-- Consult OpenAPI Generator documentation for your language -->
                <configOptions>
                    <!-- Example for Java -->
                    <apiPackage>com.yourcompany.sdk.api</apiPackage>
                    <modelPackage>com.yourcompany.sdk.model</modelPackage>
                    <library>webclient</library> <!-- Or resttemplate, native, etc. -->
                    <dateLibrary>java8</dateLibrary> <!-- Or string, OffsetDateTime, etc. -->
                    <sourceFolder>src/main/java</sourceFolder> <!-- Standard for Java source layout -->
                    <!-- <useSpringBoot3>true</useSpringBoot3> --> <!-- Example: For Spring Boot 3+ compatibility -->
                    <!-- Add other relevant options for your language/library -->
                </configOptions>

            </configuration>
        </execution>
    </executions>
</plugin>
```

## Adding Runtime Dependencies

**Important:** This plugin **generates** the SDK source code but **does not** automatically add the runtime libraries needed *by* that generated code to your project. You must add these dependencies manually to your main `<dependencies>` section.

1.  **Identify Requirements:** Determine the runtime dependencies needed based on the SDK `language` and `library` you selected. The best source for this information is the [OpenAPI Generator Documentation](https://openapi-generator.tech/docs/generators) for your specific generator.
2.  **Add Dependencies to POM:** Add the required `<dependency>` entries to your `pom.xml`.

**Example for Java (`library = webclient`):**

```xml
<dependencies>
    <!-- Add Spring WebFlux (provides WebClient) -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-webflux</artifactId>
        <!-- Version managed by springdoc-sdk-plugin parent POM -->
    </dependency>

    <!-- Jackson Databind (usually needed for JSON serialization) -->
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
        <!-- Version managed by springdoc-sdk-plugin parent POM -->
    </dependency>
    
    <!-- Add other dependencies potentially needed by generated code -->
    <!-- e.g., jakarta validation api, jackson date/time modules -->

</dependencies>
```

**Dependency Version Management:**

The parent POM of the `springdoc-sdk-plugin` suite includes `<dependencyManagement>` for common libraries like `spring-boot-starter-webflux` and `jackson-databind`. This means you can often **omit the `<version>` tag** when adding these specific dependencies to your project, and Maven will use the version defined by the plugin's parent POM. See `DESIGN_RATIONALE.md` for more details.

For dependencies *not* managed by the plugin's parent POM, you will need to specify the version yourself or rely on your own project's dependency management (e.g., a Spring Boot parent POM). 