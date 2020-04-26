package com.github.mwierzchowski.helios.adapter.sunapi;

import org.springframework.stereotype.Component;
import org.sunrisesunset.model.SunriseSunsetResponse;

@Component
public class SunApiHealthIndicator {
    public void register(SunriseSunsetResponse response) {
        // todo
    }

    public void register(Throwable throwable) {
        // todo
    }
}
