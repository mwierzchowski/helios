package com.github.mwierzchowski.helios;

import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;
import javax.ws.rs.ApplicationPath;

/**
 * Application boot configuration.
 * @author Marcin Wierzchowski
 */
@EnableCaching
@EnableScheduling
@EnableJpaAuditing
@EnableJpaRepositories
@EnableTransactionManagement
@SpringBootApplication
@ApplicationPath("/api")
public class HeliosApplication extends ResourceConfig {
	/**
	 * Start application.
	 * @param args application arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(HeliosApplication.class, args);
	}

	/**
	 * Initialization of application endpoints.
	 */
	@PostConstruct
    public void initializeEndpoints() {
        packages("com.github.mwierzchowski.helios.service");
    }
}
