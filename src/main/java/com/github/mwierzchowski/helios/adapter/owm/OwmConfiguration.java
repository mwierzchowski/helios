package com.github.mwierzchowski.helios.adapter.owm;

import com.github.mwierzchowski.helios.HeliosProperties;
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
    public ApiClient owmApiClient(HeliosProperties properties) {
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(properties.getOwm().getBasePath());
        apiClient.setApiKey(properties.getOwm().getApiKey());
        return apiClient;
    }

    @Bean
    public CurrentWeatherApi currentWeatherApi(ApiClient apiClient) {
        return new CurrentWeatherApi(apiClient);
    }
}
