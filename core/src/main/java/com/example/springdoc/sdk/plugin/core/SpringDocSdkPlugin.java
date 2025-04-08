package com.example.springdoc.sdk.plugin.core;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.Map;

@Mojo(name = "generate")
public class SpringDocSdkPlugin extends AbstractMojo {
    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    protected MavenProject project;

    @Parameter(property = "openApiSpec", required = true)
    protected File openApiSpec;

    @Parameter(property = "outputDirectory", defaultValue = "${project.build.directory}/generated-sources/openapi")
    protected File outputDirectory;

    @Parameter(property = "language", required = true)
    protected String language;

    @Parameter(property = "configOptions")
    protected String configOptions;

    @Parameter(property = "generatorConfig")
    protected Map<String, String> generatorConfig;

    public File getOpenApiSpec() {
        return openApiSpec;
    }

    public void setOpenApiSpec(File openApiSpec) {
        this.openApiSpec = openApiSpec;
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getConfigOptions() {
        return configOptions;
    }

    public void setConfigOptions(String configOptions) {
        this.configOptions = configOptions;
    }

    public Map<String, String> getGeneratorConfig() {
        return generatorConfig;
    }

    public void setGeneratorConfig(Map<String, String> generatorConfig) {
        this.generatorConfig = generatorConfig;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        // Validate required parameters
        if (openApiSpec == null) {
            throw new MojoExecutionException("OpenAPI specification file is required");
        }

        if (language == null) {
            throw new MojoExecutionException("Language is required");
        }

        if (outputDirectory == null) {
            throw new MojoExecutionException("Output directory is required");
        }

        // Validate OpenAPI spec file
        if (!openApiSpec.exists()) {
            throw new MojoExecutionException("OpenAPI specification file does not exist: " + openApiSpec.getAbsolutePath());
        }

        // Validate output directory
        if (!outputDirectory.exists() && !outputDirectory.mkdirs()) {
            throw new MojoExecutionException("Failed to create output directory: " + outputDirectory.getAbsolutePath());
        }

        // Validate language
        if (!isValidLanguage(language)) {
            throw new MojoExecutionException("Invalid language: " + language);
        }

        getLog().info("Generating SDK and documentation from OpenAPI specification: " + openApiSpec.getAbsolutePath());
        getLog().info("Output directory: " + outputDirectory.getAbsolutePath());
        getLog().info("Language: " + language);
        getLog().info("Config options: " + configOptions);
        getLog().info("Generator config: " + generatorConfig);
    }

    private boolean isValidLanguage(String language) {
        // Add more languages as needed
        return "java".equals(language) || "typescript".equals(language) || "python".equals(language);
    }
} 