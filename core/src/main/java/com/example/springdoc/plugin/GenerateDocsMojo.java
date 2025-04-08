package com.example.springdoc.plugin;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.parser.core.models.ParseOptions;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.commons.io.FileUtils;
import freemarker.template.TemplateExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Mojo(name = "generate-docs", defaultPhase = LifecyclePhase.PROCESS_RESOURCES)
public class GenerateDocsMojo extends AbstractMojo {

    @org.apache.maven.plugins.annotations.Parameter(property = "openApiSpec", required = true)
    private String openApiSpec;

    @org.apache.maven.plugins.annotations.Parameter(property = "outputDirectory", defaultValue = "${project.build.directory}/generated-docs")
    private String outputDirectory;

    @org.apache.maven.plugins.annotations.Parameter(property = "outputFormat", defaultValue = "html")
    private String outputFormat;

    @org.apache.maven.plugins.annotations.Parameter(property = "injectHostingEndpoints", defaultValue = "true")
    private boolean injectHostingEndpoints;

    @org.apache.maven.plugins.annotations.Parameter(property = "project", readonly = true)
    private MavenProject project;

    private Configuration cfg;

    @Override
    public void execute() throws MojoExecutionException {
        try {
            getLog().info("Reading OpenAPI specification from: " + openApiSpec);
            OpenAPI openAPI = readOpenAPISpec();
            
            // Initialize FreeMarker configuration
            cfg = new Configuration(Configuration.VERSION_2_3_32);
            cfg.setDefaultEncoding("UTF-8");
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            cfg.setLogTemplateExceptions(false);
            cfg.setWrapUncheckedExceptions(true);
            cfg.setFallbackOnNullLoopVariable(false);
            
            // Create output directory if it doesn't exist
            File outputDir = new File(outputDirectory);
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }
            
            // Process templates and generate documentation
            processTemplates(openAPI);
            
            // Generate configuration class if enabled
            if (injectHostingEndpoints) {
                generateConfigurationClass();
            }
            
            getLog().info("Documentation generated successfully in " + outputDirectory);
        } catch (MojoExecutionException e) {
            throw e;
        } catch (Exception e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    private void injectConfigurationClass() throws IOException {
        // Create the package directory structure in source directory first
        Path sourcePath = Paths.get(project.getBuild().getDirectory(), "generated-sources", "annotations", "com", "example", "springdoc", "config");
        Files.createDirectories(sourcePath);

        // Read and write the configuration class
        String configTemplate = readResourceFile("templates/DocumentationConfig.java");
        Path configFile = sourcePath.resolve("DocumentationConfig.java");
        Files.write(configFile, configTemplate.getBytes());

        // Add the generated sources directory to both compile and test source roots
        String generatedSourcesPath = Paths.get(project.getBuild().getDirectory(), "generated-sources", "annotations").toString();
        project.addCompileSourceRoot(generatedSourcesPath);
        project.addTestCompileSourceRoot(generatedSourcesPath);

        getLog().info("Generated configuration class: " + configFile);
        getLog().info("Added generated sources to compile and test source roots: " + generatedSourcesPath);
    }

    private String readResourceFile(String resourcePath) throws IOException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }
            return new String(is.readAllBytes());
        }
    }

    private OpenAPI readOpenAPISpec() throws MojoExecutionException {
        try {
            getLog().info("Reading OpenAPI specification from: " + openApiSpec);
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

        OpenAPI openAPI = result.getOpenAPI();
        if (openAPI.getInfo() == null) {
            throw new MojoExecutionException("Invalid OpenAPI specification: Missing 'info' section");
        }

        Info info = openAPI.getInfo();
        if (info.getTitle() == null || info.getTitle().trim().isEmpty()) {
            throw new MojoExecutionException("Invalid OpenAPI specification: Missing 'info.title'");
        }

        if (info.getVersion() == null || info.getVersion().trim().isEmpty()) {
            throw new MojoExecutionException("Invalid OpenAPI specification: Missing 'info.version'");
        }

        // Log successful validation
        getLog().info("Successfully validated OpenAPI specification: " + info.getTitle() + " v" + info.getVersion());
    }

    private Template getTemplate(String templateName) throws IOException {
        // First try to load from classpath
        cfg.setClassLoaderForTemplateLoading(getClass().getClassLoader(), "templates");
        try {
            return cfg.getTemplate(templateName);
        } catch (IOException e) {
            getLog().warn("Failed to load template from classpath: " + e.getMessage());
        }

        // If not found in classpath, try filesystem
        Path templatePath = Paths.get("src/main/resources/templates", templateName);
        if (Files.exists(templatePath)) {
            cfg.setDirectoryForTemplateLoading(templatePath.getParent().toFile());
            try {
                return cfg.getTemplate(templateName);
            } catch (IOException e) {
                getLog().error("Failed to load template: " + templateName);
                getLog().error("Available resources in classpath:");
                try (InputStream is = getClass().getClassLoader().getResourceAsStream("templates")) {
                    if (is != null) {
                        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                            String resource;
                            while ((resource = br.readLine()) != null) {
                                getLog().error(" - " + resource);
                            }
                        }
                    }
                }
                throw e;
            }
        }

        throw new IOException("Template not found: " + templateName);
    }

    private void processTemplates(OpenAPI openAPI) throws IOException, TemplateException {
        Map<String, Object> model = new HashMap<>();
        
        // Add API info
        Info info = openAPI.getInfo();
        Map<String, Object> infoMap = new HashMap<>();
        infoMap.put("title", info.getTitle());
        infoMap.put("description", info.getDescription() != null ? info.getDescription() : "");
        infoMap.put("version", info.getVersion());
        if (info.getContact() != null) {
            infoMap.put("contact", Map.of(
                "name", info.getContact().getName() != null ? info.getContact().getName() : "",
                "email", info.getContact().getEmail() != null ? info.getContact().getEmail() : "",
                "url", info.getContact().getUrl() != null ? info.getContact().getUrl() : ""
            ));
        }
        if (info.getLicense() != null) {
            infoMap.put("license", Map.of(
                "name", info.getLicense().getName() != null ? info.getLicense().getName() : "",
                "url", info.getLicense().getUrl() != null ? info.getLicense().getUrl() : ""
            ));
        }
        model.put("info", infoMap);
        
        // Add paths
        model.put("paths", extractPaths(openAPI));
        
        // Add components if available
        if (openAPI.getComponents() != null && openAPI.getComponents().getSchemas() != null) {
            model.put("schemas", openAPI.getComponents().getSchemas());
        }
        
        // Add servers if available
        if (openAPI.getServers() != null && !openAPI.getServers().isEmpty()) {
            model.put("servers", openAPI.getServers());
        }

        // Add tags if available
        if (openAPI.getTags() != null && !openAPI.getTags().isEmpty()) {
            List<Map<String, String>> tags = openAPI.getTags().stream()
                .map(tag -> Map.of(
                    "name", tag.getName() != null ? tag.getName() : "",
                    "description", tag.getDescription() != null ? tag.getDescription() : ""
                ))
                .collect(Collectors.toList());
            model.put("tags", tags);
        }

        // Create output directory if it doesn't exist
        File outputDir = new File(outputDirectory);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        // Generate all documentation files
        String[] templateFiles = {"index.html", "api-docs.html", "sdk.html"};
        for (String templateFile : templateFiles) {
            try {
                Template template = getTemplate(templateFile);
                try (Writer writer = new FileWriter(new File(outputDir, templateFile))) {
                    // Add common navigation model attributes
                    model.put("navigation", Arrays.asList(
                        Map.of("title", "Overview", "href", "index.html", "id", "overview"),
                        Map.of("title", "API Reference", "href", "api-docs.html", "id", "api-docs"),
                        Map.of("title", "SDK Guide", "href", "sdk.html", "id", "sdk")
                    ));
                    model.put("currentPage", templateFile);
                    template.process(model, writer);
                    getLog().info("Generated " + templateFile);
                }
            } catch (IOException | TemplateException e) {
                getLog().error("Failed to generate " + templateFile + ": " + e.getMessage());
                throw e;
            }
        }

        // Copy any additional resources (images, CSS, etc.)
        copyResources(outputDir);
    }

    private void copyResources(File outputDir) throws IOException {
        // Copy Bootstrap CSS and JS
        String[] resources = {
            "https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css",
            "https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"
        };

        for (String resource : resources) {
            String fileName = resource.substring(resource.lastIndexOf('/') + 1);
            File resourceFile = new File(outputDir, fileName);
            if (!resourceFile.exists()) {
                try (InputStream in = new java.net.URL(resource).openStream();
                     FileOutputStream out = new FileOutputStream(resourceFile)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                }
            }
        }
    }

    private List<Map<String, Object>> extractPaths(OpenAPI openAPI) {
        List<Map<String, Object>> pathsList = new ArrayList<>();
        Map<String, PathItem> paths = openAPI.getPaths();
        if (paths != null) {
            for (Map.Entry<String, PathItem> entry : paths.entrySet()) {
                String url = entry.getKey();
                PathItem pathItem = entry.getValue();

                Map<String, Object> pathInfo = new HashMap<>();
                pathInfo.put("url", url);
                List<Map<String, Object>> operations = new ArrayList<>();

                // Add GET operations
                addOperationToList(operations, "GET", pathItem.getGet());
                // Add POST operations
                addOperationToList(operations, "POST", pathItem.getPost());
                // Add PUT operations
                addOperationToList(operations, "PUT", pathItem.getPut());
                // Add DELETE operations
                addOperationToList(operations, "DELETE", pathItem.getDelete());
                // Add PATCH operations
                addOperationToList(operations, "PATCH", pathItem.getPatch());

                pathInfo.put("operations", operations);
                pathsList.add(pathInfo);
            }
        }
        return pathsList;
    }

    private void addOperationToList(List<Map<String, Object>> operations, String method, Operation operation) {
        if (operation != null) {
            Map<String, Object> operationInfo = new HashMap<>();
            operationInfo.put("method", method);
            operationInfo.put("summary", operation.getSummary());
            operationInfo.put("description", operation.getDescription());
            operationInfo.put("operationId", operation.getOperationId());
            operationInfo.put("tags", operation.getTags());
            
            // Add parameters
            if (operation.getParameters() != null && !operation.getParameters().isEmpty()) {
                List<Map<String, Object>> parameters = new ArrayList<>();
                for (io.swagger.v3.oas.models.parameters.Parameter param : operation.getParameters()) {
                    Map<String, Object> paramInfo = new HashMap<>();
                    paramInfo.put("name", param.getName());
                    paramInfo.put("in", param.getIn());
                    paramInfo.put("description", param.getDescription());
                    paramInfo.put("required", param.getRequired());
                    parameters.add(paramInfo);
                }
                operationInfo.put("parameters", parameters);
            }
            
            // Add responses
            if (operation.getResponses() != null && !operation.getResponses().isEmpty()) {
                Map<String, Object> responses = new HashMap<>();
                for (Map.Entry<String, ApiResponse> entry : operation.getResponses().entrySet()) {
                    Map<String, Object> responseInfo = new HashMap<>();
                    responseInfo.put("description", entry.getValue().getDescription());
                    responses.put(entry.getKey(), responseInfo);
                }
                operationInfo.put("responses", responses);
            }
            
            operations.add(operationInfo);
        }
    }

    private void generateConfigurationClass() throws MojoExecutionException {
        try {
            // Create the package directory structure in source directory first
            Path sourcePath = Paths.get(project.getBuild().getDirectory(), "generated-sources", "annotations", "com", "example", "springdoc", "config");
            Files.createDirectories(sourcePath);

            // Read and write the configuration class
            String configTemplate = readResourceFile("templates/DocumentationConfig.java");
            Path configFile = sourcePath.resolve("DocumentationConfig.java");
            Files.write(configFile, configTemplate.getBytes());

            // Add the generated sources directory to both compile and test source roots
            String generatedSourcesPath = Paths.get(project.getBuild().getDirectory(), "generated-sources", "annotations").toString();
            project.addCompileSourceRoot(generatedSourcesPath);
            project.addTestCompileSourceRoot(generatedSourcesPath);

            getLog().info("Generated configuration class: " + configFile);
            getLog().info("Added generated sources to compile and test source roots: " + generatedSourcesPath);
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to generate configuration class", e);
        }
    }
} 