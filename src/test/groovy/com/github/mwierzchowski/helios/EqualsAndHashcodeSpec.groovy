package com.github.mwierzchowski.helios

import com.github.mwierzchowski.helios.core.timers.Timer
import com.github.mwierzchowski.helios.core.timers.TimerAlertEvent
import com.github.mwierzchowski.helios.core.timers.TimerRemovedEvent
import com.github.mwierzchowski.helios.core.timers.TimerSchedule
import nl.jqno.equalsverifier.EqualsVerifier
import org.mapstruct.Mapper
import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import org.reflections.util.ConfigurationBuilder
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import spock.lang.Specification

import javax.persistence.Converter

import static nl.jqno.equalsverifier.Warning.NONFINAL_FIELDS
import static nl.jqno.equalsverifier.Warning.STRICT_INHERITANCE

class EqualsAndHashcodeSpec extends Specification {
    def excludeClasses = [
            TimerAlertEvent,
            TimerSchedule,
            TimerRemovedEvent,
            Timer
    ]

    def "Classes should implement equals and hashcode methods"() {
        given:
        def configBuilder = new ConfigurationBuilder()
                .setScanners(new SubTypesScanner(false))
                .addUrls(HeliosApplication.location)
        when:
        new Reflections(configBuilder).getSubTypesOf(Object).stream()
                .filter(this.&isTestable)
                .forEach {
                    EqualsVerifier.forClass(it)
                        .suppress(STRICT_INHERITANCE)
                        .suppress(NONFINAL_FIELDS)
                        .verify()
                }
        then:
        noExceptionThrown()
    }

    boolean isTestable(Class<?> clazz) {
        clazz.packageName.startsWith("com.github.mwierzchowski.helios") &&
        !excludeClasses.contains(clazz) &&
        !clazz.isInterface() &&
        !clazz.isEnum() &&
        !clazz.getName().contains("\$") &&
        !clazz.isAnnotationPresent(Component) &&
        !clazz.isAnnotationPresent(Service) &&
        !clazz.isAnnotationPresent(Repository) &&
        !clazz.isAnnotationPresent(Configuration) &&
        !clazz.isAnnotationPresent(Mapper) &&
        !clazz.isAnnotationPresent(Converter)
    }
}
