package com.github.mwierzchowski.helios.core.blinds;

public interface BlindDriver {
    Integer stop(Integer id);
    Long move(Integer id, Integer start, Integer stop);
}
