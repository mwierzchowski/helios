package com.github.mwierzchowski.helios.core.blinds;

import java.util.List;
import java.util.Optional;

public interface BlindRepository {
    List<Blind> findAll();
    Optional<Blind> findById(Integer id);
}
