package one.digitalinnovation.budgeting.infrastructure.persistence;

import one.digitalinnovation.budgeting.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Interface Spring Data JPA para persistência de Transaction.
 * 
 * Esta interface fica na camada Infrastructure (abstração do JPA).
 * Herda de JpaRepository que fornece:
 * - save(), findById(), findAll(), deleteById() automáticos
 * - Queries customizadas via method names
 * - Paginação e sorting
 * 
 * IMPORTANTE: Esta NÃO é a mesma que TransactionRepository do domínio.
 * Esta é a interface Spring Data JPA (específica de infraestrutura).
 * 
 * A interface de domínio (TransactionRepository) será implementada
 * por TransactionJpaRepository que DELEGA para esta.
 * 
 * @author Voice Budgeting Team
 * @version 1.0
 */
@Repository
public interface SpringDataTransactionRepository extends JpaRepository<Transaction, UUID> {
    
    // JpaRepository já fornece:
    // - save(T): salva
    // - findById(ID): busca por ID
    // - findAll(): lista todas
    // - deleteById(ID): deleta
    // - delete(T): deleta
    // - deleteAll(): deleta todas
    // - count(): conta
    
    // Poderia adicionar queries customizadas aqui, ex:
    // List<Transaction> findByType(TransactionType type);
    // Page<Transaction> findAll(Pageable pageable);
}
