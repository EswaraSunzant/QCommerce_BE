package com.qcommerce.config;

import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Jackson ObjectMapper.
 * This class ensures that the JsonNullableModule is registered,
 * allowing Jackson to correctly serialize and deserialize JsonNullable types
 * used in OpenAPI generated models.
 */
@Configuration
public class JacksonConfig {

    /**
     * Provides a JsonNullableModule bean that Spring Boot will automatically
     * register with the default Jackson ObjectMapper.
     *
     * @return The JsonNullableModule.
     */
    @Bean
    public JsonNullableModule jsonNullableModule() {
        return new JsonNullableModule();
    }
}
