package com.github.mwierzchowski.helios.adapter.owm;

import org.openweathermap.api.CurrentWeatherApi;
import org.openweathermap.invoker.ApiClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Open Weather Map (OWM) adapter configuration.
 * @author Marcin Wierzchowski
 */
@Configuration
public class OwmConfiguration {
    @Bean
    public ApiClient owmApiClient(OwmProperties owmProperties) {
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(owmProperties.getBasePath());
        apiClient.setApiKey(owmProperties.getApiKey());
        return apiClient;
    }

    @Bean
    public CurrentWeatherApi currentWeatherApi(ApiClient apiClient) {
        return new CurrentWeatherApi(apiClient);
    }
}
