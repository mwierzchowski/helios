package com.github.mwierzchowski.helios.service.ext

import com.github.mwierzchowski.helios.core.commons.CommonProperties
import com.github.mwierzchowski.helios.service.dto.ServiceErrorDto
import spock.lang.Specification
import spock.lang.Subject

class UnhandledExceptionMapperSpec extends Specification {
    def commonProperties = new CommonProperties()

    @Subject
    def mapper = new UnhandledExceptionMapper(commonProperties)

    def "Should return response with all details"() {
        given:
        def message = 'test message'
        def exception = new RuntimeException(message)
        when:
        def response = mapper.toResponse(exception)
        then:
        response.status == 500
        with (response.entity as ServiceErrorDto) {
            it.message == message
            it.exception == exception.class.name
            it.timestamp != null
        }
    }
}
