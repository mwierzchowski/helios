package com.github.mwierzchowski.helios

import com.github.mwierzchowski.helios.core.commons.CommonConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.ComponentScan
import org.springframework.core.annotation.AliasFor
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource

import java.lang.annotation.Inherited
import java.lang.annotation.Retention
import java.lang.annotation.Target

import static java.lang.annotation.ElementType.TYPE
import static java.lang.annotation.RetentionPolicy.RUNTIME

@Inherited
@Target(TYPE)
@Retention(RUNTIME)
@SpringBootTest
@ActiveProfiles("test")
@EnableAutoConfiguration(exclude = [DataSourceAutoConfiguration, DataSourceTransactionManagerAutoConfiguration,
        TransactionAutoConfiguration, HibernateJpaAutoConfiguration, LiquibaseAutoConfiguration])
@TestPropertySource(properties = [
        "debug = false",
        "spring.main.lazy-initialization = true",
        "embedded.postgresql.enabled = false"])
@EnableCaching
@EnableWireMock
@ComponentScan(basePackageClasses = [CommonConfiguration])
@interface LiteIntegrationSpec {
    @AliasFor(annotation = SpringBootTest, attribute = "classes") Class<?>[] value() default []
}
