package com.github.mwierzchowski.helios.adapter.sunapi;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.sunrisesunset.api.SunriseSunsetApi;
import org.sunrisesunset.invoker.ApiClient;

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
}
