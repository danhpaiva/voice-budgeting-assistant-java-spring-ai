package one.digitalinnovation.budgeting.application;

import one.digitalinnovation.budgeting.domain.Transaction;
import one.digitalinnovation.budgeting.domain.TransactionRepository;
import one.digitalinnovation.budgeting.domain.TransactionType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Use Case para CRIAR uma nova transação financeira.
 * 
 * Localizado na Camada de Aplicação, este serviço orquestra:
 * 1. Criação da entidade de domínio (Transaction)
 * 2. Validação de regras de negócio
 * 3. Persistência via repositório de domínio (interface)
 * 
 * IMPORTANTE: Depende apenas da INTERFACE do repositório, não da implementação.
 * Isso permite testar com mock, sem precisar de banco de dados real.
 * 
 * Método de Execução:
 * - Entrada: dados primitivos (String, BigDecimal, enum)
 * - Processamento: lógica de negócio
 * - Saída: entidade de domínio persistida
 * 
 * @author Voice Budgeting Team
 * @version 1.0
 */
@Service
public class CreateTransactionUseCase {
    
    /**
     * Repositório para persistência.
     * 
     * IMPORTANTE: É uma INTERFACE (TransactionRepository), não uma implementação.
     * A implementação concreta (JPA) fica na camada Infrastructure.
     * 
     * Isso permite:
     * - Testar com mock
     * - Trocar implementação sem afetar este código
     * - Desacoplamento claro entre camadas
     */
    private final TransactionRepository repository;
    
    /**
     * Constructor Injection (Injeção por Construtor).
     * 
     * Benefícios:
     * - Dependência explícita
     * - Imutabilidade (repository é final)
     * - Fácil de testar (passar mock no constructor)
     * - Requer dependência no momento de criação (fail-fast)
     * 
     * Alternativa (NÃO usada): @Autowired em field = acoplamento, dificultoso testar
     * 
     * @param repository Implementação do TransactionRepository (injetada pelo Spring)
     */
    public CreateTransactionUseCase(TransactionRepository repository) {
        this.repository = repository;
    }
    
    /**
     * Executa a ação de criar uma transação.
     * 
     * Fluxo:
     * 1. Recebe dados primitivos (String, BigDecimal, enum)
     * 2. Cria entidade de domínio (Transaction)
     * 3. Valida regras de negócio (isValid())
     * 4. Persiste via repositório
     * 5. Retorna entidade persistida
     * 
     * @param description Descrição da transação (ex: "Almoço no restaurante")
     * @param amount Valor da transação (ex: 50.00)
     * @param type Tipo de transação (INCOME ou EXPENSE)
     * 
     * @return Transaction persistida no banco
     * 
     * @throws IllegalArgumentException Se validação de negócio falhar
     *         - description vazio ou null
     *         - amount <= 0
     *         - type null
     * 
     * @throws Exception Se falhar persistência (propagado do repositório)
     */
    public Transaction execute(String description, BigDecimal amount, TransactionType type) {
        
        // 1. Validar entrada (dados primitivos)
        validateInput(description, amount, type);
        
        // 2. Criar entidade de domínio
        var transaction = new Transaction(
            UUID.randomUUID(),              // ID único gerado aqui
            description,
            amount,
            type,
            LocalDateTime.now()             // Timestamp automático
        );
        
        // 3. Validar regras de negócio (domínio puro)
        if (!transaction.isValid()) {
            throw new IllegalArgumentException(
                String.format("Invalid transaction: description='%s', amount=%s, type=%s",
                    description, amount, type)
            );
        }
        
        // 4. Persistir via repositório (interface de domínio)
        return repository.save(transaction);
    }
    
    /**
     * Valida os dados de entrada antes de processar.
     * 
     * Validações:
     * - description: não pode ser null ou vazio
     * - amount: não pode ser null ou <= 0
     * - type: não pode ser null
     * 
     * @param description Descrição
     * @param amount Valor
     * @param type Tipo
     * 
     * @throws IllegalArgumentException Se validação falhar
     */
    private void validateInput(String description, BigDecimal amount, TransactionType type) {
        
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be null or empty");
        }
        
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        
        if (type == null) {
            throw new IllegalArgumentException("Transaction type cannot be null");
        }
    }
}
