package ${packageName};

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

@Configuration
public class ${className} implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(${className}.class);

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("${resourceHandlerPath}")
                .addResourceLocations("${resourceLocation}")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        logger.debug("Serving documentation file: {}", resourcePath);
                        Resource requestedResource = location.createRelative(resourcePath);
                        return requestedResource.exists() && requestedResource.isReadable() ? requestedResource : null;
                    }
                });

        // Add redirect from /docs to /docs/index.html
        registry.addResourceHandler("${redirectFrom}")
                .addResourceLocations("${resourceLocation}")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        logger.debug("Redirecting from {} to {}", "${redirectFrom}", "${redirectTo}");
                        return location.createRelative("index.html");
                    }
                });
    }
} 