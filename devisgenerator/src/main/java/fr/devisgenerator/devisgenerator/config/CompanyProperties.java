package fr.devisgenerator.devisgenerator.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "company")
public record CompanyProperties(
        String name,
        String address,
        String email,
        String phone
) {
}
