package one.digitalinnovation.budgeting.application;

import one.digitalinnovation.budgeting.domain.Transaction;
import one.digitalinnovation.budgeting.domain.TransactionRepository;
import one.digitalinnovation.budgeting.domain.TransactionType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Use Case para CONSULTAR transações financeiras.
 * 
 * Localizado na Camada de Aplicação, este serviço fornece:
 * 1. Listar todas as transações
 * 2. Buscar transação por ID
 * 3. Listar transações filtradas por tipo (opcional)
 * 4. Calcular totais e estatísticas
 * 
 * IMPORTANTE: Depende apenas da INTERFACE do repositório, não da implementação.
 * Facilita testes com mock e desacoplamento de persistência.
 * 
 * Método de Execução:
 * - Entrada: critério de busca (ID, tipo, etc)
 * - Processamento: consulta via repositório
 * - Saída: entidades de domínio (nunca nulo, sempre Optional ou List)
 * 
 * @author Voice Budgeting Team
 * @version 1.0
 */
@Service
public class GetTransactionsUseCase {
    
    /**
     * Repositório para consultas.
     * 
     * IMPORTANTE: É uma INTERFACE (TransactionRepository), não uma implementação.
     * A implementação concreta (JPA) fica na camada Infrastructure.
     */
    private final TransactionRepository repository;
    
    /**
     * Constructor Injection.
     * 
     * @param repository Implementação do TransactionRepository (injetada pelo Spring)
     */
    public GetTransactionsUseCase(TransactionRepository repository) {
        this.repository = repository;
    }
    
    /**
     * Retorna TODAS as transações do repositório.
     * 
     * Fluxo:
     * 1. Consulta repositório (getAll)
     * 2. Retorna lista (pode ser vazia)
     * 
     * @return Lista de todas as transações (nunca null)
     *         Vazio se não há transações
     * 
     * Exemplo:
     * <pre>
     * var transactions = getTransactionsUseCase.execute();
     * if (transactions.isEmpty()) {
     *     System.out.println("Nenhuma transação");
     * } else {
     *     transactions.forEach(System.out::println);
     * }
     * </pre>
     */
    public List<Transaction> execute() {
        return repository.findAll();
    }
    
    /**
     * Busca uma transação específica por seu ID.
     * 
     * Fluxo:
     * 1. Valida que ID não é null
     * 2. Consulta repositório por ID
     * 3. Retorna Optional (pode estar vazio se não encontrado)
     * 
     * @param id Identificador da transação
     * @return Optional contendo a transação se encontrada
     *         Optional vazio se não encontrada
     * 
     * @throws IllegalArgumentException Se id for null
     * 
     * Exemplo:
     * <pre>
     * var transaction = getTransactionsUseCase.executeById(uuid);
     * if (transaction.isPresent()) {
     *     System.out.println("Encontrada: " + transaction.get());
     * } else {
     *     System.out.println("Não encontrada");
     * }
     * 
     * // Ou com ifPresentOrElse:
     * transaction.ifPresentOrElse(
     *     t -> System.out.println("Encontrada: " + t),
     *     () -> System.out.println("Não encontrada")
     * );
     * </pre>
     */
    public Optional<Transaction> executeById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        return repository.findById(id);
    }
    
    /**
     * Filtra transações por tipo.
     * 
     * Fluxo:
     * 1. Obtém todas as transações
     * 2. Filtra por tipo
     * 3. Retorna lista filtrada
     * 
     * @param type Tipo de transação (INCOME ou EXPENSE)
     * @return Lista de transações do tipo especificado (pode ser vazia)
     * 
     * @throws IllegalArgumentException Se type for null
     * 
     * Exemplo:
     * <pre>
     * var expenses = getTransactionsUseCase.executeByType(EXPENSE);
     * var income = getTransactionsUseCase.executeByType(INCOME);
     * </pre>
     */
    public List<Transaction> executeByType(TransactionType type) {
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null");
        }
        
        return repository.findAll().stream()
            .filter(t -> t.type() == type)
            .collect(Collectors.toList());
    }
    
    /**
     * Calcula o saldo total de todas as transações.
     * 
     * Fluxo:
     * 1. Obtém todas as transações
     * 2. Mapeia cada uma para seu impacto no saldo (amount ou -amount)
     * 3. Soma todos os impactos
     * 4. Retorna total
     * 
     * Fórmula:
     * Saldo = Σ(INCOME) - Σ(EXPENSE)
     * 
     * @return Saldo total (pode ser negativo se mais gastos que renda)
     * 
     * Exemplo:
     * <pre>
     * var balance = getTransactionsUseCase.calculateBalance();
     * if (balance.signum() < 0) {
     *     System.out.println("Déficit: " + balance.negate());
     * } else {
     *     System.out.println("Saldo positivo: " + balance);
     * }
     * </pre>
     */
    public java.math.BigDecimal calculateBalance() {
        return repository.findAll().stream()
            .map(Transaction::getBalanceImpact)
            .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }
    
    /**
     * Calcula a soma de transações por tipo.
     * 
     * @param type Tipo (INCOME ou EXPENSE)
     * @return Soma de todas as transações daquele tipo
     * 
     * @throws IllegalArgumentException Se type for null
     * 
     * Exemplo:
     * <pre>
     * var totalExpense = getTransactionsUseCase.calculateByType(EXPENSE);
     * var totalIncome = getTransactionsUseCase.calculateByType(INCOME);
     * System.out.println("Renda: " + totalIncome);
     * System.out.println("Despesa: " + totalExpense);
     * System.out.println("Saldo: " + (totalIncome - totalExpense));
     * </pre>
     */
    public java.math.BigDecimal calculateByType(TransactionType type) {
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null");
        }
        
        return executeByType(type).stream()
            .map(Transaction::amount)
            .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }
    
    /**
     * Retorna o total de transações cadastradas.
     * 
     * @return Número de transações no repositório
     * 
     * Exemplo:
     * <pre>
     * long total = getTransactionsUseCase.countAll();
     * System.out.println("Total de transações: " + total);
     * </pre>
     */
    public long countAll() {
        return repository.count();
    }
}
