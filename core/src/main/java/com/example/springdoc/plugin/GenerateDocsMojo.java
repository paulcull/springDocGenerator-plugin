package com.example.springdoc.plugin;

import com.example.springdoc.plugin.config.PluginConfiguration;
import com.example.springdoc.plugin.config.SwaggerUIConfig;
import com.example.springdoc.plugin.template.TemplateManager;
import freemarker.template.TemplateException;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.core.models.ParseOptions;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mojo(name = "generate-docs", defaultPhase = LifecyclePhase.PROCESS_RESOURCES)
public class GenerateDocsMojo extends AbstractMojo {

    @Parameter(property = "springdoc.openApiSpec", required = true)
    private String openApiSpec;

    @Parameter(property = "springdoc.outputDirectory", defaultValue = "${project.build.directory}/classes/generated-docs")
    private File outputDirectory;

    @Parameter(property = "springdoc.injectHostingEndpoints", defaultValue = "true")
    private boolean injectHostingEndpoints;

    @Parameter(property = "springdoc.redirectFrom", defaultValue = "/docs")
    private String redirectFrom;

    @Parameter(property = "springdoc.redirectTo", defaultValue = "/docs/index.html")
    private String redirectTo;

    @Parameter(property = "springdoc.basePackage", defaultValue = "${project.groupId}")
    private String basePackage;

    @Parameter(property = "springdoc.templateDirectory", defaultValue = "templates")
    private String templateDirectory;

    @Parameter(property = "springdoc.resourcePaths", defaultValue = "static")
    private String[] resourcePaths;

    @Parameter(property = "springdoc.configClassName", defaultValue = "DocumentationConfig")
    private String configClassName;

    @Parameter(property = "springdoc.resourceHandlerPath", defaultValue = "/docs/**")
    private String resourceHandlerPath;

    @Parameter(property = "springdoc.resourceLocation", defaultValue = "classpath:/static/docs/")
    private String resourceLocation;

    @Parameter(property = "springdoc.injectSwaggerUI", defaultValue = "true")
    private boolean injectSwaggerUI;

    @Parameter(property = "springdoc.swaggerUIPath", defaultValue = "/swagger-ui.html")
    private String swaggerUIPath;

    @Parameter(property = "springdoc.swaggerUIVersion", defaultValue = "5.11.0")
    private String swaggerUIVersion;

    @Parameter
    private SwaggerUIConfig swaggerUIConfig;

    @Parameter(property = "project", readonly = true)
    private MavenProject project;

    @Parameter
    private Map<String, String> templateMappings;

    @Parameter
    private Map<String, Object> templateVariables;

    private TemplateManager templateManager;
    private PluginConfiguration configuration;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            // Initialize configuration with provided values
            configuration = new PluginConfiguration();
            configuration.setBasePackage(basePackage);
            configuration.setTemplateDirectory(templateDirectory);
            configuration.setResourcePaths(Arrays.asList(resourcePaths));
            configuration.setTemplateMappings(templateMappings != null ? templateMappings : new HashMap<>());
            configuration.setConfigClassName(configClassName);
            configuration.setResourceHandlerPath(resourceHandlerPath);
            configuration.setResourceLocation(resourceLocation);
            configuration.setDefaultRedirectFrom(redirectFrom);
            configuration.setDefaultRedirectTo(redirectTo);
            configuration.setInjectSwaggerUI(injectSwaggerUI);
            configuration.setSwaggerUIPath(swaggerUIPath);
            configuration.setSwaggerUIVersion(swaggerUIVersion);
            configuration.setSwaggerUIConfig(swaggerUIConfig != null ? swaggerUIConfig : new SwaggerUIConfig());

            // Initialize template manager
            templateManager = new TemplateManager(configuration.getTemplateDirectory(), getLog());

            getLog().info("Reading OpenAPI specification from: " + openApiSpec);
            OpenAPI openAPI = readOpenAPISpec();
            
            // Create output directory if it doesn't exist
            if (!outputDirectory.exists()) {
                outputDirectory.mkdirs();
            }
            
            // Process templates and generate documentation
            processTemplates(openAPI);
            
            // Generate configuration class if enabled
            if (injectHostingEndpoints) {
                getLog().info("Injecting documentation hosting configuration...");
                injectConfigurationClass();
            } else {
                getLog().info("Skipping documentation hosting configuration (injectHostingEndpoints=false)");
            }
            
            getLog().info("Documentation generated successfully in " + outputDirectory);
        } catch (MojoExecutionException e) {
            throw e;
        } catch (Exception e) {
            throw new MojoExecutionException("Failed to generate documentation: " + e.getMessage(), e);
        }
    }

    private void injectConfigurationClass() throws IOException, TemplateException, MojoExecutionException {
        try {
            Path sourcePath = createSourcePath();

            // Generate DocumentationConfig
            Map<String, Object> data = new HashMap<>();
            data.put("packageName", configuration.getBasePackage());
            data.put("className", configuration.getConfigClassName());
            data.put("resourceHandlerPath", configuration.getResourceHandlerPath());
            data.put("resourceLocation", configuration.getResourceLocation());
            data.put("redirectFrom", configuration.getDefaultRedirectFrom());
            data.put("redirectTo", configuration.getDefaultRedirectTo());

            // Add any custom template variables
            if (templateVariables != null) {
                data.putAll(templateVariables);
            }

            Path configFile = sourcePath.resolve(configuration.getConfigClassName() + ".java");
            templateManager.writeTemplate("DocumentationConfig.java.ftl", data, configFile);
            getLog().info("Generated documentation configuration class: " + configFile);

            // Generate DocumentationController
            Path controllerPath = sourcePath.resolve("controller");
            Files.createDirectories(controllerPath);
            Path controllerFile = controllerPath.resolve("DocumentationController.java");
            templateManager.writeTemplate("DocumentationController.java.ftl", data, controllerFile);
            getLog().info("Generated documentation controller class: " + controllerFile);

            // Generate SwaggerUIConfig if enabled
            if (swaggerUIConfig != null && swaggerUIConfig.isEnabled()) {
                data = new HashMap<>();
                data.put("packageName", configuration.getBasePackage());
                data.put("swaggerUIConfig", configuration.getSwaggerUIConfig());
                if (templateVariables != null) {
                    data.putAll(templateVariables);
                }

                Path swaggerConfigFile = sourcePath.resolve("SwaggerUIConfig.java");
                templateManager.writeTemplate("SwaggerUIConfig.java.ftl", data, swaggerConfigFile);
                getLog().info("Generated Swagger UI configuration class: " + swaggerConfigFile);
            }

            addGeneratedSourcesToProject();

        } catch (IOException | TemplateException e) {
            throw new MojoExecutionException("Failed to inject documentation hosting configuration: " + e.getMessage(), e);
        }
    }

    private OpenAPI readOpenAPISpec() throws MojoExecutionException {
        try {
            ParseOptions options = new ParseOptions();
            options.setResolve(true);
            options.setResolveFully(true);

            if (openApiSpec == null || openApiSpec.trim().isEmpty()) {
                throw new MojoExecutionException("OpenAPI specification path cannot be null or empty");
            }

            // Check if the spec is a URL
            if (openApiSpec.startsWith("http://") || openApiSpec.startsWith("https://")) {
                SwaggerParseResult result = new OpenAPIParser().readLocation(openApiSpec, null, options);
                validateParseResult(result, "URL");
                return result.getOpenAPI();
            }

            // Handle file-based spec
            File specFile = new File(openApiSpec);
            if (!specFile.exists()) {
                throw new MojoExecutionException("OpenAPI specification file not found: " + openApiSpec);
            }

            if (!specFile.canRead()) {
                throw new MojoExecutionException("Cannot read OpenAPI specification file: " + openApiSpec);
            }

            String fileContent = Files.readString(specFile.toPath());
            if (fileContent.trim().isEmpty()) {
                throw new MojoExecutionException("OpenAPI specification file is empty: " + openApiSpec);
            }

            // Try to parse as YAML first, then JSON
            try {
                SwaggerParseResult result = new OpenAPIParser().readContents(fileContent, null, options);
                validateParseResult(result, "file");
                return result.getOpenAPI();
            } catch (Exception e) {
                throw new MojoExecutionException("Failed to parse OpenAPI specification: " + e.getMessage());
            }
        } catch (MojoExecutionException e) {
            throw e;
        } catch (Exception e) {
            throw new MojoExecutionException("Failed to read OpenAPI specification: " + e.getMessage(), e);
        }
    }

    private void validateParseResult(SwaggerParseResult result, String source) throws MojoExecutionException {
        if (result == null) {
            throw new MojoExecutionException("Failed to parse OpenAPI specification: Parser returned null result");
        }

        if (result.getOpenAPI() == null) {
            String messages = result.getMessages() != null ? String.join(", ", result.getMessages()) : "No error messages available";
            throw new MojoExecutionException("Failed to parse OpenAPI specification from " + source + ": " + messages);
        }

        if (result.getMessages() != null && !result.getMessages().isEmpty()) {
            for (String message : result.getMessages()) {
                getLog().warn("OpenAPI parsing message: " + message);
            }
        }
    }

    private void processTemplates(OpenAPI openAPI) throws IOException, TemplateException, MojoExecutionException {
        try {
            // Prepare template data
            Map<String, Object> data = new HashMap<>();
            data.put("openAPI", openAPI);
            data.put("title", openAPI.getInfo().getTitle());
            data.put("description", openAPI.getInfo().getDescription());
            data.put("version", openAPI.getInfo().getVersion());
            data.put("basePackage", configuration.getBasePackage());
            data.put("configClassName", configuration.getConfigClassName());
            data.put("swaggerUIConfig", configuration.getSwaggerUIConfig());

            // Process each template
            for (Map.Entry<String, String> entry : configuration.getTemplateMappings().entrySet()) {
                String templateName = entry.getKey();
                String outputFileName = entry.getValue();
                Path outputPath = Paths.get(outputDirectory.toString(), outputFileName);
                templateManager.writeTemplate(templateName, data, outputPath);
                getLog().info("Generated " + outputFileName);
            }
        } catch (IOException | TemplateException e) {
            throw new MojoExecutionException("Failed to process templates: " + e.getMessage(), e);
        }
    }

    private Path createSourcePath() throws IOException {
        String[] packageParts = configuration.getBasePackage().split("\\.");
        Path sourcePath = Paths.get(project.getBuild().getDirectory(), "generated-sources", "annotations");
        for (String part : packageParts) {
            sourcePath = sourcePath.resolve(part);
        }
        Files.createDirectories(sourcePath);
        return sourcePath;
    }

    private void addGeneratedSourcesToProject() {
        String generatedSourcesPath = Paths.get(project.getBuild().getDirectory(), "generated-sources", "annotations").toString();
        project.addCompileSourceRoot(generatedSourcesPath);
        project.addTestCompileSourceRoot(generatedSourcesPath);
        getLog().info("Added generated sources to compile and test source roots: " + generatedSourcesPath);
    }
} 