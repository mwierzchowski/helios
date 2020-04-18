package com.github.mwierzchowski.helios.service

import com.github.mwierzchowski.helios.core.timers.*
import com.github.mwierzchowski.helios.service.dto.TimerDto
import com.github.mwierzchowski.helios.service.dto.TimerScheduleDto
import com.github.mwierzchowski.helios.service.mapper.TimerServiceMapper
import org.mapstruct.factory.Mappers
import org.springframework.context.ApplicationEventPublisher
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalTime

import static java.time.DayOfWeek.*
import static java.util.Collections.emptyList
import static java.util.Optional.empty
import static java.util.Optional.of as optional

class TimerServiceSpec extends Specification {
    TimerServiceMapper mapper = Mappers.getMapper(TimerServiceMapper)
    TimerRepository timerRepository = Mock()
    TimerAlertStarter alertStarter = Mock()
    ApplicationEventPublisher eventPublisher = Mock()

    @Subject
    TimerService timerService = new TimerService(mapper, timerRepository, alertStarter, eventPublisher)

    def "Should return list of timers"() {
        given:
        def timer1 = timerOf(1)
        def timer2 = timerOf(2, false)
        timerRepository.findAll() >> [timer1, timer2]
        when:
        def timerDtoList = timerService.getTimers()
        then:
        timerDtoList.size() == 2
        with (timerDtoList[0]) {
            it.id > 0
            it.description != null
            it.scheduled
        }
        with (timerDtoList[1]) {
            it.id > 0
            it.description != null
            !it.scheduled
        }
    }

    def "Should return empty list of timers if timers do not exist"() {
        given:
        timerRepository.findAll() >> emptyList()
        expect:
        timerService.getTimers().size() == 0
    }

    def "Should add timer if it does not exist"() {
        given:
        def timerDto = timerDtoOf(1, "new timer")
        timerRepository.findByDescription(timerDto.description) >> empty()
        when:
        timerService.addTimer(timerDto)
        then:
        1 * timerRepository.save({
            verifyAll(it, Timer) {
                id == null
                description == timerDto.description
            }
        })
    }

    def "Should not add timer if it exists"() {
        given:
        def timer = timerOf()
        timerRepository.findByDescription(timer.description) >> optional(timer)
        when:
        timerService.addTimer(timerDtoOf(timer.description))
        then:
        0 * timerRepository.save(_ as Timer)
    }

    def "Should remove timer if it exists"() {
        given:
        def timerId = 1
        timerRepository.findById(timerId) >> optional(timerOf(timerId))
        when:
        timerService.removeTimer(timerId)
        then:
        1 * timerRepository.delete(_ as Timer)
        1 * eventPublisher.publishEvent(_ as TimerRemovedEvent)
    }

    def "Should not remove timer if it does not exist"() {
        given:
        def timerId = 1
        timerRepository.findById(timerId) >> empty()
        when:
        timerService.removeTimer(timerId)
        then:
        0 * timerRepository.delete(_ as Timer)
        0 * eventPublisher.publishEvent(_ as TimerRemovedEvent)
    }

    def "Should change timer description if new description does not exist"() {
        given:
        def timerId = 1
        def timer = timerOf(timerId)
        def newDescription = timer.description + " unique"
        timerRepository.findById(timerId) >> optional(timer)
        timerRepository.findByDescription(newDescription) >> empty()
        when:
        timerService.changeTimerDescription(timerId, newDescription)
        then:
        1 * timerRepository.save({
            verifyAll(it, Timer) {
                id == timerId
                description == newDescription
            }
        })
    }

    def "Should not change timer description if description is the same as previous"() {
        given:
        def timerId = 1
        def timer = timerOf(timerId)
        def newDescription = timer.description
        timerRepository.findById(timerId) >> optional(timer)
        when:
        timerService.changeTimerDescription(timerId, newDescription)
        then:
        0 * timerRepository.save(_ as Timer)
    }

    def "Should throw exception on timer description change if new description exists"() {
        given:
        def timerId = 1
        def timer1 = timerOf(timerId)
        def timer2 = timerOf(timerId + 1)
        def newDescription = timer2.description
        timerRepository.findById(timerId) >> optional(timer1)
        timerRepository.findByDescription(newDescription) >> optional(timer2)
        when:
        timerService.changeTimerDescription(timerId, newDescription)
        then:
        thrown IllegalArgumentException
    }

    def "Should throw exception on timer description change if timer does not exist"() {
        given:
        def timerId = 1
        timerRepository.findById(timerId) >> empty()
        when:
        timerService.changeTimerDescription(timerId, "some description")
        then:
        thrown NoSuchElementException
    }

    def "Should return timer schedule list"() {
        given:
        def timerId = 1
        timerRepository.findById(timerId) >> optional(timerOf(timerId))
        when:
        def schedules = timerService.getSchedules(timerId)
        then:
        schedules.size() == 2
        with (schedules[0]) {
            LocalTime.parse(it.time) == LocalTime.of(6, 30)
            it.days.contains MONDAY.toString()
        }
        with (schedules[1]) {
            LocalTime.parse(it.time) == LocalTime.of(8, 0)
            it.days.contains SATURDAY.toString()
        }
    }

    def "Should return empty timer schedule list if schedules do not exist"() {
        given:
        def timerId = 1
        timerRepository.findById(timerId) >> optional(timerOf(timerId, false))
        expect:
        timerService.getSchedules(timerId).size() == 0
    }

    def "Should throw exception on timer schedule list return if timer does not exist"() {
        given:
        def timerId = 1
        timerRepository.findById(timerId) >> empty()
        when:
        timerService.getSchedules(timerId)
        then:
        thrown NoSuchElementException
    }

    def "Should add timer schedule if schedules do not exist"() {
        given:
        def timerId = 1
        def timer = timerOf(timerId, false)
        def scheduleDto = timerScheduleDtoOf("06:30", ["MONDAY"])
        timerRepository.findById(timerId) >> optional(timer)
        when:
        timerService.addSchedule(timerId, scheduleDto)
        then:
        1 * timerRepository.save({
            verifyAll(it, Timer) {
                id == timerId
                schedules[0].id == null
                schedules[0].timer == timer
                schedules[0].time == LocalTime.parse(scheduleDto.time)
                schedules[0].days.contains(valueOf(scheduleDto.days[0]))
            }
        })
        1 * alertStarter.startAlertFor({
            verifyAll(it, Timer) {
                id == timerId
            }
        })
    }

    def "Should add timer schedule if schedule for given day does not exist"() {
        given:
        def timerId = 1
        def scheduleDto = timerScheduleDtoOf("10:00", ["SUNDAY"])
        timerRepository.findById(timerId) >> optional(timerOf(timerId))
        when:
        timerService.addSchedule(timerId, scheduleDto)
        then:
        1 * timerRepository.save({
            verifyAll(it, Timer) {
                id == timerId
                schedules[2].id == null
                schedules[2].time == LocalTime.parse(scheduleDto.time)
                schedules[2].days.contains(valueOf(scheduleDto.days[0]))
            }
        })
        1 * alertStarter.startAlertFor({
            verifyAll(it, Timer) {
                id == timerId
            }
        })
    }

    def "Should not add timer schedule if schedule already exist"() {
        given:
        def timerId = 1
        def scheduleDto = timerScheduleDtoOf("08:00", ["SATURDAY"])
        timerRepository.findById(timerId) >> optional(timerOf(timerId))
        when:
        timerService.addSchedule(timerId, scheduleDto)
        then:
        0 * timerRepository.save(_ as Timer)
    }

    def "Should throw exception on adding schedule if timer does not exist"() {
        given:
        def timerId = 1
        timerRepository.findById(timerId) >> empty()
        when:
        timerService.addSchedule(timerId, timerScheduleDtoOf("6:30", ["MONDAY"]))
        then:
        thrown NoSuchElementException
    }

    def "Should throw exception on adding schedule if it conflicts with others"() {
        given:
        def timerId = 1
        def scheduleDto = timerScheduleDtoOf("10:00", ["MONDAY", "TUESDAY"])
        timerRepository.findById(timerId) >> optional(timerOf(timerId))
        when:
        timerService.addSchedule(timerId, scheduleDto)
        then:
        thrown IllegalArgumentException
    }

    def "Should remove timer schedule if it exists"() {
        given:
        def timerId = 1
        def scheduleId = 1
        timerRepository.findById(timerId) >> optional(timerOf(timerId))
        when:
        timerService.removeSchedule(timerId, scheduleId)
        then:
        1 * timerRepository.save({
            verifyAll(it, Timer) {
                id == timerId
                schedules.size() == 1
                schedules[0].id == 2
            }
        })
    }

    def "Should not remove remove timer schedule if it does not exist"() {
        given:
        def timerId = 1
        def scheduleId = 1
        timerRepository.findById(timerId) >> optional(timerOf(timerId, false))
        when:
        timerService.removeSchedule(timerId, scheduleId)
        then:
        0 * timerRepository.save(_ as Timer)
    }

    def "Should throw exception on removing schedule if timer does not exist"() {
        given:
        def timerId = 1
        def scheduleId = 1
        timerRepository.findById(timerId) >> empty()
        when:
        timerService.removeSchedule(timerId, scheduleId)
        then:
        thrown NoSuchElementException
    }

    /** Helper methods ************************************************************************************************/

    def timerOf(id = 1, schedule = true) {
        new Timer().tap {
            it.id = id
            it.description = "test timer ${id}"
            if (schedule) {
                it.add new TimerSchedule().tap {
                    it.id = 1
                    it.time = LocalTime.of(6, 30)
                    it.days = [MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY]
                }
                it.add new TimerSchedule().tap {
                    it.id = 2
                    it.time = LocalTime.of(8, 0)
                    it.days = [SATURDAY]
                }
            }
        }
    }

    def timerDtoOf(id = null, description) {
        new TimerDto().tap {
            it.id = id
            it.description = description
        }
    }

    def timerScheduleDtoOf(time, days) {
        new TimerScheduleDto().tap {
            it.time = time
            it.days = days
        }
    }
}
