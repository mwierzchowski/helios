package com.github.mwierzchowski.helios.core.timers;

import org.springframework.data.repository.RepositoryDefinition;

import java.util.List;
import java.util.Optional;

@RepositoryDefinition(domainClass = Timer.class, idClass = Integer.class)
public interface TimerRepository {
    List<Timer> findAll();
    Optional<Timer> findById(Integer id);
    Optional<Timer> findByDescription(String description);
    void save(Timer timer);
    void delete(Timer timer);
}
