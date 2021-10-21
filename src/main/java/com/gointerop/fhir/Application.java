package com.gointerop.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.fhirpath.IFhirPath;
import ca.uhn.fhir.rest.server.RestfulServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

import com.gointerop.fhir.hapi.HapiRestfulServer;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import java.util.TimeZone;

@SpringBootApplication
public class Application extends SpringBootServletInitializer {

    @Autowired
    private HapiRestfulServer jpaRestfulServer;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    @Bean
    public ServletRegistrationBean<RestfulServer> servletRegistrationBean() {
        ServletRegistrationBean<RestfulServer> servletRegistrationBean = new ServletRegistrationBean<>();
        servletRegistrationBean.setServlet(jpaRestfulServer);
        servletRegistrationBean.addUrlMappings("/fhir/*");
        servletRegistrationBean.setLoadOnStartup(1);

        return servletRegistrationBean;
    }

    @Bean
    public FhirContext fhirContext() {
        return FhirContext.forR4();
    }
    
    @Bean
    public IFhirPath iFhirPath() {
        return fhirContext().newFhirPath();
    }
    
    @Bean
    public DataSource dataSource() {
    	DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("org.sqlite.JDBC");
        dataSourceBuilder.url("jdbc:sqlite:database.sqlite");
        return dataSourceBuilder.build();
    }
}
