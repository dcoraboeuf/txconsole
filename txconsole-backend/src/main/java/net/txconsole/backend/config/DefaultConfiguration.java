package net.txconsole.backend.config;

import net.txconsole.backend.support.fm.FnResourceGui;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.Validator;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DefaultConfiguration {

    @Bean
    public Validator validator() {
        return new LocalValidatorFactoryBean();
    }

    @Bean
    @Qualifier("templating")
    public FreeMarkerConfigurationFactoryBean templateFreemarkerConfig() {
        FreeMarkerConfigurationFactoryBean f = new FreeMarkerConfigurationFactoryBean();
        f.setTemplateLoaderPath("classpath:META-INF/templates/");
        // Freemarker variables
        Map<String, Object> variables = new HashMap<>();
        // - resources
        variables.put("resourceGui", new FnResourceGui());
        // OK
        f.setFreemarkerVariables(variables);
        return f;
    }

}
