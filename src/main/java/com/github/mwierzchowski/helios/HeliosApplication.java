package com.github.mwierzchowski.helios;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main application config.
 * @author Marcin Wierzchowski
 */
@EnableCaching
@EnableScheduling
@EnableJpaRepositories
@EnableTransactionManagement
@SpringBootApplication
public class HeliosApplication {
	/**
	 * Main application method.
	 * @param args app arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(HeliosApplication.class, args);
	}
}
