package com.github.mwierzchowski.helios.adapter.sunapi;

import com.github.mwierzchowski.helios.adapter.commons.ExternalServiceHealthIndicator;
import com.github.mwierzchowski.helios.core.commons.EventStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.sunrisesunset.api.SunriseSunsetApi;
import org.sunrisesunset.invoker.ApiClient;
import org.sunrisesunset.model.SunriseSunsetResponse;

/**
 * Sun API adapter configuration.
 * @author Marcin Wierzchowski
 */
@Configuration
public class SunApiConfiguration {
    @Bean
    public ApiClient sunApiClient(SunApiProperties properties) {
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(properties.getBasePath());
        return apiClient;
    }

    @Bean
    public SunriseSunsetApi sunriseSunsetApi(ApiClient apiClient) {
        return new SunriseSunsetApi(apiClient);
    }

    @Bean
    public ExternalServiceHealthIndicator<SunriseSunsetResponse> sunApiHealthIndicator(EventStore eventStore) {
        return new ExternalServiceHealthIndicator<>(SunApiSunEphemerisProvider.class, eventStore);
    }
}
