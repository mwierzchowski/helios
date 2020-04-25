package com.github.mwierzchowski.helios.adapter.owm;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.openweathermap.model.CurrentWeatherResponse;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.time.ZoneId.systemDefault;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;
import static java.util.stream.Collectors.toList;
import static org.springframework.boot.actuate.health.Status.DOWN;
import static org.springframework.boot.actuate.health.Status.UNKNOWN;
import static org.springframework.boot.actuate.health.Status.UP;

/**
 * Health indicator for OWM adapter.
 * @author Marcin Wierzchowski
 */
@Data
@Component
public class OwmHealthIndicator implements HealthIndicator {
    /**
     * Constant for maximum history capacity.
     */
    static final int HISTORY_MAX = 10;

    /**
     * Recent history. It is implemented as FIFO queue.
     */
    private List<RequestAttempt> recentHistory = new ArrayList<>(HISTORY_MAX);

    /**
     * Details of last successful request attempt.
     */
    private RequestAttempt lastSuccess = null;

    /**
     * Details of last failed request attempt.
     */
    private RequestAttempt lastFailure = null;

    /**
     * Counter of all successful request attempts since start of application.
     */
    private int successCounter = 0;

    /**
     * Counter of all failed request attempts since start of application.
     */
    private int failureCounter = 0;

    @Override
    public synchronized Health health() {
        return Health.status(getStatus())
                .withDetails(getDetails())
                .build();
    }

    /**
     * Registers successful call to OWM API.
     * @param currentWeatherResponse successful response
     */
    public synchronized void register(CurrentWeatherResponse currentWeatherResponse) {
        updateHistory(currentWeatherResponse);
    }

    /**
     * Registers failed call to OWM API.
     * @param throwable failure reason
     */
    public synchronized void register(Throwable throwable) {
        updateHistory(throwable);
    }

    /** Helpers *******************************************************************************************************/

    /**
     * Sum of all (successful and failed) requests.
     * @return total counter
     */
    int getAllRequestCounter() {
        return successCounter + failureCounter;
    }

    /**
     * Percentage of successful calls.
     * @return success rate
     */
    double getSuccessRate() {
        return getAllRequestCounter() == 0 ? 0 : (double) successCounter / getAllRequestCounter() * 100;
    }

    /**
     * Percentage of failed calls.
     * @return failure rate
     */
    double getFailureRate() {
        return getAllRequestCounter() == 0 ? 0 : (double) failureCounter / getAllRequestCounter() * 100;
    }

    /**
     * Updates history with given result ({@link CurrentWeatherResponse} or {@link Throwable}).
     * @param object request attempt result
     */
    private void updateHistory(Object object) {
        RequestAttempt requestAttempt = new RequestAttempt(object);
        recentHistory.add(0, requestAttempt);
        if (recentHistory.size() > HISTORY_MAX) {
            recentHistory.remove(HISTORY_MAX);
        }
        if (requestAttempt.isSuccess()) {
            successCounter += 1;
            lastSuccess = requestAttempt;
        } else {
            failureCounter += 1;
            lastFailure = requestAttempt;
        }
    }

    /**
     * Provides adapter health status.
     * @return health status
     */
    private Status getStatus() {
        if (recentHistory.isEmpty()) {
            return UNKNOWN;
        } else {
            return recentHistory.get(0).isSuccess() ? UP : DOWN;
        }
    }

    /**
     * Provides adapter health details.
     * @return health details
     */
    private Map<String,?> getDetails() {
        Map<String, Object> details = new LinkedHashMap<>();
        details.put("allRequestCounter", getAllRequestCounter());
        details.put("successRate", getSuccessRate());
        details.put("failureRate", getFailureRate());
        if (lastSuccess != null) {
            details.put("lastSuccess", lastSuccess.toDetailsMap(true));
        }
        if (lastFailure != null) {
            details.put("lastFailure", lastFailure.toDetailsMap(true));
        }
        if (!recentHistory.isEmpty()) {
            details.put("recentHistory", recentHistory.stream()
                    .map(requestAttempt -> requestAttempt.toDetailsMap(false))
                    .collect(toList())
            );
        }
        return details;
    }

    /**
     * Container object for holding request history.
     */
    @Data
    @RequiredArgsConstructor
    static class RequestAttempt {
        /**
         * Timestamp of logging given request attempt.
         */
        private final Instant timestamp = Instant.now();

        /**
         * Result of request attempt.
         */
        private final Object object;

        /**
         * Checks if request attempt was successful or not.
         * @return success status
         */
        boolean isSuccess() {
            return !(object instanceof Throwable);
        }

        /**
         * Casts result to {@link CurrentWeatherResponse}.
         * @return successful result
         */
        CurrentWeatherResponse getResponse() {
            return (CurrentWeatherResponse) object;
        }

        /**
         * Casts result to {@link Throwable}.
         * @return failure reason
         */
        Throwable getThrowable() {
            return (Throwable) object;
        }

        /**
         * Maps given request attempt to health status detail.
         * @param includeObject should detail map include complete object ({@link CurrentWeatherResponse} or
         * {@link Throwable}) or just information about success.
         * @return details map
         */
        Map<String, Object> toDetailsMap(boolean includeObject) {
            Map<String, Object> details = new LinkedHashMap<>();
            details.put("timestamp", ISO_LOCAL_DATE_TIME.withZone(systemDefault()).format(timestamp));
            if (includeObject) {
                if (isSuccess()) {
                    details.put("response", getResponse());
                } else {
                    details.put("throwable", getThrowable());
                }
            } else {
                details.put("success", isSuccess());
            }
            return details;
        }
    }
}
