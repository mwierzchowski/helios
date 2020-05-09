package com.github.mwierzchowski.helios

import org.junit.Test

import static io.restassured.RestAssured.when
import static org.hamcrest.Matchers.equalTo

class HealthTest {
    def healthUrl = "/actuator/health"
    @Test
    void "General health status should be UP"() {
        when().get(healthUrl)
        .then().statusCode(200)
                .body("status", equalTo("UP"))
    }

    @Test
    void "OWM health status should be UP"() {
        when().get(healthUrl)
        .then().body("components.owm.status", equalTo("UP"))
    }

    @Test
    void "Sun API health status should be UP"() {
        when().get(healthUrl)
        .then().body("components.sunApi.status", equalTo("UP"))
    }
}
