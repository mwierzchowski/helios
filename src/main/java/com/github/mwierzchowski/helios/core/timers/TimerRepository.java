package com.github.mwierzchowski.helios.core.timers;

import org.springframework.data.repository.RepositoryDefinition;

import java.util.List;
import java.util.Optional;

/**
 * Interface representing repository of timers.
 * @author Marcin Wierzchowski
 */
@RepositoryDefinition(domainClass = Timer.class, idClass = Integer.class)
public interface TimerRepository {
    /**
     * Finds all the timers.
     * @return list of timers
     */
    List<Timer> findAll();

    /**
     * Finds the timer by its id.
     * @param id id of the timer
     * @return optional timer or empty
     */
    Optional<Timer> findById(Integer id);

    /**
     * Dinds the timer by its unique description.
     * @param description unique description
     * @return optional timer or empty
     */
    Optional<Timer> findByDescription(String description);

    /**
     * Adds the timer to the repository
     * @param timer timer
     */
    void save(Timer timer);

    /**
     * Removes the timer from the repository
     * @param timer timer
     */
    void delete(Timer timer);
}
