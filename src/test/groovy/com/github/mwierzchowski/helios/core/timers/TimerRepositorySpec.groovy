package com.github.mwierzchowski.helios.core.timers

import com.github.mwierzchowski.helios.IntegrationSpec
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification
import spock.lang.Subject

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@IntegrationSpec
class TimerRepositorySpec extends Specification {
    @Subject
    @Autowired
    TimerRepository timerRepository

    @PersistenceContext
    EntityManager entityManager;

    def "Spring Data magic works"() {
        given:
        def timer = new Timer(1, "Marcin")
        when:
        timerRepository.save(timer)
        entityManager.flush()
        then:
        timerRepository.findByName("Marcin").get().id == 1
        timerRepository.findByName("Dorota").isEmpty()
    }

    def "Regular stuff works"() {
        given:
        def timer = new Timer(1, "Marcin")
        when:
        entityManager.persist(timer)
        entityManager.flush()
        then:
        noExceptionThrown()
    }
}
