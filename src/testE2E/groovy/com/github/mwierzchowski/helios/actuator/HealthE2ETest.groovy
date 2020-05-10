package com.github.mwierzchowski.helios.actuator

import org.junit.Test

import static io.restassured.RestAssured.when
import static org.hamcrest.Matchers.*

class HealthE2ETest {
    def healthUrl = "/actuator/health"

    @Test
    void "General health status should be UP"() {
        when().get(healthUrl).then()
                .statusCode(200)
                .body("status", equalTo("UP"))
    }

    @Test
    void "OWM health status should be UP"() {
        when().get(healthUrl).then()
                .body("components.owm.status", is("UP"))
                .body("components.owm.details.failureRate", is(0.0f))
                .body("components.owm.details.lastSuccess.response.main.temp", notNullValue())
    }

    @Test
    void "Sun API health status should be UP"() {
        when().get(healthUrl).then()
                .body("components.sunApi.status", is("UP"))
                .body("components.sunApi.details.failureRate", is(0.0f))
                .body("components.sunApi.details.lastSuccess.response.status", is("OK"))
    }

    @Test
    void "DB health status should be UP"() {
        when().get(healthUrl).then()
                .body("components.db.status", is("UP"))
                .body("components.db.details.database", is("PostgreSQL"))
    }

    @Test
    void "Mail health status should be UP"() {
        when().get(healthUrl).then()
                .body("components.mail.status", is("UP"))
    }
}
