package one.digitalinnovation.budgeting.infrastructure.exception;

/**
 * Exceção lançada quando um recurso não é encontrado.
 * 
 * EXEMPLO:
 * GET /transactions/999 (ID não existe)
 *   ↓
 * Lança: throw new ResourceNotFoundException("Transaction with ID 999 not found")
 *   ↓
 * GlobalExceptionHandler intercepta
 *   ↓
 * Retorna 404 Not Found
 * 
 * PADRÃO HTTP:
 * Status: 404 Not Found
 * Body: {
 *   "timestamp": "...",
 *   "status": 404,
 *   "error": "Resource Not Found",
 *   "message": "Transaction with ID 999 not found",
 *   "path": "/transactions/999"
 * }
 * 
 * @author Voice Budgeting Team
 * @version 1.0
 */
public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
