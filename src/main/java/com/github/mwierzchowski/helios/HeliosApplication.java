package com.github.mwierzchowski.helios;

import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;
import javax.ws.rs.ApplicationPath;

/**
 * Application boot configuration.
 * @author Marcin Wierzchowski
 */
@EnableAsync
@EnableCaching
@EnableScheduling
@EnableJpaAuditing
@EnableJpaRepositories
@EnableTransactionManagement
@SpringBootApplication
@ApplicationPath("/api")
@OpenAPIDefinition(info = @Info(version = "1.0", title = "Helios", description = "Sun blinds controller",
		contact = @Contact(name = "Marcin Wierzchowski", url = "https://github.com/mwierzchowski/helios"),
		license = @License(name = "MIT License", url = "https://opensource.org/licenses/MIT")))
public class HeliosApplication extends ResourceConfig {
	/**
	 * Initialization of application endpoints.
	 */
	@PostConstruct
    public void initializeEndpoints() {
		packages("com.github.mwierzchowski.helios.service");
		register(OpenApiResource.class);
    }

	/**
	 * Start application.
	 * @param args application arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(HeliosApplication.class, args);
	}
}
