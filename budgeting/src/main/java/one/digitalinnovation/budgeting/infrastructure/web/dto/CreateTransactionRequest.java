package one.digitalinnovation.budgeting.infrastructure.web.dto;

import one.digitalinnovation.budgeting.domain.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * DTO (Data Transfer Object) para REQUISIÇÃO de criação de transação.
 * 
 * Localizado na camada Infrastructure (web), este record:
 * 1. Recebe dados do cliente HTTP
 * 2. Valida entrada com Jakarta Validation
 * 3. Transfere para use case
 * 
 * IMPORTANTE: Use case NUNCA recebe HttpRequest diretamente.
 * Sempre converte para DTO antes.
 * 
 * Validações:
 * - @NotBlank: não pode ser null ou vazio
 * - @NotNull: não pode ser null
 * - @Positive: deve ser > 0
 * 
 * @author Voice Budgeting Team
 * @version 1.0
 */
public record CreateTransactionRequest(
    
    /**
     * Descrição da transação.
     * 
     * Validações:
     * - Obrigatório
     * - Não pode estar vazio
     * - Não pode conter apenas espaços
     * 
     * Exemplo: "Almoço no restaurante"
     */
    @NotBlank(message = "Description is required and cannot be blank")
    String description,
    
    /**
     * Valor da transação.
     * 
     * Validações:
     * - Obrigatório
     * - Deve ser > 0
     * - Tipo: BigDecimal (para precisão monetária)
     * 
     * Exemplo: 50.00
     */
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    BigDecimal amount,
    
    /**
     * Tipo de transação: INCOME ou EXPENSE.
     * 
     * Validações:
     * - Obrigatório
     * - Deve ser um dos valores do enum
     * 
     * Exemplo: EXPENSE
     */
    @NotNull(message = "Transaction type is required")
    TransactionType type
    
) {
    
    /**
     * Constructor compacto do Record.
     * 
     * Pode adicionar lógica de validação adicional aqui
     * se necessário (validação cross-field).
     * 
     * Exemplo:
     * <pre>
     * public CreateTransactionRequest {
     *     if (amount.scale() > 2) {
     *         throw new IllegalArgumentException("Amount can have max 2 decimal places");
     *     }
     * }
     * </pre>
     * 
     * Por enquanto deixamos vazio (validações estão nas annotations).
     */
}
