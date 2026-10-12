package one.digitalinnovation.budgeting.infrastructure.web.dto;

import one.digitalinnovation.budgeting.domain.Transaction;
import one.digitalinnovation.budgeting.domain.TransactionType;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO (Data Transfer Object) para RESPOSTA de transação.
 * 
 * Localizado na camada Infrastructure (web), este record:
 * 1. Recebe entidade de domínio (Transaction)
 * 2. Converte para formato JSON
 * 3. Envia ao cliente HTTP
 * 
 * IMPORTANTE: Use case retorna Transaction (domínio).
 * Controller converte Transaction → TransactionDTO antes de serializar.
 * 
 * Benefícios:
 * - Separa representação (JSON) de domínio (Transaction)
 * - Permite customizar formato JSON
 * - Oculta detalhes internos
 * - Facilita evolução de API
 * 
 * @author Voice Budgeting Team
 * @version 1.0
 */
public record TransactionDTO(
    
    /**
     * Identificador único da transação.
     * 
     * Formato: UUID (ex: "123e4567-e89b-12d3-a456-426614174000")
     */
    UUID id,
    
    /**
     * Descrição da transação.
     * 
     * Exemplo: "Almoço no restaurante"
     */
    String description,
    
    /**
     * Valor da transação.
     * 
     * Exemplo: 50.00
     */
    BigDecimal amount,
    
    /**
     * Tipo de transação.
     * 
     * Valores possíveis: INCOME, EXPENSE
     */
    TransactionType type,
    
    /**
     * Data e hora de criação da transação.
     * 
     * Formatação: ISO-8601 (ex: "2026-10-12T15:10:30")
     * 
     * @JsonFormat customiza o formato na serialização JSON.
     * - pattern: formato ISO-8601
     * - timezone: UTC (Z)
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
    LocalDateTime createdAt
    
) {
    
    /**
     * Factory method para converter Transaction (domínio) → TransactionDTO (DTO).
     * 
     * Use em controller:
     * <pre>
     * Transaction transaction = useCase.execute(...);
     * TransactionDTO dto = TransactionDTO.from(transaction);
     * </pre>
     * 
     * @param transaction Entidade de domínio
     * @return DTO com mesmos valores
     */
    public static TransactionDTO from(Transaction transaction) {
        return new TransactionDTO(
            transaction.id(),
            transaction.description(),
            transaction.amount(),
            transaction.type(),
            transaction.createdAt()
        );
    }
}
