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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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
            // Prepare PRE-PROCESSED, simplified template data
            Map<String, Object> data = new HashMap<>();

            // --- Info --- 
            Map<String, Object> infoMap = new HashMap<>();
            if (openAPI.getInfo() != null) {
                infoMap.put("title", openAPI.getInfo().getTitle());
                infoMap.put("version", openAPI.getInfo().getVersion());
                infoMap.put("description", openAPI.getInfo().getDescription());
                if (openAPI.getInfo().getContact() != null) {
                    Map<String, String> contactMap = new HashMap<>();
                    contactMap.put("name", openAPI.getInfo().getContact().getName());
                    contactMap.put("email", openAPI.getInfo().getContact().getEmail());
                    contactMap.put("url", openAPI.getInfo().getContact().getUrl());
                    infoMap.put("contact", contactMap);
                }
                 if (openAPI.getInfo().getLicense() != null) {
                    Map<String, String> licenseMap = new HashMap<>();
                    licenseMap.put("name", openAPI.getInfo().getLicense().getName());
                    licenseMap.put("url", openAPI.getInfo().getLicense().getUrl());
                    infoMap.put("license", licenseMap);
                }
            }
            data.put("info", infoMap);
            data.put("title", infoMap.getOrDefault("title", "API Documentation")); // For convenience

            // --- Servers --- 
            List<Map<String, String>> serverList = new ArrayList<>();
            if (openAPI.getServers() != null) {
                openAPI.getServers().forEach(server -> {
                    Map<String, String> serverMap = new HashMap<>();
                    serverMap.put("url", server.getUrl());
                    serverMap.put("description", server.getDescription());
                    serverList.add(serverMap);
                });
            }
            data.put("servers", serverList);

            // --- Paths & Operations --- 
            List<Map<String, Object>> pathList = new ArrayList<>();
            if (openAPI.getPaths() != null) {
                openAPI.getPaths().forEach((pathUrl, pathItem) -> {
                    if (pathItem == null) return; // Skip if pathItem itself is null

                    Map<String, Object> pathData = new HashMap<>();
                    pathData.put("url", pathUrl);
                    List<Map<String, Object>> operationList = new ArrayList<>();

                    // Use pathItem.readOperationsMap() for safer iteration
                    if (pathItem.readOperationsMap() != null) {
                         pathItem.readOperationsMap().forEach((httpMethod, operation) -> {
                            if (operation == null) return; // Skip null operations

                            Map<String, Object> opData = new HashMap<>();
                            opData.put("method", httpMethod.name().toUpperCase());
                            opData.put("summary", operation.getSummary());
                            opData.put("description", operation.getDescription());
                            opData.put("operationId", operation.getOperationId());
                            opData.put("id", (operation.getOperationId() != null) ? operation.getOperationId() : ("op_" + pathUrl.replace("/","_").replace("{","").replace("}","") + "_" + httpMethod.name().toLowerCase()));

                            // Parameters
                            List<Map<String, Object>> paramList = new ArrayList<>();
                            if (operation.getParameters() != null) {
                                operation.getParameters().forEach(param -> {
                                    Map<String, Object> paramData = new HashMap<>();
                                    paramData.put("name", param.getName());
                                    paramData.put("in", param.getIn());
                                    paramData.put("required", param.getRequired() != null && param.getRequired());
                                    paramData.put("description", param.getDescription());
                                    if (param.getSchema() != null) {
                                        paramData.put("schemaType", param.getSchema().getType());
                                        paramData.put("schemaFormat", param.getSchema().getFormat());
                                        paramData.put("schemaRef", param.getSchema().get$ref());
                                    }
                                    paramList.add(paramData);
                                });
                            }
                            opData.put("parameters", paramList);

                            // Request Body
                            if (operation.getRequestBody() != null) {
                                Map<String, Object> reqBodyData = new HashMap<>();
                                reqBodyData.put("description", operation.getRequestBody().getDescription());
                                reqBodyData.put("required", operation.getRequestBody().getRequired() != null && operation.getRequestBody().getRequired());
                                Map<String, Map<String, String>> contentMap = new HashMap<>();
                                if (operation.getRequestBody().getContent() != null) {
                                    operation.getRequestBody().getContent().forEach((contentType, mediaType) -> {
                                        if (mediaType != null && mediaType.getSchema() != null) {
                                            Map<String, String> schemaData = new HashMap<>();
                                            schemaData.put("type", mediaType.getSchema().getType());
                                            schemaData.put("ref", mediaType.getSchema().get$ref());
                                            contentMap.put(contentType, schemaData);
                                        }
                                    });
                                }
                                reqBodyData.put("content", contentMap);
                                opData.put("requestBody", reqBodyData);
                            }

                            // Responses
                            Map<String, Map<String, Object>> responsesMap = new HashMap<>();
                            if (operation.getResponses() != null) {
                                operation.getResponses().forEach((code, response) -> {
                                    if (response == null) return;
                                    Map<String, Object> responseData = new HashMap<>();
                                    responseData.put("description", response.getDescription());
                                    Map<String, Map<String, String>> contentMap = new HashMap<>();
                                     if (response.getContent() != null) {
                                        response.getContent().forEach((contentType, mediaType) -> {
                                            if (mediaType != null && mediaType.getSchema() != null) {
                                                Map<String, String> schemaData = new HashMap<>();
                                                schemaData.put("type", mediaType.getSchema().getType());
                                                schemaData.put("ref", mediaType.getSchema().get$ref());
                                                contentMap.put(contentType, schemaData);
                                            }
                                        });
                                    }
                                    responseData.put("content", contentMap);
                                    responsesMap.put(code, responseData);
                                });
                            }
                            opData.put("responses", responsesMap);

                            operationList.add(opData);
                        });
                    }
                    // Sort operations for consistent order (optional)
                    operationList.sort(Comparator.comparing(o -> (String) o.get("method"))); 
                    pathData.put("operations", operationList);
                    pathList.add(pathData);
                });
            }
            // Sort paths for consistent order (optional)
            pathList.sort(Comparator.comparing(p -> (String) p.get("url"))); 
            data.put("paths", pathList);

            // --- Schemas --- 
            List<Map<String, Object>> schemaList = new ArrayList<>();
            if (openAPI.getComponents() != null && openAPI.getComponents().getSchemas() != null) {
                openAPI.getComponents().getSchemas().forEach((name, schema) -> {
                     if (schema == null) return;
                     Map<String, Object> schemaData = new HashMap<>();
                     schemaData.put("name", name);
                     schemaData.put("type", schema.getType());
                     schemaData.put("format", schema.getFormat());
                     schemaData.put("description", schema.getDescription());
                     schemaData.put("requiredFields", schema.getRequired()); // List<String>
                     
                     List<Map<String, Object>> propList = new ArrayList<>();
                     if (schema.getProperties() != null) {
                         schema.getProperties().forEach((propName, propSchemaObj) -> {
                             if (propSchemaObj == null) return;
                             // Cast the value to Schema
                             io.swagger.v3.oas.models.media.Schema propSchema = (io.swagger.v3.oas.models.media.Schema) propSchemaObj;
                             
                             Map<String, Object> propData = new HashMap<>();
                             propData.put("name", propName);
                             propData.put("type", propSchema.getType());
                             propData.put("format", propSchema.getFormat());
                             propData.put("description", propSchema.getDescription());
                             propData.put("ref", propSchema.get$ref());
                             if (propSchema.getItems() != null) { // Handle array items
                                 // Cast item schema as well
                                 io.swagger.v3.oas.models.media.Schema itemSchema = propSchema.getItems();
                                 Map<String, String> itemsData = new HashMap<>();
                                 itemsData.put("type", itemSchema.getType());
                                 itemsData.put("ref", itemSchema.get$ref());
                                 propData.put("items", itemsData);
                             }
                             propList.add(propData);
                         });
                         // Sort properties alphabetically (optional)
                         propList.sort(Comparator.comparing(p -> (String) p.get("name"))); 
                     }
                     schemaData.put("properties", propList);
                     schemaList.add(schemaData);
                });
                // Sort schemas alphabetically (optional)
                schemaList.sort(Comparator.comparing(s -> (String) s.get("name"))); 
            }
            data.put("schemas", schemaList);

            // Add other necessary simple data for templates
            data.put("basePackage", configuration.getBasePackage());
            data.put("configClassName", configuration.getConfigClassName());
            // Note: swaggerUIConfig is complex, pass it directly or simplify if needed
            data.put("swaggerUIConfig", configuration.getSwaggerUIConfig()); 
             // Add any user-provided template variables
            if (templateVariables != null) {
                data.putAll(templateVariables);
            }

            // Process each template mapping
            if (configuration.getTemplateMappings() != null) {
                for (Map.Entry<String, String> entry : configuration.getTemplateMappings().entrySet()) {
                    String templateName = entry.getKey();
                    String outputFileName = entry.getValue();
                    if (templateName == null || outputFileName == null) {
                        getLog().warn("Skipping invalid template mapping entry: " + entry);
                        continue;
                    }
                    Path outputPath = Paths.get(outputDirectory.toString(), outputFileName);
                    templateManager.writeTemplate(templateName, data, outputPath);
                    getLog().info("Generated " + outputFileName);
                }
            } else {
                 getLog().info("No template mappings configured.");
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