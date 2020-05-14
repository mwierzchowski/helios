package com.github.mwierzchowski.helios.service.ext

import com.github.mwierzchowski.helios.service.dto.RequestErrorDto
import com.github.mwierzchowski.helios.service.dto.TimerDto
import spock.lang.Specification
import spock.lang.Subject

import javax.validation.ConstraintViolationException
import javax.validation.Validation

class ConstraintExceptionMapperSpec extends Specification {
    @Subject
    def mapper = new ConstraintExceptionMapper()
    def validator = Validation.buildDefaultValidatorFactory().getValidator()

    def "Should return response with all details"() {
        given:
        def violations = validator.validate(new TimerDto())
        def exception = new ConstraintViolationException(violations)
        when:
        def response = mapper.toResponse(exception)
        then:
        response.status == 400
        with ((response.entity as List)[0] as RequestErrorDto) {
            it.message != null
            it.object != null
            it.value == null
        }
    }
}
