package com.example.springdoc.plugin.config;

public class SwaggerUIConfig {
    private boolean enabled = true;
    private String path = "/swagger-ui.html";
    private String version = "5.11.0";
    private String title = "API Documentation";
    private String description = "API Documentation powered by Swagger UI";
    private String configUrl = "/v3/api-docs/swagger-config";
    private String url = "/v3/api-docs";
    private boolean deepLinking = true;
    private boolean displayOperationId = false;
    private boolean defaultModelsExpandDepth = true;
    private int defaultModelExpandDepth = 1;
    private boolean displayRequestDuration = false;
    private boolean filter = false;
    private boolean showExtensions = false;
    private boolean showCommonExtensions = false;
    private String layout = "BaseLayout";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getConfigUrl() {
        return configUrl;
    }

    public void setConfigUrl(String configUrl) {
        this.configUrl = configUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isDeepLinking() {
        return deepLinking;
    }

    public void setDeepLinking(boolean deepLinking) {
        this.deepLinking = deepLinking;
    }

    public boolean isDisplayOperationId() {
        return displayOperationId;
    }

    public void setDisplayOperationId(boolean displayOperationId) {
        this.displayOperationId = displayOperationId;
    }

    public boolean isDefaultModelsExpandDepth() {
        return defaultModelsExpandDepth;
    }

    public void setDefaultModelsExpandDepth(boolean defaultModelsExpandDepth) {
        this.defaultModelsExpandDepth = defaultModelsExpandDepth;
    }

    public int getDefaultModelExpandDepth() {
        return defaultModelExpandDepth;
    }

    public void setDefaultModelExpandDepth(int defaultModelExpandDepth) {
        this.defaultModelExpandDepth = defaultModelExpandDepth;
    }

    public boolean isDisplayRequestDuration() {
        return displayRequestDuration;
    }

    public void setDisplayRequestDuration(boolean displayRequestDuration) {
        this.displayRequestDuration = displayRequestDuration;
    }

    public boolean isFilter() {
        return filter;
    }

    public void setFilter(boolean filter) {
        this.filter = filter;
    }

    public boolean isShowExtensions() {
        return showExtensions;
    }

    public void setShowExtensions(boolean showExtensions) {
        this.showExtensions = showExtensions;
    }

    public boolean isShowCommonExtensions() {
        return showCommonExtensions;
    }

    public void setShowCommonExtensions(boolean showCommonExtensions) {
        this.showCommonExtensions = showCommonExtensions;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }
} 