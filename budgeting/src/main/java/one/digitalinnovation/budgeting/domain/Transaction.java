package one.digitalinnovation.budgeting.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade de Domínio que representa uma Transação Financeira.
 * 
 * Implementada como Record (Java 14+) para:
 * - Imutabilidade
 * - Getters automáticos
 * - equals(), hashCode(), toString() automáticos
 * - Menos boilerplate que classe tradicional
 * 
 * Anota com JPA para persistência automática em banco de dados.
 * 
 * Validações de domínio estão em isValid().
 */
@Entity
@Table(name = "transaction")
public record Transaction(
    
    /**
     * Identificador único da transação (Primary Key).
     * Gerado como UUID para evitar colisões e ser distribuído.
     */
    @Id
    UUID id,
    
    /**
     * Descrição ou nome da transação.
     * Exemplo: "Almoço no restaurante", "Salário mensal"
     * 
     * Obrigatório e não pode ser vazio ou null.
     */
    @Column(nullable = false, length = 255)
    String description,
    
    /**
     * Valor da transação em formato decimal.
     * Exemplo: BigDecimal.valueOf(50.00)
     * 
     * Deve ser sempre > 0 (validado em isValid())
     * Tipo: NUMERIC(10,2) no banco
     */
    @Column(nullable = false, precision = 10, scale = 2)
    BigDecimal amount,
    
    /**
     * Tipo da transação: INCOME (renda) ou EXPENSE (despesa).
     * 
     * Armazenado como STRING no banco (ex: "INCOME", "EXPENSE")
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    TransactionType type,
    
    /**
     * Data e hora da criação da transação.
     * Definida automaticamente no momento da criação.
     * 
     * Imutável após criação (updatable = false)
     */
    @Column(nullable = false, updatable = false)
    LocalDateTime createdAt
    
) {
    
    /**
     * Validações de domínio puro.
     * 
     * Pode ser testado SEM Spring, SEM banco de dados.
     * É a "lógica de negócio" que deve ser válida sempre.
     * 
     * @return true se a transação é válida
     */
    public boolean isValid() {
        return isDescriptionValid() && isAmountValid();
    }
    
    /**
     * Valida se a descrição é válida.
     * - Não pode ser null
     * - Não pode estar vazia (após trim)
     * - Tamanho entre 1 e 255 caracteres
     */
    private boolean isDescriptionValid() {
        return description != null 
            && !description.trim().isEmpty() 
            && description.length() <= 255;
    }
    
    /**
     * Valida se o valor é válido.
     * - Não pode ser null
     * - Deve ser maior que 0
     * - Deve ter no máximo 2 casas decimais (money)
     */
    private boolean isAmountValid() {
        return amount != null 
            && amount.compareTo(BigDecimal.ZERO) > 0
            && amount.scale() <= 2;
    }
    
    /**
     * Calcula o saldo afetado por esta transação.
     * 
     * Positivo se INCOME, negativo se EXPENSE.
     * Útil para cálculos de saldo total.
     * 
     * @return amount se INCOME, -amount se EXPENSE
     */
    public BigDecimal getBalanceImpact() {
        return type.isExpense() ? amount.negate() : amount;
    }
    
    /**
     * Retorna uma descrição legível da transação.
     * 
     * Exemplo: "[EXPENSE] Almoço no restaurante - R$50.00"
     */
    @Override
    public String toString() {
        return String.format("[%s] %s - R$%s (%s)",
            type.name(),
            description,
            amount,
            createdAt);
    }
}
