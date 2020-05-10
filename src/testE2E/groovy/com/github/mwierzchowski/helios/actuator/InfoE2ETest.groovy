package com.github.mwierzchowski.helios.actuator

import org.junit.Test

import static io.restassured.RestAssured.when
import static org.hamcrest.Matchers.is
import static org.hamcrest.Matchers.notNullValue

class InfoE2ETest {
    def infoUrl = "/actuator/info"

    @Test
    void "Should return application info"() {
        when().get(infoUrl).then()
                .statusCode(200)
                .body("app.name", notNullValue())
    }

    @Test
    void "Should return location info"() {
        when().get(infoUrl).then()
                .body("location.latitude", notNullValue())
                .body("location.longitude", notNullValue())
    }

    @Test
    void "Should return sunEphemeris info"() {
        when().get(infoUrl).then()
                .body("sunEphemeris.approximated", is(false))
    }

    @Test
    void "Should return weather info"() {
        when().get(infoUrl).then()
                .body("weather.provided", is(true))
                .body("weather.temperature.value", notNullValue())
    }
}
