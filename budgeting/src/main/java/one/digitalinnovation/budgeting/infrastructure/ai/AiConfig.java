package one.digitalinnovation.budgeting.infrastructure.ai;

import org.springframework.context.annotation.Configuration;

/**
 * Configuração do Spring AI.
 * 
 * Spring AI 1.1.8 autoconfigura OpenAiChatModel automaticamente
 * quando spring-ai-starter-model-openai está no classpath.
 * 
 * Não precisamos criar beans custom - apenas usar injeção de dependência.
 * 
 * @author Voice Budgeting Team
 * @version 1.0
 */
@Configuration
public class AiConfig {
    // Spring Boot autoconfigures OpenAiChatModel automatically
}
