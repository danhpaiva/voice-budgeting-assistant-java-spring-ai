package one.digitalinnovation.budgeting.infrastructure.web;

import one.digitalinnovation.budgeting.application.CreateTransactionUseCase;
import one.digitalinnovation.budgeting.application.GetTransactionsUseCase;
import one.digitalinnovation.budgeting.infrastructure.web.dto.CreateTransactionRequest;
import one.digitalinnovation.budgeting.infrastructure.web.dto.TransactionDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller para operações de Transações.
 * 
 * Localizado na camada Infrastructure (web), este controller:
 * 1. Recebe requisições HTTP
 * 2. Converte para DTOs
 * 3. Chama use cases
 * 4. Converte resposta em DTOs
 * 5. Retorna HTTP response
 * 
 * ENDPOINTS:
 * - POST /transactions        → Cria nova transação
 * - GET  /transactions        → Lista todas
 * - GET  /transactions/{id}   → Busca por ID
 * 
 * VALIDAÇÕES:
 * - @Valid → Valida DTO com Jakarta Validation
 * - @Validated → Habilita validação ao nível da classe
 * 
 * DOCUMENTAÇÃO:
 * - @Operation, @ApiResponse → OpenAPI/Swagger
 * - Endpoints aparecem em http://localhost:8080/swagger-ui.html
 * 
 * @author Voice Budgeting Team
 * @version 1.0
 */
@RestController
@RequestMapping("/transactions")
@Validated
@Tag(name = "Transactions", description = "API for managing financial transactions")
public class TransactionController {
    
    /**
     * Use case para criar transações.
     * Injetado automaticamente pelo Spring.
     */
    private final CreateTransactionUseCase createUseCase;
    
    /**
     * Use case para consultar transações.
     * Injetado automaticamente pelo Spring.
     */
    private final GetTransactionsUseCase getUseCase;
    
    /**
     * Constructor Injection.
     * 
     * @param createUseCase Use case para criar (injetado pelo Spring)
     * @param getUseCase Use case para consultar (injetado pelo Spring)
     */
    public TransactionController(CreateTransactionUseCase createUseCase,
                                GetTransactionsUseCase getUseCase) {
        this.createUseCase = createUseCase;
        this.getUseCase = getUseCase;
    }
    
    /**
     * POST /transactions
     * 
     * Cria uma nova transação financeira.
     * 
     * Fluxo:
     * 1. Cliente envia JSON com description, amount, type
     * 2. @Valid valida contra CreateTransactionRequest
     * 3. Controller chama createUseCase.execute()
     * 4. Use case retorna Transaction
     * 5. Controller converte para TransactionDTO
     * 6. Response 201 Created com DTO em JSON
     * 
     * @param request DTO com dados da transação
     * @return ResponseEntity com status 201 e DTO da transação criada
     * @throws IllegalArgumentException Se validação falhar (400)
     * 
     * Exemplo de Request:
     * <pre>
     * POST /transactions
     * Content-Type: application/json
     * 
     * {
     *   "description": "Almoço no restaurante",
     *   "amount": 50.00,
     *   "type": "EXPENSE"
     * }
     * </pre>
     * 
     * Exemplo de Response (201 Created):
     * <pre>
     * {
     *   "id": "123e4567-e89b-12d3-a456-426614174000",
     *   "description": "Almoço no restaurante",
     *   "amount": 50.00,
     *   "type": "EXPENSE",
     *   "createdAt": "2026-10-12T15:10:30"
     * }
     * </pre>
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new transaction",
               description = "Creates a new financial transaction and persists it to the database")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Transaction created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public TransactionDTO create(
        @Valid @RequestBody CreateTransactionRequest request
    ) {
        // 1. Chama use case com dados primitivos extraídos do DTO
        var transaction = createUseCase.execute(
            request.description(),
            request.amount(),
            request.type()
        );
        
        // 2. Converte Transaction (domínio) → TransactionDTO (API)
        return TransactionDTO.from(transaction);
    }
    
    /**
     * GET /transactions
     * 
     * Retorna todas as transações cadastradas.
     * 
     * Fluxo:
     * 1. Cliente faz GET /transactions
     * 2. Controller chama getUseCase.execute()
     * 3. Use case retorna List<Transaction>
     * 4. Controller converte para List<TransactionDTO>
     * 5. Response 200 OK com JSON array
     * 
     * @return List de TransactionDTO (pode ser vazia)
     * 
     * Exemplo de Response (200 OK):
     * <pre>
     * [
     *   {
     *     "id": "123e4567-e89b-12d3-a456-426614174000",
     *     "description": "Almoço no restaurante",
     *     "amount": 50.00,
     *     "type": "EXPENSE",
     *     "createdAt": "2026-10-12T15:10:30"
     *   },
     *   {
     *     "id": "223e4567-e89b-12d3-a456-426614174001",
     *     "description": "Salário",
     *     "amount": 5000.00,
     *     "type": "INCOME",
     *     "createdAt": "2026-10-12T14:00:00"
     *   }
     * ]
     * </pre>
     * 
     * NOTA: Em produção com muitos registros, considerar:
     * - Paginação: Pageable
     * - Filtros: por tipo, data, etc
     * - Sorting: por data, valor, etc
     */
    @GetMapping
    @Operation(summary = "List all transactions",
               description = "Retrieves all financial transactions from the database")
    @ApiResponse(responseCode = "200", description = "List of transactions")
    public List<TransactionDTO> listAll() {
        // 1. Chama use case para obter todas
        return getUseCase.execute().stream()
            // 2. Converte cada Transaction → TransactionDTO
            .map(TransactionDTO::from)
            // 3. Coleta em lista
            .toList();
    }
    
    /**
     * GET /transactions/{id}
     * 
     * Busca uma transação específica pelo seu ID.
     * 
     * Fluxo:
     * 1. Cliente faz GET /transactions/{id}
     * 2. Spring extrai {id} como UUID
     * 3. Controller chama getUseCase.executeById(id)
     * 4. Use case retorna Optional<Transaction>
     * 5. Se encontrado: Response 200 OK com DTO
     * 6. Se não encontrado: Response 404 Not Found
     * 
     * @param id Identificador da transação (UUID)
     * @return ResponseEntity com status 200 e DTO se encontrada,
     *         ou status 404 se não encontrada
     * 
     * Exemplo de Request:
     * <pre>
     * GET /transactions/123e4567-e89b-12d3-a456-426614174000
     * </pre>
     * 
     * Exemplo de Response (200 OK):
     * <pre>
     * {
     *   "id": "123e4567-e89b-12d3-a456-426614174000",
     *   "description": "Almoço no restaurante",
     *   "amount": 50.00,
     *   "type": "EXPENSE",
     *   "createdAt": "2026-10-12T15:10:30"
     * }
     * </pre>
     * 
     * Exemplo de Response (404 Not Found):
     * <pre>
     * {
     *   "timestamp": "2026-10-12T15:20:00",
     *   "status": 404,
     *   "error": "Not Found",
     *   "message": "Transaction not found",
     *   "path": "/transactions/invalid-id"
     * }
     * </pre>
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get transaction by ID",
               description = "Retrieves a specific transaction by its unique identifier")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transaction found"),
        @ApiResponse(responseCode = "404", description = "Transaction not found")
    })
    public ResponseEntity<TransactionDTO> getById(
        @Parameter(description = "Transaction ID (UUID)", required = true)
        @PathVariable UUID id
    ) {
        // 1. Chama use case para buscar por ID
        return getUseCase.executeById(id)
            // 2. Se encontrado: map para ResponseEntity com status 200
            .map(transaction -> ResponseEntity.ok(TransactionDTO.from(transaction)))
            // 3. Se não encontrado: retorna 404
            .orElse(ResponseEntity.notFound().build());
    }
}
