package com.github.mwierzchowski.helios

import com.github.tomakehurst.wiremock.WireMockServer
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.core.annotation.AliasFor
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestContext
import org.springframework.test.context.TestExecutionListener
import org.springframework.test.context.TestExecutionListeners

import java.lang.annotation.Inherited
import java.lang.annotation.Retention
import java.lang.annotation.Target

import static java.lang.annotation.ElementType.TYPE
import static java.lang.annotation.RetentionPolicy.RUNTIME
import static org.springframework.test.context.TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS

/**
 * Meta-annotation for integration tests that require Spring context.
 * @author Marcin Wierzchowski
 */
@Inherited
@Target(TYPE)
@Retention(RUNTIME)
@SpringBootTest
@ActiveProfiles
@AutoConfigureWireMock(port = 0)
@TestExecutionListeners(mergeMode = MERGE_WITH_DEFAULTS, listeners = WireMockResetListener)
@interface IntegrationSpec {
    @AliasFor(annotation = SpringBootTest, attribute = "properties") String[] properties() default []
    @AliasFor(annotation = ActiveProfiles, attribute = "profiles") String[] profiles() default ["test"]

    static class WireMockResetListener implements TestExecutionListener {
        void beforeTestMethod(TestContext testContext) throws Exception {
            testContext.applicationContext.getBean(WireMockServer).resetAll()
        }
    }
}