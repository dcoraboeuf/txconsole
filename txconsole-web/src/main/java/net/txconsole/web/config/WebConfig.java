package net.txconsole.web.config;

import net.sf.jstring.Strings;
import net.txconsole.core.security.SecurityUtils;
import net.txconsole.web.locale.LocaleInterceptor;
import net.txconsole.web.support.DefaultErrorHandlingMultipartResolver;
import net.txconsole.web.support.ErrorHandlingMultipartResolver;
import net.txconsole.web.support.WebInterceptor;
import net.txconsole.web.support.fm.*;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebMvc
@PropertySource("/META-INF/strings/core.properties")
public class WebConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private Environment env;
    @Autowired
    private Strings strings;
    @Autowired
    private ObjectMapper jacksonObjectMapper;
    @Autowired
    private SecurityUtils securityUtils;

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.clear();
        // Plain text
        converters.add(new StringHttpMessageConverter());
        // JSON
        MappingJacksonHttpMessageConverter mapper = new MappingJacksonHttpMessageConverter();
        mapper.setObjectMapper(jacksonObjectMapper);
        converters.add(mapper);
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String version = env.getProperty("app.version");
        registry.addResourceHandler(String.format("/resources/v%s/**", version)).addResourceLocations("/static/");
        registry.addResourceHandler("/extension/**").addResourceLocations("classpath:/META-INF/resources/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LocaleInterceptor(strings));
        registry.addInterceptor(new WebInterceptor(strings));
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.favorPathExtension(false).favorParameter(true);
    }

    @Bean
    public FreeMarkerConfig freemarkerConfig() {
        // Configurer
        FreeMarkerConfigurer c = new FreeMarkerConfigurer();
        c.setTemplateLoaderPaths(new String[]{
                "/WEB-INF/views"
        });
        // TODO Extension views
        /*
        c.setPostTemplateLoaders(new TemplateLoader[]{
                new ExtensionTemplateLoader(extensionManager)
        });
        */
        // Freemarker variables
        Map<String, Object> variables = new HashMap<>();
        // - localization
        variables.put("loc", new FnLoc(strings));
        variables.put("locSelected", new FnLocSelected());
        variables.put("locLanguages", new FnLocLanguages(strings));
        variables.put("locFormatDate", new FnLocFormatDate(strings));
        variables.put("locFormatTime", new FnLocFormatTime(strings));
        // - security
        variables.put("secLogged", new FnSecLogged(securityUtils));
        variables.put("secAccountId", new FnSecAccountId(securityUtils));
        variables.put("secAdmin", new FnSecAdmin(securityUtils));
        variables.put("secDisplayName", new FnSecDisplayName(securityUtils));
        // OK
        c.setFreemarkerVariables(variables);
        // OK
        return c;
    }

    @Bean
    public ViewResolver freemarkerViewResolver() {
        FreeMarkerViewResolver o = new FreeMarkerViewResolver();
        o.setCache(false);
        o.setPrefix("");
        o.setSuffix(".html");
        return o;
    }

    @Bean
    public View jsonViewResolver() {
        MappingJacksonJsonView o = new MappingJacksonJsonView();
        o.setObjectMapper(jacksonObjectMapper);
        return o;
    }

    @Bean
    public ErrorHandlingMultipartResolver multipartResolver() {
        return new DefaultErrorHandlingMultipartResolver(512); // Max size in K
    }

}
