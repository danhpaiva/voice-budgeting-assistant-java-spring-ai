package one.digitalinnovation.budgeting.domain;

/**
 * Enum que representa os tipos de transação financeira.
 * 
 * É um Value Object — não tem identidade, apenas valores fixos.
 * Imutável e seguro para usar em qualquer lugar.
 */
public enum TransactionType {
    
    /**
     * Renda ou receita financeira.
     * Exemplo: Salário, bônus, freelance, investimento.
     */
    INCOME,
    
    /**
     * Despesa ou gasto financeiro.
     * Exemplo: Almoço, uber, conta de energia, compra online.
     */
    EXPENSE;
    
    /**
     * Retorna true se é uma entrada (income).
     */
    public boolean isIncome() {
        return this == INCOME;
    }
    
    /**
     * Retorna true se é uma saída (expense).
     */
    public boolean isExpense() {
        return this == EXPENSE;
    }
    
    /**
     * Retorna o label em português.
     */
    public String getLabel() {
        return switch (this) {
            case INCOME -> "Renda";
            case EXPENSE -> "Despesa";
        };
    }
}
