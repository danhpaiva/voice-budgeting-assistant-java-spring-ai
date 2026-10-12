package one.digitalinnovation.budgeting.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Contrato/Interface do Repositório de Transações no Domain Layer.
 * 
 * IMPORTANTE: Esta interface está no Domain Layer (puro).
 * - NÃO depende de Spring
 * - NÃO depende de JPA
 * - É apenas um contrato de negócio
 * 
 * A implementação concreta (com JPA) fica na camada Infrastructure.
 * 
 * Benefícios:
 * - Desacoplamento: domínio não sabe sobre JPA
 * - Testabilidade: fácil mockar para testes
 * - Flexibilidade: trocar implementação sem afetar domínio
 * - Clareza: contrato explícito entre camadas
 */
public interface TransactionRepository {
    
    /**
     * Salva uma transação no repositório.
     * 
     * Se a transação já existe (mesmo ID), atualiza.
     * Se é nova (ID gerado), insere.
     * 
     * @param transaction Transação a ser salva (nunca null)
     * @return A transação salva (pode ter ID alterado se foi gerado)
     * @throws IllegalArgumentException se transação for null ou inválida
     */
    Transaction save(Transaction transaction);
    
    /**
     * Busca uma transação por seu ID.
     * 
     * @param id Identificador único da transação
     * @return Optional contendo a transação se encontrada, vazio caso contrário
     * @throws IllegalArgumentException se id for null
     */
    Optional<Transaction> findById(UUID id);
    
    /**
     * Retorna todas as transações do repositório.
     * 
     * A ordem não é garantida.
     * Pode ser vazio se não há transações.
     * 
     * @return Lista (possivelmente vazia) de todas as transações
     */
    List<Transaction> findAll();
    
    /**
     * Deleta uma transação pelo seu ID.
     * 
     * Se a transação não existe, nada acontece (idempotente).
     * 
     * @param id Identificador da transação a deletar
     * @throws IllegalArgumentException se id for null
     */
    void delete(UUID id);
    
    /**
     * Conta o total de transações no repositório.
     * 
     * Método auxiliar útil para debug e testes.
     * 
     * @return Número de transações
     */
    long count();
}
