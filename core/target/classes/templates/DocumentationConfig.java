package com.example.springdoc.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ClassPathResource;
import java.io.IOException;

@Configuration
public class DocumentationConfig implements WebMvcConfigurer {
    private static final Logger logger = LoggerFactory.getLogger(DocumentationConfig.class);

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/docs/**")
            .addResourceLocations("classpath:/generated-docs/")
            .resourceChain(true)
            .addResolver(new PathResourceResolver() {
                @Override
                protected Resource getResource(String resourcePath, Resource location) throws IOException {
                    logger.debug("Serving documentation file: " + resourcePath);
                    Resource resource = location.createRelative(resourcePath);
                    return resource.exists() && resource.isReadable() ? resource : null;
                }
            });
    }
} 