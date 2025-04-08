package com.example.springdoc.plugin.config;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class PluginConfiguration {
    private String basePackage;
    private String templateDirectory;
    private List<String> resourcePaths;
    private Map<String, String> templateMappings;
    private String configClassName;
    private String resourceHandlerPath;
    private String resourceLocation;
    private String defaultRedirectFrom;
    private String defaultRedirectTo;
    private boolean injectSwaggerUI;
    private String swaggerUIPath;
    private String swaggerUIVersion;
    private SwaggerUIConfig swaggerUIConfig;
} 