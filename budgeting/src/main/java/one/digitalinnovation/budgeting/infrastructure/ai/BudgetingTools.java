package one.digitalinnovation.budgeting.infrastructure.ai;

import one.digitalinnovation.budgeting.application.CreateTransactionUseCase;
import one.digitalinnovation.budgeting.application.GetTransactionsUseCase;
import one.digitalinnovation.budgeting.domain.Transaction;
import one.digitalinnovation.budgeting.domain.TransactionType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Component que expõe Use Cases como ferramentas de IA (Tool Calling).
 * 
 * Este component permite que o ChatClient (Spring AI) acesse
 * funcionalidades da aplicação como "funções" que o modelo de IA
 * pode chamar quando necessário.
 * 
 * PADRÃO: Tool Calling / Function Calling
 * O modelo de linguagem pode:
 * 1. Receber um prompt em português (ou inglês)
 * 2. Entender a intenção do usuário
 * 3. Chamar automaticamente a função apropriada
 * 4. Retornar resultado ao usuário
 * 
 * FLUXO:
 * "Adicione uma despesa de 50 reais com almoço"
 *   ↓
 * ChatClient + BudgetingTools
 *   ↓
 * Modelo detecta: "criar transação"
 *   ↓
 * Chama createTransaction("Almoço", 50, EXPENSE)
 *   ↓
 * Retorna: "Transação criada com sucesso!"
 * 
 * @author Voice Budgeting Team
 * @version 1.0
 */
@Component
public class BudgetingTools {
    
    /**
     * Use case para criar transações.
     * Injetado automaticamente pelo Spring.
     */
    private final CreateTransactionUseCase createTransactionUseCase;
    
    /**
     * Use case para consultar transações.
     * Injetado automaticamente pelo Spring.
     */
    private final GetTransactionsUseCase getTransactionsUseCase;
    
    /**
     * Constructor Injection.
     * 
     * @param createTransactionUseCase Injetado pelo Spring
     * @param getTransactionsUseCase Injetado pelo Spring
     */
    public BudgetingTools(CreateTransactionUseCase createTransactionUseCase,
                         GetTransactionsUseCase getTransactionsUseCase) {
        this.createTransactionUseCase = createTransactionUseCase;
        this.getTransactionsUseCase = getTransactionsUseCase;
    }
    
    /**
     * Ferramenta de IA para CRIAR uma transação.
     * 
     * O modelo de linguagem pode chamar esta função quando detectar
     * intenção do usuário de adicionar uma transação.
     * 
     * IMPORTANTE: Descrição e nomes de parâmetros devem ser em inglês,
     * pois o modelo foi treinado principalmente em inglês.
     * 
     * O modelo "vê" esta função assim:
     * - Nome: createTransaction
     * - Descrição: "Creates a financial transaction..."
     * - Parâmetros: description, amount, type
     * - Retorno: Transaction object or error
     * 
     * EXEMPLO DE CHAMADA DA IA:
     * Usuário: "Adicionar despesa de 50 reais com almoço"
     *   ↓
     * Modelo chama: createTransaction(
     *     description="Almoço",
     *     amount=50.00,
     *     type="EXPENSE"
     * )
     *   ↓
     * Função retorna: Transaction persisted
     *   ↓
     * Modelo responde: "Transação criada com sucesso!"
     * 
     * @param description Descrição da transação (ex: "Lunch at the restaurant")
     * @param amount Valor em BIG_DECIMAL (ex: 50.00)
     * @param type Tipo: "INCOME" ou "EXPENSE"
     * 
     * @return Transaction criada e persistida
     * 
     * @throws IllegalArgumentException Se validação falhar
     * @throws Exception Se persistência falhar
     */
    public Transaction createTransaction(String description, 
                                        BigDecimal amount, 
                                        TransactionType type) {
        // Delega para use case que já trata validações
        return createTransactionUseCase.execute(description, amount, type);
    }
    
    /**
     * Ferramenta de IA para LISTAR todas as transações.
     * 
     * O modelo pode chamar esta função quando detectar intenção
     * do usuário de ver ou consultar suas transações.
     * 
     * EXEMPLO DE CHAMADA DA IA:
     * Usuário: "Mostre minhas transações"
     *   ↓
     * Modelo chama: listTransactions()
     *   ↓
     * Função retorna: List<Transaction> com todas
     *   ↓
     * Modelo resposta em linguagem natural:
     *   "Você tem 3 transações:
     *    1. Almoço - R$50 (Despesa)
     *    2. Salário - R$5000 (Renda)
     *    3. Uber - R$25 (Despesa)"
     * 
     * @return List com todas as transações (pode ser vazia)
     */
    public List<Transaction> listTransactions() {
        // Delega para use case
        return getTransactionsUseCase.execute();
    }
    
    /**
     * Ferramenta de IA para CALCULAR SALDO.
     * 
     * O modelo pode chamar para calcular saldo total
     * (soma de rendas - soma de despesas).
     * 
     * EXEMPLO DE CHAMADA DA IA:
     * Usuário: "Qual é meu saldo?"
     *   ↓
     * Modelo chama: calculateBalance()
     *   ↓
     * Função retorna: BigDecimal com saldo
     *   ↓
     * Modelo responde: "Seu saldo é R$4925.00"
     * 
     * @return Saldo total (pode ser negativo se déficit)
     */
    public BigDecimal calculateBalance() {
        return getTransactionsUseCase.calculateBalance();
    }
    
    /**
     * Ferramenta de IA para LISTAR transações por TIPO.
     * 
     * O modelo pode chamar para filtrar por tipo (renda ou despesa).
     * 
     * EXEMPLO DE CHAMADA DA IA:
     * Usuário: "Quais são meus gastos?"
     *   ↓
     * Modelo chama: listTransactionsByType(EXPENSE)
     *   ↓
     * Função retorna: List<Transaction> apenas de despesas
     *   ↓
     * Modelo responde: "Seus gastos totalizam R$75"
     * 
     * @param type Tipo: INCOME ou EXPENSE
     * @return List com transações daquele tipo
     */
    public List<Transaction> listTransactionsByType(TransactionType type) {
        return getTransactionsUseCase.executeByType(type);
    }
    
    /**
     * Ferramenta de IA para CALCULAR TOTAL por TIPO.
     * 
     * EXEMPLO DE CHAMADA DA IA:
     * Usuário: "Quanto gastei?"
     *   ↓
     * Modelo chama: calculateTotalByType(EXPENSE)
     *   ↓
     * Função retorna: BigDecimal com soma
     *   ↓
     * Modelo responde: "Você gastou R$75.00 no total"
     * 
     * @param type Tipo: INCOME ou EXPENSE
     * @return Soma de todas as transações daquele tipo
     */
    public BigDecimal calculateTotalByType(TransactionType type) {
        return getTransactionsUseCase.calculateByType(type);
    }
    
    /**
     * Ferramenta de IA para CONTAR transações.
     * 
     * Útil para o modelo informar quantas transações o usuário tem.
     * 
     * @return Total de transações cadastradas
     */
    public long countTransactions() {
        return getTransactionsUseCase.countAll();
    }
}
