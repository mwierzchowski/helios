package com.github.mwierzchowski.helios.core.timers

import com.github.mwierzchowski.helios.DatabaseSpec
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import spock.lang.Specification
import spock.lang.Subject

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.PersistenceException

@DatabaseSpec
class TimerRepositorySpec extends Specification {
    @Subject
    @Autowired
    TimerRepository timerRepository

    @PersistenceContext
    EntityManager entityManager

    @Sql("/data/timer-data.sql")
    def "Repository finds all available timers"() {
        when:
        def timers = timerRepository.findAll()
        then:
        timers.size() == 2
    }

    @Sql("/data/timer-data.sql")
    def "Repository finds timer by id"() {
        when:
        def optionalTimer = timerRepository.findById(1)
        then:
        optionalTimer.isPresent()
        optionalTimer.get().id == 1
        optionalTimer.get().description == 'test timer 1'
    }

    @Sql("/data/timer-data.sql")
    def "Repository finds timer by description"() {
        when:
        def optionalTimer = timerRepository.findByDescription("test timer 2")
        then:
        optionalTimer.isPresent()
        optionalTimer.get().id == 2
        optionalTimer.get().description == 'test timer 2'
    }

    @Sql("/data/timer-data.sql")
    def "Repository saves timer"() {
        given:
        def timer = Timer.builder().description("test timer 3").build()
        when:
        timerRepository.save(timer)
        entityManager.flush()
        then:
        noExceptionThrown()
    }

    @Sql("/data/timer-data.sql")
    def "Repository deletes timer"() {
        given:
        def timer = timerRepository.findById(1)
        when:
        timerRepository.delete(timer.get())
        entityManager.flush()
        then:
        noExceptionThrown()
    }

    def "Repository does not find timers"() {
        when:
        def timers = timerRepository.findAll()
        then:
        timers.size() == 0
    }

    def "Repository does not find timer by id"() {
        when:
        def optionalTimer = timerRepository.findById(1)
        then:
        !optionalTimer.isPresent()
    }

    def "Repository does not find timer by description"() {
        when:
        def optionalTimer = timerRepository.findByDescription("test timer 3")
        then:
        !optionalTimer.isPresent()
    }

    @Sql("/data/timer-data.sql")
    def "Repository does not save timer when description is already used"() {
        given:
        def timer = Timer.builder().description("test timer 2").build()
        when:
        timerRepository.save(timer)
        entityManager.flush()
        then:
        thrown PersistenceException
    }

    @Sql("/data/timer-data.sql")
    def "Repository does not delete timer when it does not exist"() {
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
