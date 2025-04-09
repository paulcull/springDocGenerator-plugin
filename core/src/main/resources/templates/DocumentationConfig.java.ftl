package ${packageName};

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

@Configuration
public class ${className} implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("${resourceHandlerPath}")
                .addResourceLocations("${resourceLocation}")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        Resource requestedResource = location.createRelative(resourcePath);
                        // Ensure resource exists and is readable, otherwise return null (serves 404)
                        return requestedResource.exists() && requestedResource.isReadable() ? requestedResource : null;
                    }
                });

        // Add redirect from the specified path (e.g., /docs) to the index file (e.g., /docs/index.html)
        registry.addResourceHandler("${redirectFrom}")
                .addResourceLocations("${resourceLocation}")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        // Always serve index.html for the redirect path
                        return location.createRelative("index.html");
                    }
                });
    }
} 