package com.github.mwierzchowski.helios.core.rules

import com.github.mwierzchowski.helios.core.scenes.Scene
import com.github.mwierzchowski.helios.core.scenes.SceneMaker
import spock.lang.Specification

class SceneMakerSpec extends Specification {
    def sceneMaker = new SceneMaker()

    def "Dummy test to be replaced later"() {
        given:
        def scene = new Scene()
        when:
        sceneMaker.setup(scene)
        then:
        noExceptionThrown()
    }
}
