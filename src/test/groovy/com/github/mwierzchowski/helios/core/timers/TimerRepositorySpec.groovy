package com.github.mwierzchowski.helios.core.timers

import com.github.mwierzchowski.helios.DatabaseSpec
import org.apache.commons.lang3.exception.ExceptionUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import spock.lang.Specification
import spock.lang.Subject

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.PersistenceException
import java.time.LocalTime

import static java.time.DayOfWeek.*

@DatabaseSpec
class TimerRepositorySpec extends Specification {
    @Subject
    @Autowired
    TimerRepository timerRepository

    @PersistenceContext
    EntityManager entityManager

    @Sql("/data/timer-data.sql")
    def "Should find all timers"() {
        when:
        def timers = timerRepository.findAll()
        then:
        timers.size() == 2
    }

    def "Should not find timers if they do not exist"() {
        when:
        def timers = timerRepository.findAll()
        then:
        timers.size() == 0
    }

    @Sql("/data/timer-data.sql")
    def "Should find timer by id"() {
        when:
        def foundTimer = timerRepository.findById(1)
        then:
        with (foundTimer.get()) {
            it.id == 1
            it.description == 'test timer 1'
            it.created != null
            it.updated != null
            it.version >= 0
            it.schedules.size() == 2
            with (it.getSchedule(1).get()) {
                it.id == 1
                it.days.contains(MONDAY)
                it.time == LocalTime.of(6, 30)
                it.enabled
                it.created != null
                it.updated != null
                it.version >= 0
            }
        }
    }

    def "Should not find timer by id if it does not exist"() {
        when:
        def foundTimer = timerRepository.findById(999)
        then:
        foundTimer.isEmpty()
    }

    @Sql("/data/timer-data.sql")
    def "Should find timer by description"() {
        given:
        def description = "test timer 2"
        when:
        def foundTimer = timerRepository.findByDescription(description)
        then:
        foundTimer.isPresent()
        foundTimer.get().id == 2
        foundTimer.get().description == description
    }

    def "Should not find timer by description if it does not exist"() {
        when:
        def foundTimer = timerRepository.findByDescription("not existing timer")
        then:
        foundTimer.isEmpty()
    }

    def "Should save timer and its schedules"() {
        given:
        def timer = Timer.builder()
                .description("new timer")
                .build()
        timer.add(TimerSchedule.builder()
                .days([MONDAY, TUESDAY, WEDNESDAY] as Set)
                .time(LocalTime.of(8, 0))
                .enabled(true)
                .build())
        when:
        timerRepository.save(timer)
        entityManager.flush()
        then:
        with (timerRepository.findAll()[0]) {
            it.id >= 0
            it.created != null
            it.updated != null
            it.version >= 0
            it.schedules.size() == 1
            with (it.schedules[0]) {
                it.id >= 0
                it.created != null
                it.updated != null
                it.version >= 0
                it.time != null
                it.days.containsAll([MONDAY, TUESDAY, WEDNESDAY] as Set)
                it.enabled
            }
        }
    }

    def "Should save timer but not schedules if they do not exist"() {
        given:
        def timer = Timer.builder()
                .description("new timer")
                .build()
        when:
        timerRepository.save(timer)
        entityManager.flush()
        then:
        with (timerRepository.findAll()[0]) {
            it.id >= 0
            it.created != null
            it.updated != null
            it.version >= 0
            it.schedules.size() == 0
        }
    }

    @Sql("/data/timer-data.sql")
    def "Should throw exception on save if description is used"() {
        given:
        def timer = Timer.builder().description("test timer 2").build()
        when:
        timerRepository.save(timer)
        entityManager.flush()
        then:
        def pex = thrown(PersistenceException)
        def pexMessage = ExceptionUtils.getRootCause(pex).getMessage()
        pexMessage.contains('unique constraint') && pexMessage.contains('description')
    }

    @Sql("/data/timer-data.sql")
    def "Should delete timer"() {
        given:
        def timerId = 1
        def timer = timerRepository.findById(timerId)
        when:
        timerRepository.delete(timer.get())
        entityManager.flush()
        then:
        timerRepository.findById(timerId).isEmpty()
    }

    @Sql("/data/timer-data.sql")
    def "Should not delete timer if it does not exist"() {
        given:
        def timer = timerRepository.findById(1).get()
        timerRepository.delete(timer)
        when:
        timerRepository.delete(timer)
        entityManager.flush()
        then:
        noExceptionThrown()
    }
}
