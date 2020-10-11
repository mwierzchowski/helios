package com.github.mwierzchowski.helios.core.blinds;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

import static java.time.Duration.between;
import static java.time.Instant.now;

@Data
@RequiredArgsConstructor
public class BlindMovement {
    private final Integer start;
    private final Integer stop;
    private final Long time;
    private final Instant created = now();

    public BlindMovement updated() {
        Long updatedTime = time - between(created, now()).toMillis();
        return new BlindMovement(start, stop, updatedTime);
    }

    public static BlindMovement alreadyAt(Integer position) {
        return new BlindMovement(position, position, 0L);
    }
}
