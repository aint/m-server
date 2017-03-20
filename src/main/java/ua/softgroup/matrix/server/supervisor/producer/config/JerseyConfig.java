package ua.softgroup.matrix.server.supervisor.producer.config;

import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.servlet.ErrorPage;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import ua.softgroup.matrix.server.supervisor.producer.filter.TokenAuthenticationFilter;
import ua.softgroup.matrix.server.supervisor.producer.resources.ReportResource;
import ua.softgroup.matrix.server.supervisor.producer.resources.SummaryResource;
import ua.softgroup.matrix.server.supervisor.producer.resources.TimeResource;

import javax.ws.rs.ApplicationPath;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
@Component
@ApplicationPath("/api/v1")
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        register(ReportResource.class);
        register(SummaryResource.class);
        register(TimeResource.class);

        register(GenericExceptionMapper.class);
        register(ValidationExceptionMapper.class);
        register(ValidationConfigurationContextResolver.class);
        register(TokenAuthenticationFilter.class);

        configureSwagger();
    }

    private void configureSwagger() {
        this.register(ApiListingResource.class);
        this.register(SwaggerSerializers.class);

        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setTitle("Matrix REST API");
        beanConfig.setDescription("Make Matrix Great Again");
        beanConfig.setVersion("1.0");
        beanConfig.setBasePath("/api/v1");
        beanConfig.setResourcePackage("ua.softgroup.matrix.server.supervisor.producer");
        beanConfig.setScan(true);
    }

    @Bean
    public FilterRegistrationBean corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
        bean.setOrder(0);
        return bean;
    }

    /**
     * Route all errors towards a stub for hiding Tomcat error pages
     */
    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer() {
        return container -> container.addErrorPages(new ErrorPage("/"));
    }

}
