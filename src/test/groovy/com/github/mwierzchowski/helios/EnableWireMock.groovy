package com.github.mwierzchowski.helios

import com.github.tomakehurst.wiremock.WireMockServer
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.cloud.contract.wiremock.WireMockConfiguration
import org.springframework.test.context.TestContext
import org.springframework.test.context.TestExecutionListener
import org.springframework.test.context.TestExecutionListeners

import java.lang.annotation.Inherited
import java.lang.annotation.Retention
import java.lang.annotation.Target

import static java.lang.annotation.ElementType.TYPE
import static java.lang.annotation.RetentionPolicy.RUNTIME
import static org.springframework.test.context.TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS

@Inherited
@Target(TYPE)
@Retention(RUNTIME)
@AutoConfigureWireMock(port = 0)
@TestExecutionListeners(mergeMode = MERGE_WITH_DEFAULTS, listeners = WireMockResetListener)
@interface EnableWireMock {
    static class WireMockResetListener implements TestExecutionListener {
        void beforeTestClass(TestContext testContext) throws Exception {
            testContext.applicationContext.getBean(WireMockConfiguration).init()
        }

        void beforeTestMethod(TestContext testContext) throws Exception {
            testContext.applicationContext.getBean(WireMockServer).resetAll()
        }
    }
}