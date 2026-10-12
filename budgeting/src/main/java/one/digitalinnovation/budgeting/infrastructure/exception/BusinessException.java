package one.digitalinnovation.budgeting.infrastructure.exception;

/**
 * Exceção lançada para erros de negócio/validação.
 * 
 * EXEMPLO:
 * Usuário tenta criar transação com valor negativo
 *   ↓
 * Lança: throw new BusinessException("Transaction amount must be positive")
 *   ↓
 * GlobalExceptionHandler intercepta
 *   ↓
 * Retorna 400 Bad Request
 * 
 * PADRÃO HTTP:
 * Status: 400 Bad Request
 * Body: {
 *   "timestamp": "...",
 *   "status": 400,
 *   "error": "Bad Request",
 *   "message": "Transaction amount must be positive",
 *   "path": "/transactions"
 * }
 * 
 * @author Voice Budgeting Team
 * @version 1.0
 */
public class BusinessException extends RuntimeException {
    
    public BusinessException(String message) {
        super(message);
    }
    
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
