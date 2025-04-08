package com.example.springdoc.sdk.plugin.core;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SpringDocSdkPluginTest {
    @TempDir
    Path tempDir;

    private SpringDocSdkPlugin plugin;
    private File openApiSpecFile;

    @BeforeEach
    void setUp() throws IOException {
        plugin = new SpringDocSdkPlugin();
        
        // Create a minimal OpenAPI spec file
        openApiSpecFile = new File(tempDir.toFile(), "openapi.yaml");
        try (FileWriter writer = new FileWriter(openApiSpecFile)) {
            writer.write("openapi: 3.0.0\n");
            writer.write("info:\n");
            writer.write("  title: Test API\n");
            writer.write("  version: 1.0.0\n");
            writer.write("paths: {}\n");
        }

        plugin.setOpenApiSpec(openApiSpecFile);
        plugin.setOutputDirectory(new File(tempDir.toFile(), "output"));
        plugin.setLanguage("java");
    }

    @Test
    void testExecute() throws MojoExecutionException, MojoFailureException {
        assertDoesNotThrow(() -> plugin.execute());
        assertTrue(plugin.getOutputDirectory().exists());
    }

    @Test
    void testExecuteWithInvalidOpenApiSpec() {
        plugin.setOpenApiSpec(new File("nonexistent.yaml"));
        assertThrows(MojoExecutionException.class, () -> plugin.execute());
    }

    @Test
    void testExecuteWithNullOpenApiSpec() {
        plugin.setOpenApiSpec(null);
        assertThrows(MojoExecutionException.class, () -> plugin.execute());
    }

    @Test
    void testExecuteWithInvalidOutputDirectory() {
        plugin.setOutputDirectory(new File("/nonexistent/directory"));
        assertThrows(MojoExecutionException.class, () -> plugin.execute());
    }

    @Test
    void testExecuteWithNullOutputDirectory() {
        plugin.setOutputDirectory(null);
        assertThrows(MojoExecutionException.class, () -> plugin.execute());
    }

    @Test
    void testExecuteWithInvalidLanguage() {
        plugin.setLanguage("invalid");
        assertThrows(MojoExecutionException.class, () -> plugin.execute());
    }

    @Test
    void testExecuteWithNullLanguage() {
        plugin.setLanguage(null);
        assertThrows(MojoExecutionException.class, () -> plugin.execute());
    }

    @Test
    void testExecuteWithGeneratorConfig() throws MojoExecutionException, MojoFailureException {
        Map<String, String> config = new HashMap<>();
        config.put("dateLibrary", "java8");
        config.put("library", "resttemplate");
        plugin.setGeneratorConfig(config);
        assertDoesNotThrow(() -> plugin.execute());
    }

    @Test
    void testExecuteWithNullGeneratorConfig() throws MojoExecutionException, MojoFailureException {
        plugin.setGeneratorConfig(null);
        assertDoesNotThrow(() -> plugin.execute());
    }
} 