package com.github.mwierzchowski.helios

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.annotation.AliasFor
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

import java.lang.annotation.Inherited
import java.lang.annotation.Retention
import java.lang.annotation.Target

import static java.lang.annotation.ElementType.TYPE
import static java.lang.annotation.RetentionPolicy.RUNTIME

/**
 * Meta-annotation for integration tests that require Spring context.
 * @author Marcin Wierzchowski
 */
@Inherited
@Target(TYPE)
@Retention(RUNTIME)
@SpringBootTest
@ActiveProfiles
@Transactional
@EnableWireMock
@interface IntegrationSpec {
    @AliasFor(annotation = SpringBootTest, attribute = "properties") String[] properties() default []
    @AliasFor(annotation = ActiveProfiles, attribute = "profiles") String[] profiles() default ["test"]
}