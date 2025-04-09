# Getting Started Guide

This guide walks you through setting up the SpringDoc SDK Generator plugin suite in your Spring Boot Maven project to generate both a documentation page and downloadable client SDKs.

## Prerequisites

1.  **Plugin Installed:** Ensure the plugin suite (`springdoc-sdk-plugin`) is built and installed in your local Maven repository. You can do this by cloning the plugin repository and running `./install-plugin.sh` or `mvn clean install -DskipTests` in its root directory.
2.  **OpenAPI Specification:** Have an OpenAPI 3.x specification file (e.g., `openapi.yaml` or `openapi.json`) available within your service project (e.g., in `src/main/resources`).

## Step 1: Add Plugin Dependencies and Properties (pom.xml)

In your service project's `pom.xml`, add the necessary runtime dependencies for the generated *Java* SDK (assuming OkHttp/Gson generation, which seems to be the default) and the plugin version property.

```xml
<properties>
    <java.version>17</java.version>
    <springdoc.version>2.3.0</springdoc.version> <!-- Or your springdoc version -->
    <!-- Add the plugin version property -->
    <springdoc-sdk-plugin.version>1.0.0-SNAPSHOT</springdoc-sdk-plugin.version> 
    <lombok.version>1.18.30</lombok.version> <!-- Ensure lombok version is defined -->
</properties>

<dependencies>
    <!-- Your existing project dependencies (Spring Boot, Data, Web, etc.) -->
    
    <!-- Lombok (Ensure optional=false if used in main code) -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <!-- <optional>true</optional> --> 
    </dependency>

    <!-- Standard SpringDoc UI (Optional but recommended) -->
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>${springdoc.version}</version>
    </dependency>

    <!-- === Dependencies for Generated Java SDK (using OkHttp/Gson) === -->
    <dependency>
        <groupId>com.squareup.okhttp3</groupId>
        <artifactId>okhttp</artifactId>
        <!-- Version managed by Spring Boot parent or add explicit version -->
    </dependency>
    <dependency>
        <groupId>com.squareup.okhttp3</groupId>
        <artifactId>logging-interceptor</artifactId>
         <!-- Version managed by Spring Boot parent or add explicit version -->
    </dependency>
    <dependency>
        <groupId>com.google.code.gson</groupId>
        <artifactId>gson</artifactId>
         <!-- Version managed by Spring Boot parent or add explicit version -->
    </dependency>
    <dependency>
        <groupId>io.gsonfire</groupId>
        <artifactId>gson-fire</artifactId>
        <version>1.9.0</version> <!-- Check latest -->
    </dependency>
    <dependency> 
        <groupId>jakarta.annotation</groupId>
        <artifactId>jakarta.annotation-api</artifactId>
        <!-- Version managed by Spring Boot parent -->
    </dependency>
    <!-- Add javax.annotation for compatibility if generator outputs it -->
    <dependency>
        <groupId>javax.annotation</groupId>
        <artifactId>javax.annotation-api</artifactId>
        <version>1.3.2</version> 
    </dependency>
     <!-- Add jsr305 for @Nullable/@Nonnull if needed -->
    <dependency>
        <groupId>com.google.code.findbugs</groupId>
        <artifactId>jsr305</artifactId>
        <version>3.0.2</version>
    </dependency>

</dependencies>
```

## Step 2: Configure Maven Build Plugins (pom.xml)

Add the following plugins to your `<build><plugins>` section. Ensure you have the `maven-compiler-plugin` configured for Lombok if you use it.

```xml
<!-- === Compiler Plugin (Ensure Lombok Annotation Processing) === -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.11.0</version>
    <configuration>
        <annotationProcessorPaths>
            <path>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
                <version>${lombok.version}</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>

<!-- === Spring Boot Plugin (Standard) === -->
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <configuration>
        <excludes>
            <exclude>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
            </exclude>
        </excludes>
    </configuration>
</plugin>

<!-- === Core Plugin: Generate Docs Page & Config === -->
<plugin>
    <groupId>com.example</groupId>
    <artifactId>springdoc-sdk-plugin-core</artifactId>
    <version>${springdoc-sdk-plugin.version}</version>
    <executions>
        <execution>
            <id>generate-documentation</id>
            <phase>process-sources</phase> <!-- Runs before compile -->
            <goals>
                <goal>generate-docs</goal>
            </goals>
            <configuration>
                <openApiSpec>${project.basedir}/src/main/resources/petstore.yaml</openApiSpec>
                <basePackage>com.example.petstore.config</basePackage> <!-- Package for generated config -->
                <outputDirectory>${project.build.directory}/classes/generated-docs</outputDirectory>
                <templateMappings>
                    <!-- Map template from plugin JAR to output file -->
                    <index.ftl>index.html</index.ftl>
                </templateMappings>
                <!-- Location where generated files are placed for serving -->
                <resourceLocation>classpath:/generated-docs/</resourceLocation>
                <swaggerUIConfig>
                    <enabled>true</enabled> <!-- Enable generation of SwaggerUIConfig bean -->
                </swaggerUIConfig>
            </configuration>
        </execution>
    </executions>
</plugin>

<!-- === Dependency Plugin: Unpack Static Assets (e.g., Bootstrap) === -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-dependency-plugin</artifactId>
    <version>3.6.1</version> 
    <executions>
        <execution>
            <id>unpack-doc-assets</id>
            <phase>process-resources</phase> <!-- Runs after generate-docs -->
            <goals>
                <goal>unpack</goal>
            </goals>
            <configuration>
                <artifactItems>
                    <artifactItem>
                        <groupId>com.example</groupId>
                        <artifactId>springdoc-sdk-plugin-core</artifactId>
                        <version>${springdoc-sdk-plugin.version}</version>
                        <type>jar</type>
                        <overWrite>true</overWrite>
                        <!-- Unpack into generated-docs, keeping subdirs -->
                        <outputDirectory>${project.build.outputDirectory}/generated-docs</outputDirectory>
                        <!-- Path INSIDE the plugin JAR to unpack -->
                        <includes>static-docs-assets/**</includes>
                    </artifactItem>
                </artifactItems>
            </configuration>
        </execution>
    </executions>
</plugin>

<!-- === SDK Generator Plugin: Generate SDK Sources === -->
<plugin>
    <groupId>com.example</groupId>
    <artifactId>springdoc-sdk-plugin-sdk-generator</artifactId>
    <version>${springdoc-sdk-plugin.version}</version>
    <executions>
        <!-- Java SDK -->
        <execution>
            <id>generate-java-sdk</id>
            <phase>generate-sources</phase>
            <goals>
                <goal>generate-sdk</goal>
            </goals>
            <configuration>
                <openApiSpec>${project.basedir}/src/main/resources/petstore.yaml</openApiSpec>
                <!-- Output SDK source outside target -->
                <outputDirectory>${project.basedir}/generated-sdk/java</outputDirectory>
                <language>java</language>
                <generatorName>java</generatorName>
                <generatorConfig>
                    <apiPackage>com.example.petstore.sdk.java.api</apiPackage>
                    <modelPackage>com.example.petstore.sdk.java.model</modelPackage>
                    <library>webclient</library> 
                    <dateLibrary>java8</dateLibrary>
                    <useSpringBoot3>true</useSpringBoot3> 
                    <sourceFolder>src/main/java</sourceFolder>
                    <useJakartaEe>true</useJakartaEe> 
                </generatorConfig>
            </configuration>
        </execution>
        <!-- TypeScript SDK -->
        <execution>
            <id>generate-ts-sdk</id>
            <phase>generate-sources</phase>
            <goals>
                <goal>generate-sdk</goal>
            </goals>
            <configuration>
                <openApiSpec>${project.basedir}/src/main/resources/petstore.yaml</openApiSpec>
                <outputDirectory>${project.basedir}/generated-sdk/ts</outputDirectory>
                <language>typescript-axios</language>
                <generatorName>typescript-axios</generatorName>
                <generatorConfig>
                    <supportsES6>true</supportsES6>
                </generatorConfig>
            </configuration>
        </execution>
        <!-- Python SDK -->
        <execution>
            <id>generate-python-sdk</id>
            <phase>generate-sources</phase>
            <goals>
                <goal>generate-sdk</goal>
            </goals>
            <configuration>
                <openApiSpec>${project.basedir}/src/main/resources/petstore.yaml</openApiSpec>
                <outputDirectory>${project.basedir}/generated-sdk/python</outputDirectory>
                <language>python</language>
                <generatorName>python</generatorName>
                <generatorConfig>
                    <packageName>petstore_sdk</packageName>
                </generatorConfig>
            </configuration>
        </execution>
    </executions>
</plugin>

<!-- === Build Helper Plugin: Add Generated Java Sources === -->
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>build-helper-maven-plugin</artifactId>
    <version>3.5.0</version> 
    <executions>
        <execution>
            <id>add-sdk-sources</id>
            <phase>generate-sources</phase> <!-- Runs after SDK generation -->
            <goals>
                <goal>add-source</goal>
            </goals>
            <configuration>
                <sources>
                    <!-- Add generated Java SDK source -->
                    <source>${project.basedir}/generated-sdk/java/src/main/java</source>
                </sources>
            </configuration>
        </execution>
    </executions>
</plugin>

<!-- === Assembly Plugin: Package SDKs (Run via Profile) === -->
<!-- NOTE: This is placed in a profile to run AFTER sources are generated -->
<!-- See Step 4 below -->

<!-- === Resources Plugin: Copy SDK ZIPs (Run via Profile) === -->
<!-- NOTE: This is placed in a profile to run AFTER assembly -->
<!-- See Step 4 below -->
```

## Step 3: Create Assembly Descriptor (src/assembly/sdk-assembly-fileset.xml)

Create the following file in your service project at `src/assembly/sdk-assembly-fileset.xml`. This tells the `maven-assembly-plugin` how to package the SDK source code into a ZIP file.

```xml
<assembly xmlns="http://maven.apache.org/ASSEMBLY/2.1.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/ASSEMBLY/2.1.0 http://maven.apache.org/xsd/assembly-2.1.0.xsd">
    <id>sdk-source-fileset</id>
    <formats>
        <format>zip</format>
    </formats>
    <includeBaseDirectory>false</includeBaseDirectory>
    <!-- Define the fileSet using the property passed from POM -->
    <fileSets>
        <fileSet>
            <directory>${sdk.source.dir}</directory>
            <outputDirectory>/</outputDirectory>
            <includes>
                <include>**/*</include>
            </includes>
        </fileSet>
    </fileSets>
</assembly>
```

## Step 4: Create Packaging Profile (pom.xml)

Add the following `<profiles>` section at the end of your `pom.xml` (just before `</project>`). This moves the SDK packaging (`assembly`) and copying (`resources`) steps into a profile so they run correctly *after* the SDK source code exists.

```xml
<profiles>
    <profile>
        <id>package-sdk</id>
        <build>
            <plugins>
                <!-- Plugin to create ZIP archives of generated SDKs -->
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-assembly-plugin</artifactId>
                    <version>3.6.0</version> 
                    <executions>
                        <execution>
                            <id>zip-java-sdk</id>
                            <phase>package</phase> 
                            <goals>
                                <goal>single</goal>
                            </goals>
                            <configuration>
                                <appendAssemblyId>false</appendAssemblyId>
                                <finalName>java-sdk</finalName>
                                <outputDirectory>${project.build.directory}/sdk-packages</outputDirectory>
                                <descriptors>
                                    <descriptor>src/assembly/sdk-assembly-fileset.xml</descriptor>
                                </descriptors>
                                <properties>
                                    <sdk.source.dir>${project.basedir}/generated-sdk/java</sdk.source.dir>
                                </properties>
                            </configuration>
                        </execution>
                         <execution>
                            <id>zip-ts-sdk</id>
                            <phase>package</phase>
                            <goals>
                                <goal>single</goal>
                            </goals>
                            <configuration>
                                <appendAssemblyId>false</appendAssemblyId>
                                <finalName>ts-sdk</finalName>
                                <outputDirectory>${project.build.directory}/sdk-packages</outputDirectory>
                                <descriptors>
                                    <descriptor>src/assembly/sdk-assembly-fileset.xml</descriptor> 
                                </descriptors>
                                <properties>
                                    <sdk.source.dir>${project.basedir}/generated-sdk/ts</sdk.source.dir>
                                </properties>
                            </configuration>
                        </execution>
                         <execution>
                            <id>zip-python-sdk</id>
                            <phase>package</phase>
                            <goals>
                                <goal>single</goal>
                            </goals>
                            <configuration>
                                <appendAssemblyId>false</appendAssemblyId>
                                <finalName>python-sdk</finalName>
                                <outputDirectory>${project.build.directory}/sdk-packages</outputDirectory>
                                <descriptors>
                                    <descriptor>src/assembly/sdk-assembly-fileset.xml</descriptor> 
                                </descriptors>
                                <properties>
                                    <sdk.source.dir>${project.basedir}/generated-sdk/python</sdk.source.dir>
                                </properties>
                            </configuration>
                        </execution>
                    </executions>
                </plugin>

                <!-- Plugin to copy the packaged SDK ZIPs to the docs directory -->
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-resources-plugin</artifactId>
                    <version>3.3.1</version>
                    <executions>
                        <execution>
                            <id>copy-sdk-zips</id>
                            <phase>package</phase>
                            <goals>
                                <goal>copy-resources</goal>
                            </goals>
                            <configuration>
                                <outputDirectory>${project.build.directory}/generated-docs/sdk</outputDirectory>
                                <resources>
                                    <resource>
                                        <directory>${project.build.directory}/sdk-packages</directory>
                                        <includes>
                                            <include>*.zip</include>
                                        </includes>
                                    </resource>
                                </resources>
                            </configuration>
                        </execution>
                    </executions>
                </plugin>
            </plugins>
        </build>
    </profile>
</profiles>
```

## Step 5: Build and Run

Because packaging the SDKs (`maven-assembly-plugin`) needs to run *after* the SDK source code has been generated and is fully available on the filesystem, a standard single `mvn clean install` can be unreliable.

**Recommended Method:**

Use the provided helper script (or run the commands manually):

1.  **(Optional) Make script executable (Linux/macOS):** `chmod +x generate-docs-and-sdk.sh`
2.  **Run the generation script:**
    *   Linux/macOS: 
        ```bash
        ./generate-docs-and-sdk.sh 
        ```
    *   Windows:
        ```cmd
        generate-docs-and-sdk.bat
        ```
    This script runs `mvn clean compile` first, followed by `mvn package -Ppackage-sdk -DskipTests`.

3.  **Run the Application:**
    ```bash
    mvn spring-boot:run
    ```

**Manual Alternative:**

If you prefer not to use the script, run the Maven commands separately:

1.  Generate sources & compile: `mvn clean compile`
2.  Package SDKs: `mvn package -Ppackage-sdk -DskipTests`
3.  Run application: `mvn spring-boot:run`

## Step 6: Access Documentation

Once the application is running, navigate to `http://localhost:8080/docs` (or the path configured via `<resourceHandlerPath>`) in your browser. You should see the generated documentation page, including download links for the SDKs created in Step 5.

See the specific module usage guides for more detailed configuration options:
*   [Core Module Usage](./core-usage.md)
*   [SDK Generator Usage](./sdk-generator-usage.md)
*   [Docs Generator Usage](./docs-generator-usage.md) (Placeholder) 