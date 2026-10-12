package one.digitalinnovation.budgeting.infrastructure.web;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI / Swagger configuration for Voice Budgeting Assistant.
 * 
 * Customizes the Swagger/OpenAPI documentation generation.
 * 
 * @author Voice Budgeting Team
 * @version 1.0
 */
@Configuration
public class OpenApiConfig {

    /**
     * Define informações gerais da API.
     * 
     * @return OpenAPI instance com metadados
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Voice Budgeting Assistant API")
                .version("0.0.1-SNAPSHOT")
                .description("REST API para gerenciar transações financeiras com suporte a IA e processamento de voz")
                .contact(new Contact()
                    .name("Daniel Paiva")
                    .url("https://github.com/danhpaiva")
                    .email("daniel@example.com"))
                .license(new License()
                    .name("MIT")
                    .url("https://github.com/danhpaiva/voice-budgeting-assistant-java-spring-ai/blob/develop/LICENSE")));
    }
}
