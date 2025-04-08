package com.example.springdoc.plugin.template;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.apache.maven.plugin.logging.Log;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class TemplateManager {
    private final Configuration configuration;
    private final Log log;

    public TemplateManager(String templateDirectory, Log log) throws IOException {
        this.log = log;
        this.configuration = new Configuration(Configuration.VERSION_2_3_32);
        configuration.setClassLoaderForTemplateLoading(getClass().getClassLoader(), templateDirectory);
        configuration.setDefaultEncoding("UTF-8");
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        configuration.setLogTemplateExceptions(false);
        configuration.setWrapUncheckedExceptions(true);
    }

    public void writeTemplate(String templateName, Object data, Path outputPath) throws IOException, TemplateException {
        log.debug("Processing template: " + templateName);
        Template template = configuration.getTemplate(templateName);

        // Create parent directories if they don't exist
        Files.createDirectories(outputPath.getParent());

        // Process template and write to file
        try (Writer writer = Files.newBufferedWriter(outputPath)) {
            template.process(data, writer);
        }
        log.debug("Template processed successfully: " + outputPath);
    }

    public String processTemplate(String templateName, Object data) throws IOException, TemplateException {
        log.debug("Processing template: " + templateName);
        Template template = configuration.getTemplate(templateName);

        // Process template to string
        try (StringWriter writer = new StringWriter()) {
            template.process(data, writer);
            return writer.toString();
        }
    }
} 