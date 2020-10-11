package com.github.mwierzchowski.helios.core.blinds;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;

import static com.github.mwierzchowski.helios.core.blinds.Blind.Status.MOVING;
import static com.github.mwierzchowski.helios.core.blinds.Blind.Status.STILL;
import static com.github.mwierzchowski.helios.core.blinds.BlindMovement.alreadyAt;

@Data
@Slf4j
public class Blind {
    private Integer id;
    private String description;
    private Status status;
    private Integer position;
    private LinkedList<BlindMovement> history = new LinkedList<>();

    public BlindMovement move(BlindDriver driver, Integer nextPosition) {
        if (status == MOVING) {
            var recentMove = history.getFirst();
            if (recentMove.getStop().equals(nextPosition)) {
                log.info("Blind {} is already moving to position {}", id, nextPosition);
                return recentMove.updated();
            }
            log.info("Blind {} is moving to position {}, stopping before move", id, recentMove.getStop());
            position = driver.stop(id);
            status = STILL;
        }
        if (position.equals(nextPosition)) {
            log.info("Blind {} is already at position {}", id, nextPosition);
            return alreadyAt(nextPosition);
        }
        var time = driver.move(id, position, nextPosition);
        var movement = new BlindMovement(position, nextPosition, time);
        history.addFirst(movement);
        status = MOVING;
        position = null;
        return movement;
    }

    public void stop(BlindDriver driver) {
        if (status == STILL) {
            log.warn("Blind {} is already stopped at position {}", id, position);
            return;
        }
        position = driver.stop(id);
        status = STILL;
    }

    public enum Status {
        STILL,
        MOVING,
        UNKNOWN
    }
}
