package com.example.springdoc.sdk.plugin.sdk;

import com.example.springdoc.sdk.plugin.core.SpringDocSdkPlugin;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.openapitools.codegen.DefaultGenerator;
import org.openapitools.codegen.config.CodegenConfigurator;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mojo(name = "generate-sdk")
public class SpringDocSdkGeneratorMojo extends SpringDocSdkPlugin {
    @Parameter(property = "generatorName", required = true)
    private String generatorName;

    @Parameter(property = "templateDir")
    private File templateDir;

    @Parameter(property = "additionalProperties")
    private Map<String, Object> additionalProperties;

    public void setGeneratorName(String generatorName) {
        this.generatorName = generatorName;
    }

    public void setTemplateDir(File templateDir) {
        this.templateDir = templateDir;
    }

    public void setAdditionalProperties(List<String> properties) {
        this.additionalProperties = new HashMap<>();
        for (String property : properties) {
            String[] parts = property.split("=", 2);
            if (parts.length == 2) {
                additionalProperties.put(parts[0], parts[1]);
            }
        }
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Generating SDK from OpenAPI specification: " + openApiSpec.getAbsolutePath());
        getLog().info("Generator name: " + generatorName);
        getLog().info("Template directory: " + (templateDir != null ? templateDir.getAbsolutePath() : "default"));
        getLog().info("Additional properties: " + additionalProperties);

        try {
            CodegenConfigurator configurator = new CodegenConfigurator()
                    .setGeneratorName(generatorName)
                    .setInputSpec(openApiSpec.getAbsolutePath())
                    .setOutputDir(outputDirectory.getAbsolutePath())
                    .setTemplateDir(templateDir != null ? templateDir.getAbsolutePath() : null);

            if (additionalProperties != null) {
                configurator.setAdditionalProperties(additionalProperties);
            }

            new DefaultGenerator().opts(configurator.toClientOptInput()).generate();
        } catch (Exception e) {
            throw new MojoExecutionException("Failed to generate SDK", e);
        }
    }
}