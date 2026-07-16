package fr.devisgenerator.devisgenerator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient brevoRestClient(BrevoProperties properties) {

        return RestClient.builder()
                .baseUrl(properties.baseUrl())
                .build();
    }
}