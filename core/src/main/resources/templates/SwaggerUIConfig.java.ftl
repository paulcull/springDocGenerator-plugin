package ${packageName};

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

@Configuration
public class SwaggerUIConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("${swaggerUIConfig.path}/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/swagger-ui/${swaggerUIConfig.version}/");
    }

    @Bean
    public InternalResourceViewResolver viewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("${swaggerUIConfig.path}/");
        resolver.setSuffix(".html");
        resolver.setViewClass(JstlView.class);
        return resolver;
    }
} 