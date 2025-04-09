# Core Module Usage (`springdoc-core:generate-docs`)

This goal generates static documentation resources and Spring Boot configuration classes to host them.

## Functionality

1.  **Reads OpenAPI Spec**: Parses the specified OpenAPI specification file (YAML or JSON).
2.  **Generates Static Resources (Optional)**: If `<templateMappings>` are provided, it processes Freemarker templates (`.ftl`) found in the `<templateDirectory>` using data from the OpenAPI spec and `<templateVariables>`. The output files (e.g., `index.html`, `styles.css`) are placed in the `<outputDirectory>`.
3.  **Generates Hosting Configuration (Optional)**: If `<injectHostingEndpoints>` is `true` (the default), it generates several Java classes within `target/generated-sources/annotations` under the specified `<basePackage>`:
    *   **`${configClassName}.java`**: A Spring `@Configuration` class implementing `WebMvcConfigurer`. It configures resource handlers to serve static files from `<resourceLocation>` (which should correspond to where your generated/static documentation assets are packaged, often `classpath:/static/docs/`) via the `<resourceHandlerPath>` (e.g., `/docs/**`). It also sets up a redirect from `<redirectFrom>` to `<redirectTo>`.
    *   **`controller/DocumentationController.java`**: A simple Spring `@Controller` that primarily assists with the redirect logic defined in the `${configClassName}`.
    *   **`SwaggerUIConfig.java`**: A simple configuration properties holder class generated if `<injectSwaggerUI>` is true and `<swaggerUIConfig>` is provided and enabled. This allows the templates to potentially access Swagger UI settings.
4.  **Adds Source Root**: Automatically adds the `target/generated-sources/annotations` directory to the Maven project's compile source roots, so the generated Java classes are compiled with the rest of your project.

## Configuration

Add the plugin to your `pom.xml`'s `<build><plugins>` section:

```xml
<plugin>
    <groupId>com.example</groupId> <!-- Plugin groupId -->
    <artifactId>springdoc-sdk-plugin-core</artifactId> <!-- Core module artifactId -->
    <version>1.0.0-SNAPSHOT</version> <!-- Use the appropriate plugin version -->
    <executions>
        <execution>
            <id>generate-documentation</id>
            <phase>process-sources</phase> <!-- Bind to a phase before compile -->
            <goals>
                <goal>generate-docs</goal> 
            </goals>
            <configuration>
                <!-- === Required === -->
                
                <!-- Path or URL to your OpenAPI specification file -->
                <openApiSpec>${project.basedir}/path/to/your/openapi.yaml</openApiSpec>

                <!-- === Commonly Used === -->
                
                <!-- Base package for the generated Java configuration classes -->
                <!-- If not set, defaults to ${project.groupId} -->
                <basePackage>com.yourcompany.config.docs</basePackage> 
                
                <!-- Controls whether Spring config classes are generated -->
                <!-- Defaults to true. Set to false if you only want to generate static resources -->
                <!-- using <templateMappings> and handle hosting yourself. -->
                <injectHostingEndpoints>true</injectHostingEndpoints>
                
                <!-- Nested configuration object for Swagger UI properties -->
                <!-- These properties are primarily made available to templates -->
                <!-- and used to generate SwaggerUIConfig.java if injectSwaggerUI=true -->
                <swaggerUIConfig>
                    <enabled>true</enabled> <!-- Default: true -->
                    <path>/swagger-ui.html</path> <!-- Default: /swagger-ui.html -->
                    <version>5.11.0</version> <!-- Default: 5.11.0 -->
                    <!-- See config/SwaggerUIConfig.java in plugin source for all fields -->
                    <!-- Example: <title>My API Docs</title> -->
                </swaggerUIConfig>
                
                <!-- Maps Freemarker template files (in templateDirectory) to output files -->
                <!-- Use this to generate static HTML, CSS, JS etc. -->
                <!-- Output files are placed relative to outputDirectory -->
                <!-- <templateMappings>
                    <entry>
                        <key>index.ftl</key> 
                        <value>index.html</value>
                    </entry>
                    <entry>
                        <key>assets/main.css.ftl</key> 
                        <value>styles/main.css</value>
                    </entry>
                </templateMappings> -->

                <!-- === Less Common / Fine-tuning === -->
                
                <!-- Directory containing Freemarker templates (*.ftl) -->
                <!-- Defaults to 'templates' relative to project base directory -->
                <!-- <templateDirectory>src/main/doc-templates</templateDirectory> -->
                
                <!-- Base output directory for resources generated via <templateMappings> -->
                <!-- Defaults to ${project.build.directory}/classes/generated-docs -->
                <!-- NOTE: This is NOT where generated Java code goes -->
                <!-- <outputDirectory>${project.build.directory}/my-static-docs</outputDirectory> -->
                
                <!-- Name for the generated Spring @Configuration class -->
                <!-- Defaults to 'DocumentationConfig' -->
                <!-- <configClassName>MyDocsMvcConfig</configClassName> -->
                
                <!-- Path pattern for serving generated/static resources via Spring -->
                <!-- Used in generated DocumentationConfig if injectHostingEndpoints=true -->
                <!-- Defaults to /docs/** -->
                <!-- <resourceHandlerPath>/mydocs/**</resourceHandlerPath> -->
                
                <!-- Location of resources to be served (Spring format) -->
                <!-- Used in generated DocumentationConfig if injectHostingEndpoints=true -->
                <!-- Should point to where resources from <outputDirectory> end up -->
                <!-- Defaults to classpath:/static/docs/ -->
                <!-- Example if outputDirectory is default: <resourceLocation>classpath:/generated-docs/</resourceLocation> -->
                <!-- <resourceLocation>classpath:/static/mydocs/</resourceLocation> -->
                
                <!-- Path to redirect users from when accessing the documentation root -->
                <!-- Used in generated DocumentationConfig if injectHostingEndpoints=true -->
                <!-- Defaults to /docs -->
                <!-- <redirectFrom>/mydocs</redirectFrom> -->
                
                <!-- Target file for the root redirect -->
                <!-- Used in generated DocumentationConfig if injectHostingEndpoints=true -->
                <!-- Defaults to /docs/index.html -->
                <!-- <redirectTo>/mydocs/home.html</redirectTo> -->
                
                <!-- Controls generation of SwaggerUIConfig.java class -->
                <!-- Defaults to true -->
                <!-- <injectSwaggerUI>true</injectSwaggerUI> -->
                
                <!-- Additional key-value pairs to pass into Freemarker templates -->
                <!-- <templateVariables>
                    <copyrightYear>2024</copyrightYear>
                    <buildTimestamp>${maven.build.timestamp}</buildTimestamp>
                </templateVariables> -->
                
                <!-- Legacy parameter, potentially for copying static resources -->
                <!-- Default: ["static"] -->
                <!-- <resourcePaths>
                    <resourcePath>static-assets</resourcePath>
                </resourcePaths> -->

            </configuration>
        </execution>
    </executions>
</plugin>
```

## Use Cases

*   **Simple Hosting Setup**: Use defaults or configure `basePackage`, `resourceHandlerPath`, `resourceLocation`, `redirectFrom`, `redirectTo`. Manually place your static documentation files (e.g., exported from another tool, or simple HTML) into the location specified by `resourceLocation` (e.g., `src/main/resources/static/docs`). The plugin generates the Spring configuration to serve them.
*   **Template-Based Generation**: Define Freemarker templates in `<templateDirectory>`. Use `<templateMappings>` to specify which templates produce which output files in `<outputDirectory>`. Configure `<resourceLocation>` to point to where these generated files are packaged (e.g., `classpath:/generated-docs/` if using the default `outputDirectory`). Use `<templateVariables>` and `<swaggerUIConfig>` to pass data to your templates.
*   **Configuration Only**: Set `<injectHostingEndpoints>false`. Use `<templateMappings>` to generate resources. Handle serving these resources entirely yourself within your application code. 