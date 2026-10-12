package one.digitalinnovation.budgeting.infrastructure.persistence;

import one.digitalinnovation.budgeting.domain.Transaction;
import one.digitalinnovation.budgeting.domain.TransactionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementação de TransactionRepository usando Spring Data JPA.
 * 
 * Localizado na camada Infrastructure, este adapter:
 * 1. Implementa a interface de domínio (TransactionRepository)
 * 2. Delega para Spring Data JPA (SpringDataTransactionRepository)
 * 3. Abstrai detalhes de persistência do domínio
 * 
 * PADRÃO: Adapter Pattern
 * - TransactionRepository = interface de domínio (contrato puro)
 * - TransactionJpaRepository = implementação com JPA (infraestrutura)
 * 
 * BENEFÍCIO: Domínio não depende de Spring Data JPA.
 * Se quisermos trocar para Hibernate, MongoDB, etc, só mudamos esta classe.
 * 
 * FLUXO:
 * Use Case (application)
 *    ↓ depende de
 * TransactionRepository (domain interface)
 *    ↑ implementado por
 * TransactionJpaRepository (infrastructure)
 *    ↓ delega para
 * SpringDataTransactionRepository (Spring Data JPA)
 *    ↓ acessa
 * H2 Database
 * 
 * @author Voice Budgeting Team
 * @version 1.0
 */
@Repository
public class TransactionJpaRepository implements TransactionRepository {
    
    /**
     * Interface Spring Data JPA para persistência.
     * 
     * Fornece:
     * - save(T), findById(ID), findAll(), delete(), count()
     * - Gerenciamento automático de transações
     * - Queries customizadas por method names
     * 
     * Injetado automaticamente pelo Spring.
     */
    private final SpringDataTransactionRepository springDataRepository;
    
    /**
     * Constructor Injection.
     * 
     * Spring injeta automaticamente a implementação de SpringDataTransactionRepository
     * (Spring Data gera a implementação em runtime).
     * 
     * @param springDataRepository Interface JPA injetada
     */
    public TransactionJpaRepository(SpringDataTransactionRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }
    
    /**
     * Salva uma transação no banco de dados.
     * 
     * Delega para: springDataRepository.save(transaction)
     * 
     * Se transação tem ID novo: INSERT
     * Se transação tem ID existente: UPDATE
     * 
     * @param transaction Transação a ser salva (validada antes de chegar aqui)
     * @return Transaction persistida com ID confirmado
     * 
     * @throws IllegalArgumentException Se transaction for null
     * @throws DataIntegrityViolationException Se violar constraints do BD
     */
    @Override
    public Transaction save(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }
        return springDataRepository.save(transaction);
    }
    
    /**
     * Busca uma transação pelo ID.
     * 
     * Delega para: springDataRepository.findById(id)
     * 
     * SELECT * FROM transaction WHERE id = ?
     * 
     * @param id Identificador da transação
     * @return Optional com a transação (vazio se não encontrada)
     * 
     * @throws IllegalArgumentException Se id for null
     */
    @Override
    public Optional<Transaction> findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        return springDataRepository.findById(id);
    }
    
    /**
     * Retorna todas as transações do banco.
     * 
     * Delega para: springDataRepository.findAll()
     * 
     * SELECT * FROM transaction
     * 
     * CUIDADO: Se há muitas transações (milhões), isso pode ser lento.
     * Considerar paginação (findAll(Pageable)) em produção.
     * 
     * @return Lista de todas as transações (nunca null, pode ser vazia)
     */
    @Override
    public List<Transaction> findAll() {
        return springDataRepository.findAll();
    }
    
    /**
     * Deleta uma transação pelo ID.
     * 
     * Delega para: springDataRepository.deleteById(id)
     * 
     * DELETE FROM transaction WHERE id = ?
     * 
     * IDEMPOTENTE: Se transação não existe, não lança erro.
     * 
     * @param id Identificador da transação a deletar
     * 
     * @throws IllegalArgumentException Se id for null
     * @throws EmptyResultDataAccessException Se id não encontrado
     *         (na maioria dos casos, Spring ignora e não lança)
     */
    @Override
    public void delete(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        springDataRepository.deleteById(id);
    }
    
    /**
     * Conta o total de transações.
     * 
     * Delega para: springDataRepository.count()
     * 
     * SELECT COUNT(*) FROM transaction
     * 
     * @return Número de transações no banco
     */
    @Override
    public long count() {
        return springDataRepository.count();
    }
}
