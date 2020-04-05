package com.github.mwierzchowski.helios.service

import com.github.mwierzchowski.helios.core.timers.Timer
import com.github.mwierzchowski.helios.core.timers.TimerRemovedEvent
import com.github.mwierzchowski.helios.core.timers.TimerRepository
import com.github.mwierzchowski.helios.service.dto.TimerDto
import com.github.mwierzchowski.helios.service.mappers.TimerServiceMapper
import org.mapstruct.factory.Mappers
import org.springframework.context.ApplicationEventPublisher
import spock.lang.Specification
import spock.lang.Subject

class TimerServiceSpec extends Specification {
    TimerServiceMapper mapper = Mappers.getMapper(TimerServiceMapper)
    TimerRepository timerRepository = Mock()
    ApplicationEventPublisher eventPublisher = Mock()

    @Subject
    TimerService timerService = new TimerService(mapper, timerRepository, eventPublisher)

    def "Service provides list of all timers"() {
        given:
        def timer = new Timer(1, "test timer")
        timerRepository.findAll() >> [timer]
        when:
        def timerDtoList = timerService.getTimers()
        then:
        timerDtoList.size() == 1
        timerDtoList[0].id == timer.id
    }

    def "Service adds new timer"() {
        given:
        def dto = new TimerDto(1, "new timer")
        timerRepository.findByDescription(dto.description) >> Optional.empty()
        when:
        timerService.addTimer(dto)
        then:
        1 * timerRepository.save({
            verifyAll(it, Timer) {
                id == null
                description == dto.description
            }
        })
    }

    def "Service removes existing timer and sends notification"() {
        given:
        def timerId = 1
        timerRepository.findById(timerId) >> Optional.of(new Timer(timerId, "test timer"))
        when:
        timerService.removeTimer(timerId)
        then:
        1 * timerRepository.delete(_ as Timer)
        1 * eventPublisher.publishEvent(_ as TimerRemovedEvent)
    }

    def "Service does nothing when timer to be added already exists"() {
        given:
        def timer = new Timer(1, "test timer")
        timerRepository.findByDescription(timer.description) >> Optional.of(timer)
        when:
        timerService.addTimer(new TimerDto(timer.id, timer.description))
        then:
        0 * timerRepository.save(_ as Timer)
    }

    def "Service does nothing when timer to be removed does not exist"() {
        given:
        def timerId = 1
        timerRepository.findById(timerId) >> Optional.empty()
        when:
        timerService.removeTimer(timerId)
        then:
        0 * timerRepository.delete(_ as Timer)
        0 * eventPublisher.publishEvent(_ as TimerRemovedEvent)
    }
}
