package com.github.mwierzchowski.helios;

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
 * Main application config.
 * @author Marcin Wierzchowski
 */
@EnableCaching
@EnableScheduling
@EnableJpaAuditing
@EnableJpaRepositories
@EnableTransactionManagement
@ApplicationPath("/api")
@SpringBootApplication
public class HeliosApplication extends ResourceConfig {
	@PostConstruct
    public void init() {
        packages("com.github.mwierzchowski.helios.service");
    }

	/**
	 * Main application method.
	 * @param args app arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(HeliosApplication.class, args);
	}
}
