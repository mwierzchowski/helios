package com.github.mwierzchowski.helios.service.ext

import com.github.mwierzchowski.helios.core.commons.NotFoundException
import com.github.mwierzchowski.helios.core.timers.Timer
import com.github.mwierzchowski.helios.service.dto.RequestErrorDto
import spock.lang.Specification
import spock.lang.Subject

class NotFoundExceptionMapperSpec extends Specification {
    @Subject
    def mapper = new NotFoundExceptionMapper()

    def "Should return response with all details"() {
        given:
        def clazz = Timer
        def id = 123
        when:
        def response = mapper.toResponse(new NotFoundException(clazz, id))
        then:
        response.status == 404
        with (response.entity as RequestErrorDto) {
            it.message != null
            it.object == clazz.simpleName
            it.value == id
        }
    }
}
