package com.github.mwierzchowski.helios;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application config.
 * @author Marcin Wierzchowski
 */
@EnableCaching
@EnableScheduling
@EnableConfigurationProperties(HeliosProperties.class)
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
