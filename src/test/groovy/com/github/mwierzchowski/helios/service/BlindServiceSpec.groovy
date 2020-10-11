package com.github.mwierzchowski.helios.service

import com.github.mwierzchowski.helios.core.blinds.Blind
import com.github.mwierzchowski.helios.core.blinds.BlindDriver
import com.github.mwierzchowski.helios.core.blinds.BlindDriverRegistry
import com.github.mwierzchowski.helios.core.blinds.BlindRepository
import com.github.mwierzchowski.helios.core.commons.NotFoundException
import spock.lang.Specification
import spock.lang.Subject

import static com.github.mwierzchowski.helios.core.blinds.Blind.Status.STILL
import static java.util.stream.Collectors.toList
import static org.apache.commons.lang3.Range.between

class BlindServiceSpec extends Specification {
    BlindDriver driver = Mock()
    BlindDriverRegistry registry = Mock()
    BlindRepository repository = Mock()

    def setup() {
        registry.driverFor(_ as Blind) >> driver
    }

    @Subject
    BlindService service = new BlindService(registry, repository)

    def "Should return list of blinds"() {
        given:
        def blind1 = blindOf(1, 25)
        def blind2 = blindOf(2, 50)
        repository.findAll() >> [blind1, blind2]
        when:
        def response = service.getBlinds()
        then:
        response.size() == 2
        response.stream()
                .map {it.id}
                .collect(toList())
                .containsAll(blind1.id, blind2.id)
    }

    def "Should move the blind to new position"() {
        given:
        def blindId = 1
        def startPosition = 0
        def stopPosition = 50
        def blind = blindOf(blindId, startPosition)
        repository.findById(blindId) >> Optional.of(blind)
        driver.move(blindId, startPosition, stopPosition) >> 1000
        when:
        def response = service.move(blindId, stopPosition)
        then:
        response.start == startPosition
        response.stop == stopPosition
        response.time > 0
    }

    def "Should not move the blind if stop position is same as start"() {
        given:
        def blindId = 1
        def startPosition = 50
        def stopPosition = 50
        repository.findById(blindId) >> Optional.of(blindOf(blindId, startPosition))
        when:
        def response = service.move(blindId, stopPosition)
        then:
        response.start == startPosition
        response.stop == stopPosition
        response.time == 0
    }

    def "Should throw exception if blind to move does not exist"() {
        given:
        def blindId = 1
        repository.findById(blindId) >> Optional.empty()
        when:
        service.move(blindId, 0)
        then:
        def ex = thrown NotFoundException
        ex.id == blindId
    }

    def "Should change stop position if moved blind is already moving"() {
        given:
        def blindId = 1
        def startPosition = 0
        def stopPosition1 = 100
        def stopPosition2 = 50
        repository.findById(blindId) >> Optional.of(blindOf(blindId, startPosition))
        driver.move(blindId, startPosition, stopPosition1) >> 18000
        driver.stop(blindId) >> 25
        driver.move(blindId, 25, stopPosition2) >> 14000
        def response1 = service.move(blindId, stopPosition1)
        when:
        sleep(1000)
        def response2 = service.move(blindId, stopPosition2)
        then:
        between(startPosition, stopPosition1).fit(response2.start)
        response2.stop == stopPosition2
        response2.time != response1.time
    }

    def "Should not change stop position if moved blind is already moving to that position"() {
        given:
        def blindId = 1
        def startPosition = 0
        def stopPosition = 100
        repository.findById(blindId) >> Optional.of(blindOf(blindId, startPosition))
        driver.move(blindId, startPosition, stopPosition) >> 18000
        def response1 = service.move(blindId, stopPosition)
        when:
        def delay = 100
        sleep(delay)
        def response2 = service.move(blindId, stopPosition)
        then:
        response1.start == response2.start
        response1.stop == response2.stop
        response1.time >= response2.time + delay
    }

    def "Should stop moving blind"() {
        given:
        def blindId = 1
        def startPosition = 0
        def stopPosition = 100
        repository.findById(blindId) >> Optional.of(blindOf(blindId, startPosition))
        driver.move(blindId, startPosition, stopPosition) >> 18000
        driver.stop(blindId) >> 25
        service.move(blindId, stopPosition)
        when:
        sleep(100)
        def response = service.stop(blindId)
        then:
        response.id == blindId
        response.status == STILL.toString()
        between(startPosition, stopPosition).fit(response.position)
    }

    def "Should do nothing if stopped blind is still"() {
        given:
        def blindId = 1
        def position = 0
        repository.findById(blindId) >> Optional.of(blindOf(blindId, position))
        when:
        def response = service.stop(blindId)
        then:
        response.id == blindId
        response.position == position
        response.status == STILL.toString()
    }

    def blindOf(id, position) {
        new Blind().tap {
            it.id = id
            it.description = "Blind ${id}"
            it.status = STILL
            it.position = position
        }
    }
}
