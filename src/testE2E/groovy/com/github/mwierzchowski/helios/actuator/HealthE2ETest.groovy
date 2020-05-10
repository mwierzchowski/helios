package com.github.mwierzchowski.helios.actuator

import org.junit.Test

import static io.restassured.RestAssured.when
import static org.hamcrest.Matchers.is
import static org.hamcrest.Matchers.notNullValue

class HealthE2ETest {
    def healthUrl = "/actuator/health"

    @Test
    void "Should return general health status UP"() {
        when().get(healthUrl).then()
                .statusCode(200)
                .body("status", is("UP"))
    }

    @Test
    void "Should return OWM health status UP"() {
        when().get(healthUrl).then()
                .body("components.owm.status", is("UP"))
                .body("components.owm.details.failureRate", is(0.0f))
                .body("components.owm.details.lastSuccess.response.main.temp", notNullValue())
    }

    @Test
    void "Should return Sun API health status UP"() {
        when().get(healthUrl).then()
                .body("components.sunApi.status", is("UP"))
                .body("components.sunApi.details.failureRate", is(0.0f))
                .body("components.sunApi.details.lastSuccess.response.status", is("OK"))
    }

    @Test
    void "Should return DB health status UP"() {
        when().get(healthUrl).then()
                .body("components.db.status", is("UP"))
                .body("components.db.details.database", is("PostgreSQL"))
    }

    @Test
    void "Should return mail health status UP"() {
        when().get(healthUrl).then()
                .body("components.mail.status", is("UP"))
    }
}
