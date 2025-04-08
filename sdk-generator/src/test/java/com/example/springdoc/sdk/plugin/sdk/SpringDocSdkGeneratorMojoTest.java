package com.example.springdoc.sdk.plugin.sdk;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class SpringDocSdkGeneratorMojoTest {
    @TempDir
    Path tempDir;

    private SpringDocSdkGeneratorMojo mojo;
    private File openApiSpecFile;

    @BeforeEach
    void setUp() throws IOException {
        mojo = new SpringDocSdkGeneratorMojo();
        
        // Create a valid OpenAPI spec file
        openApiSpecFile = new File(tempDir.toFile(), "openapi.yaml");
        try (FileWriter writer = new FileWriter(openApiSpecFile)) {
            writer.write("openapi: 3.0.0\n");
            writer.write("info:\n");
            writer.write("  title: Test API\n");
            writer.write("  version: 1.0.0\n");
            writer.write("paths:\n");
            writer.write("  /test:\n");
            writer.write("    get:\n");
            writer.write("      summary: Test endpoint\n");
            writer.write("      responses:\n");
            writer.write("        '200':\n");
            writer.write("          description: OK\n");
            writer.write("          content:\n");
            writer.write("            application/json:\n");
            writer.write("              schema:\n");
            writer.write("                type: object\n");
            writer.write("                properties:\n");
            writer.write("                  message:\n");
            writer.write("                    type: string\n");
        }

        mojo.setOpenApiSpec(openApiSpecFile);
        mojo.setOutputDirectory(new File(tempDir.toFile(), "output"));
        mojo.setLanguage("java");
        mojo.setGeneratorName("java");
        mojo.setAdditionalProperties(Arrays.asList("apiPackage=com.example.api", "modelPackage=com.example.model"));
    }

    @Test
    void testExecute() throws MojoExecutionException, MojoFailureException {
        assertDoesNotThrow(() -> mojo.execute());
    }

    @Test
    void testExecuteWithInvalidGeneratorName() {
        mojo.setGeneratorName("nonexistent");
        assertThrows(MojoExecutionException.class, () -> mojo.execute());
    }

    @Test
    void testExecuteWithInvalidTemplateDir() {
        mojo.setTemplateDir(new File("nonexistent"));
        assertThrows(MojoExecutionException.class, () -> mojo.execute());
    }
}