package com.github.mwierzchowski.helios.core.timers;

import org.springframework.data.repository.RepositoryDefinition;

import java.util.Optional;

@RepositoryDefinition(domainClass = Timer.class, idClass = Long.class)
public interface TimerRepository {
    void save(Timer timer);
    Optional<Timer> findByName(String name);
}
