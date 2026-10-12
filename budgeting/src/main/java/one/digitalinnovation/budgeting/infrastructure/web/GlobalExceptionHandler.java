package one.digitalinnovation.budgeting.infrastructure.web;

import one.digitalinnovation.budgeting.infrastructure.exception.BusinessException;
import one.digitalinnovation.budgeting.infrastructure.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * GlobalExceptionHandler para tratamento centralizado de erros.
 * 
 * RESPONSABILIDADES:
 * 1. Interceptar exceções lançadas pelos controllers
 * 2. Converter em responses HTTP padronizadas
 * 3. Retornar status codes apropriados
 * 4. Fornecer mensagens de erro úteis
 * 
 * MAPEAMENTOS:
 * - ResourceNotFoundException → 404 Not Found
 * - BusinessException → 400 Bad Request
 * - IllegalArgumentException → 400 Bad Request
 * - MethodArgumentNotValidException → 400 Bad Request (validações)
 * - Exception (outros erros) → 500 Internal Server Error
 * 
 * PADRÃO DE RESPOSTA:
 * {
 *   "timestamp": "2026-10-12T16:00:00",
 *   "status": 404,
 *   "error": "Resource Not Found",
 *   "message": "Transaction with ID 999 not found",
 *   "path": "/transactions/999"
 * }
 * 
 * @author Voice Budgeting Team
 * @version 1.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * Trata ResourceNotFoundException (recurso não encontrado → 404).
     * 
     * EXEMPLO:
     * GET /transactions/999 (ID não existe)
     *   ↓
     * TransactionService lança: throw new ResourceNotFoundException("Transaction not found")
     *   ↓
     * Este handler intercepta
     *   ↓
     * Retorna 404 Not Found
     * 
     * @param ex A exceção de recurso não encontrado
     * @param request Informações da requisição HTTP
     * @return ResponseEntity com erro e status 404
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            WebRequest request) {
        
        Map<String, Object> errorDetails = new LinkedHashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("status", HttpStatus.NOT_FOUND.value());
        errorDetails.put("error", "Resource Not Found");
        errorDetails.put("message", ex.getMessage());
        errorDetails.put("path", request.getDescription(false).replace("uri=", ""));
        
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }
    
    /**
     * Trata BusinessException (erros de negócio → 400).
     * 
     * EXEMPLO:
     * POST /transactions com valor negativo
     *   ↓
     * CreateTransactionUseCase lança: throw new BusinessException("Amount must be positive")
     *   ↓
     * Este handler intercepta
     *   ↓
     * Retorna 400 Bad Request
     * 
     * @param ex A exceção de negócio
     * @param request Informações da requisição HTTP
     * @return ResponseEntity com erro e status 400
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, Object>> handleBusinessException(
            BusinessException ex,
            WebRequest request) {
        
        Map<String, Object> errorDetails = new LinkedHashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("status", HttpStatus.BAD_REQUEST.value());
        errorDetails.put("error", "Business Error");
        errorDetails.put("message", ex.getMessage());
        errorDetails.put("path", request.getDescription(false).replace("uri=", ""));
        
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Trata IllegalArgumentException (erros de validação básica → 400).
     * 
     * @param ex A exceção de argumento inválido
     * @param request Informações da requisição HTTP
     * @return ResponseEntity com erro formatado e status 400
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(
            IllegalArgumentException ex,
            WebRequest request) {
        
        Map<String, Object> errorDetails = new LinkedHashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("status", HttpStatus.BAD_REQUEST.value());
        errorDetails.put("error", "Bad Request");
        errorDetails.put("message", ex.getMessage());
        errorDetails.put("path", request.getDescription(false).replace("uri=", ""));
        
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Trata MethodArgumentNotValidException (erros de validação de @Valid → 400).
     * 
     * EXEMPLO:
     * POST /transactions com campo inválido
     * @Valid @RequestBody CreateTransactionRequest falha na validação
     *   ↓
     * Este handler intercepta
     *   ↓
     * Retorna 400 com lista de erros de validação
     * 
     * @param ex A exceção de validação
     * @param request Informações da requisição HTTP
     * @return ResponseEntity com erros de validação e status 400
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            WebRequest request) {
        
        // Extrair mensagens de erro dos campos
        String errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        
        Map<String, Object> errorDetails = new LinkedHashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("status", HttpStatus.BAD_REQUEST.value());
        errorDetails.put("error", "Validation Failed");
        errorDetails.put("message", "Validation errors: " + errors);
        errorDetails.put("path", request.getDescription(false).replace("uri=", ""));
        
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Trata todas as outras exceções não capturadas (erros inesperados → 500).
     * 
     * EXEMPLO:
     * Erro de API, null pointer, exception inesperada, etc
     *   ↓
     * Este handler intercepta como último recurso
     *   ↓
     * Retorna 500 Internal Server Error
     * 
     * @param ex A exceção genérica
     * @param request Informações da requisição HTTP
     * @return ResponseEntity com erro e status 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(
            Exception ex,
            WebRequest request) {
        
        Map<String, Object> errorDetails = new LinkedHashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorDetails.put("error", "Internal Server Error");
        errorDetails.put("message", ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred");
        errorDetails.put("path", request.getDescription(false).replace("uri=", ""));
        
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
