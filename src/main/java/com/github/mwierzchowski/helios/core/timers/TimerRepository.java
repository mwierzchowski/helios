package com.github.mwierzchowski.helios.core.timers;

import java.util.List;
import java.util.Optional;

public interface TimerRepository {
    Optional<Timer> findById(Integer id);
    Optional<Timer> findByDescription(String description);
    List<Timer> findAll();
    void add(Timer timer);
    void remove(Timer timer);
}
