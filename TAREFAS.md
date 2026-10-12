# 📋 Plano de Tarefas — Voice Budgeting Assistant

![Progress](https://img.shields.io/badge/Progress-10%25-yellow?style=flat-square)
![Tasks](https://img.shields.io/badge/Tasks-10-blue?style=flat-square)
![Completed](https://img.shields.io/badge/Completed-1-green?style=flat-square)

Implementação estruturada do Voice Budgeting Assistant em 10 etapas, seguindo Domain-Driven Design (DDD).

---

## Visão Geral

```
Etapa 1: ✅ Configuração Base (CONCLUÍDA)
Etapa 2: ⏳ Camada de Domínio
Etapa 3: ⏳ Camada de Aplicação (Use Cases)
Etapa 4: ⏳ Camada de Infraestrutura (Persistência)
Etapa 5: ⏳ REST CRUD (Endpoints)
Etapa 6: ⏳ Tool Calling com Spring AI
Etapa 7: ⏳ Configuração do ChatClient
Etapa 8: ⏳ Endpoint de Voz (Fluxo Principal)
Etapa 9: ⏳ Tratamento de Erros
Etapa 10: ⏳ Documentação Final
```

---

## ✅ Etapa 1 — Configuração Base

**Status:** ✅ CONCLUÍDA  
**Data:** 12 de outubro de 2026  
**Tempo:** ~1 hora

### O que foi feito

- [x] Projeto Maven com Spring Boot 4.1.1
- [x] Dependências corrigidas e validadas
- [x] H2 Database em memória configurado
- [x] Swagger/OpenAPI integrado (springdoc-openapi-starter-webmvc-ui)
- [x] `application.properties` configurado com:
  - H2 console
  - JPA/Hibernate
  - Logging
- [x] Compilação com sucesso (`BUILD SUCCESS`)
- [x] README documentado com badges

### Verificação

```bash
cd budgeting
./mvnw clean compile

# Esperado: BUILD SUCCESS ✅
```

### Arquivos criados/modificados

- ✅ `budgeting/pom.xml` — Dependências corrigidas
- ✅ `budgeting/src/main/resources/application.properties` — Configuração
- ✅ `budgeting/README.md` — Documentação da app
- ✅ `README.md` — Documentação raiz
- ✅ `CONCEITOS.md` — Guia de DDD
- ✅ `TAREFAS.md` — Este arquivo

---

## ➡️ Etapa 2 — Camada de Domínio

**Status:** ⏳ PLANEJADA  
**Estimativa:** 1-2 horas  
**Dependências:** Etapa 1

### Objetivo

Criar entidades de domínio puro (sem Spring), representando o core do negócio.

### Arquivos a criar

#### 1. `budgeting/src/main/java/one/digitalinnovation/budgeting/domain/TransactionType.java`

```java
package one.digitalinnovation.budgeting.domain;

public enum TransactionType {
    INCOME,    // Renda / Receita
    EXPENSE    // Despesa / Gasto
}
```

**Por quê Record/Enum?**
- Enum = type-safe, imutável, perfeito para valores fixos
- Sem necessidade de getters/setters

---

#### 2. `budgeting/src/main/java/one/digitalinnovation/budgeting/domain/Transaction.java`

```java
package one.digitalinnovation.budgeting.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transaction")
public record Transaction(
    @Id
    UUID id,
    
    @Column(nullable = false)
    String description,
    
    @Column(nullable = false)
    BigDecimal amount,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    TransactionType type,
    
    @Column(nullable = false, updatable = false)
    LocalDateTime createdAt
) {
    
    /**
     * Validação de domínio puro.
     * Pode ser testada sem Spring ou banco de dados.
     */
    public boolean isValid() {
        return description != null && !description.isBlank() &&
               amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }
}
```

**Por quê Record?**
- Imutável (final)
- Getters automáticos (sem prefixo "get")
- `equals()`, `hashCode()`, `toString()` automáticos
- Muito mais conciso que classe tradicional

**Anotações JPA:**
- `@Entity` — Mapeamento para banco de dados
- `@Table(name = "transaction")` — Nome da tabela
- `@Id` — Chave primária
- `@Column(nullable = false)` — Constraints SQL

---

#### 3. `budgeting/src/main/java/one/digitalinnovation/budgeting/domain/TransactionRepository.java`

```java
package one.digitalinnovation.budgeting.domain;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

/**
 * Interface de repositório no Domain Layer (puro).
 * NÃO depende de Spring ou JPA — apenas contrato.
 */
public interface TransactionRepository {
    
    /**
     * Salva uma transação.
     */
    Transaction save(Transaction transaction);
    
    /**
     * Busca uma transação por ID.
     */
    Optional<Transaction> findById(UUID id);
    
    /**
     * Lista todas as transações.
     */
    List<Transaction> findAll();
    
    /**
     * Deleta uma transação por ID.
     */
    void delete(UUID id);
}
```

**Por quê interface no domínio?**
- Abstração = domínio não depende de JPA
- Fácil de mockar para testes
- Permite trocar implementação sem afetar domínio

---

### Critério de Aceite

- [x] Archivos compila sem dependências externas
- [x] TransactionType enum com INCOME e EXPENSE
- [x] Transaction record com UUID, description, amount, type, createdAt
- [x] Validação `isValid()` implementada
- [x] TransactionRepository interface com métodos CRUD
- [x] `./mvnw clean compile` retorna BUILD SUCCESS

### Teste

```bash
cd budgeting

# Compilar
./mvnw clean compile

# Esperado: BUILD SUCCESS ✅

# Verificar que arquivos foram criados
ls -la src/main/java/one/digitalinnovation/budgeting/domain/
```

---

## ➡️ Etapa 3 — Camada de Aplicação (Use Cases)

**Status:** ⏳ PLANEJADA  
**Estimativa:** 1-2 horas  
**Dependências:** Etapa 1, 2

### Objetivo

Implementar orquestradores de negócio (use cases) que coordenam domínio e infraestrutura.

### Arquivos a criar

#### 1. `budgeting/src/main/java/one/digitalinnovation/budgeting/application/CreateTransactionUseCase.java`

```java
package one.digitalinnovation.budgeting.application;

import one.digitalinnovation.budgeting.domain.*;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CreateTransactionUseCase {
    
    private final TransactionRepository repository;
    
    /**
     * Injeção de dependência por constructor.
     * Garante imutabilidade e facilita testes.
     */
    public CreateTransactionUseCase(TransactionRepository repository) {
        this.repository = repository;
    }
    
    /**
     * Executa a ação de criar uma transação.
     * 
     * @param description Descrição da transação
     * @param amount Valor (deve ser > 0)
     * @param type INCOME ou EXPENSE
     * @return Transaction criada e persistida
     * @throws IllegalArgumentException Se transação inválida
     */
    public Transaction execute(String description, BigDecimal amount, TransactionType type) {
        // 1. Criar objeto de domínio
        var transaction = new Transaction(
            UUID.randomUUID(),          // ID único
            description,
            amount,
            type,
            LocalDateTime.now()         // Timestamp automático
        );
        
        // 2. Validar regras de negócio
        if (!transaction.isValid()) {
            throw new IllegalArgumentException(
                "Invalid transaction: description and amount (>0) are required"
            );
        }
        
        // 3. Persistir via repositório (interface)
        return repository.save(transaction);
    }
}
```

**Key Points:**
- `@Service` — Bean Spring automaticamente injetável
- Constructor injection — Dependência explícita e testável
- Validação de domínio — Usa `transaction.isValid()`
- Exception — Lança `IllegalArgumentException` (negócio, não framework)

---

#### 2. `budgeting/src/main/java/one/digitalinnovation/budgeting/application/GetTransactionsUseCase.java`

```java
package one.digitalinnovation.budgeting.application;

import one.digitalinnovation.budgeting.domain.*;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GetTransactionsUseCase {
    
    private final TransactionRepository repository;
    
    public GetTransactionsUseCase(TransactionRepository repository) {
        this.repository = repository;
    }
    
    /**
     * Retorna todas as transações.
     */
    public List<Transaction> execute() {
        return repository.findAll();
    }
    
    /**
     * Retorna uma transação por ID.
     */
    public Optional<Transaction> executeById(UUID id) {
        return repository.findById(id);
    }
}
```

---

### Critério de Aceite

- [x] CreateTransactionUseCase criado com `execute()` method
- [x] GetTransactionsUseCase criado com `execute()` e `executeById()`
- [x] Injeção de dependência via constructor
- [x] Validação de domínio ativada (isValid())
- [x] Exceções de negócio claras (IllegalArgumentException)
- [x] `./mvnw clean compile` retorna BUILD SUCCESS

### Teste

```bash
cd budgeting
./mvnw clean compile

# Esperado: BUILD SUCCESS ✅
```

---

## ➡️ Etapa 4 — Persistência (JPA Repository)

**Status:** ⏳ PLANEJADA  
**Estimativa:** 1-2 horas  
**Dependências:** Etapa 1, 2, 3

### Objetivo

Adaptar interface de repositório de domínio para implementação JPA.

### Arquivo a criar

#### `budgeting/src/main/java/one/digitalinnovation/budgeting/infrastructure/persistence/TransactionJpaRepository.java`

```java
package one.digitalinnovation.budgeting.infrastructure.persistence;

import one.digitalinnovation.budgeting.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

/**
 * Implementação de TransactionRepository usando Spring Data JPA.
 * Resida na camada Infrastructure.
 */
@Repository
public class TransactionJpaRepository implements TransactionRepository {
    
    private final SpringDataJpaRepository jpa;
    
    public TransactionJpaRepository(SpringDataJpaRepository jpa) {
        this.jpa = jpa;
    }
    
    @Override
    public Transaction save(Transaction transaction) {
        return jpa.save(transaction);
    }
    
    @Override
    public Optional<Transaction> findById(UUID id) {
        return jpa.findById(id);
    }
    
    @Override
    public List<Transaction> findAll() {
        return jpa.findAll();
    }
    
    @Override
    public void delete(UUID id) {
        jpa.deleteById(id);
    }
    
    /**
     * Interface interna do Spring Data JPA.
     * Abstrai detalhes de implementação.
     */
    public interface SpringDataJpaRepository extends JpaRepository<Transaction, UUID> {
    }
}
```

---

### Critério de Aceite

- [x] Implementa interface TransactionRepository
- [x] Usa Spring Data JPA
- [x] H2 cria tabela `transaction` automaticamente
- [x] Dados persistem na memória (H2 em dev)
- [x] `./mvnw spring-boot:run` sem erros
- [x] H2 Console mostra tabela criada

### Teste

```bash
cd budgeting
./mvnw spring-boot:run

# Em outro terminal:
curl http://localhost:8080/transactions

# Acesse H2 Console
# URL: http://localhost:8080/h2-console
# JDBC URL: jdbc:h2:mem:budgeting
# User: sa
# Password: (vazio)
# Query: SELECT * FROM transaction;
```

---

## ➡️ Etapa 5 — REST CRUD

**Status:** ⏳ PLANEJADA  
**Estimativa:** 2 horas  
**Dependências:** Etapa 1-4

### Objetivo

Expor endpoints REST para criar, listar e buscar transações.

### Arquivos a criar

#### 1. DTOs (Data Transfer Objects)

`budgeting/src/main/java/one/digitalinnovation/budgeting/infrastructure/web/dto/CreateTransactionRequest.java`

```java
package one.digitalinnovation.budgeting.infrastructure.web.dto;

import one.digitalinnovation.budgeting.domain.TransactionType;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record CreateTransactionRequest(
    @NotBlank(message = "Description is required")
    String description,
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    BigDecimal amount,
    
    @NotNull(message = "Type is required")
    TransactionType type
) {}
```

`budgeting/src/main/java/one/digitalinnovation/budgeting/infrastructure/web/dto/TransactionDTO.java`

```java
package one.digitalinnovation.budgeting.infrastructure.web.dto;

import one.digitalinnovation.budgeting.domain.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionDTO(
    UUID id,
    String description,
    BigDecimal amount,
    TransactionType type,
    LocalDateTime createdAt
) {}
```

---

#### 2. Controller

`budgeting/src/main/java/one/digitalinnovation/budgeting/infrastructure/web/TransactionController.java`

```java
package one.digitalinnovation.budgeting.infrastructure.web;

import one.digitalinnovation.budgeting.application.*;
import one.digitalinnovation.budgeting.infrastructure.web.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/transactions")
@Validated
public class TransactionController {
    
    private final CreateTransactionUseCase createUseCase;
    private final GetTransactionsUseCase getUseCase;
    
    public TransactionController(
        CreateTransactionUseCase createUseCase,
        GetTransactionsUseCase getUseCase
    ) {
        this.createUseCase = createUseCase;
        this.getUseCase = getUseCase;
    }
    
    /**
     * POST /transactions
     * Cria uma nova transação.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionDTO create(@RequestBody @Valid CreateTransactionRequest request) {
        var transaction = createUseCase.execute(
            request.description(),
            request.amount(),
            request.type()
        );
        return toDTO(transaction);
    }
    
    /**
     * GET /transactions
     * Lista todas as transações.
     */
    @GetMapping
    public List<TransactionDTO> listAll() {
        return getUseCase.execute().stream()
            .map(this::toDTO)
            .toList();
    }
    
    /**
     * GET /transactions/{id}
     * Busca uma transação por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TransactionDTO> getById(@PathVariable UUID id) {
        return getUseCase.executeById(id)
            .map(t -> ResponseEntity.ok(toDTO(t)))
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Converte Transaction (domínio) para DTO (API).
     */
    private TransactionDTO toDTO(Transaction transaction) {
        return new TransactionDTO(
            transaction.id(),
            transaction.description(),
            transaction.amount(),
            transaction.type(),
            transaction.createdAt()
        );
    }
}
```

---

### Critério de Aceite

- [x] POST /transactions cria transação (201 Created)
- [x] GET /transactions lista todas (200 OK)
- [x] GET /transactions/{id} busca por ID (200 ou 404)
- [x] Validação com @Valid funciona
- [x] Swagger documenta endpoints
- [x] DTOs criados

### Teste

```bash
# Terminal 1: rodar servidor
cd budgeting
./mvnw spring-boot:run

# Terminal 2: testar endpoints

# 1. Criar transação
curl -X POST http://localhost:8080/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Almoço",
    "amount": 50.00,
    "type": "EXPENSE"
  }'

# 2. Listar transações
curl http://localhost:8080/transactions

# 3. Acessar Swagger
# http://localhost:8080/swagger-ui.html
```

---

## ➡️ Etapas 6-10 (Continuação)

As próximas etapas envolvem Spring AI (que aguarda versão estável) e refinamentos finais.

### Próximas Etapas

- **Etapa 6:** Tool Calling com Spring AI
- **Etapa 7:** Configuração ChatClient
- **Etapa 8:** Endpoint de Voz (/ai/voice)
- **Etapa 9:** Tratamento de Erros Robusto
- **Etapa 10:** Documentação Final

---

## 📊 Progresso

```
Etapa 1: [████████████████████] 100% ✅ CONCLUÍDA
Etapa 2: [░░░░░░░░░░░░░░░░░░░░]   0% ⏳ PRÓXIMA
Etapa 3: [░░░░░░░░░░░░░░░░░░░░]   0%
Etapa 4: [░░░░░░░░░░░░░░░░░░░░]   0%
Etapa 5: [░░░░░░░░░░░░░░░░░░░░]   0%
Etapa 6: [░░░░░░░░░░░░░░░░░░░░]   0%
Etapa 7: [░░░░░░░░░░░░░░░░░░░░]   0%
Etapa 8: [░░░░░░░░░░░░░░░░░░░░]   0%
Etapa 9: [░░░░░░░░░░░░░░░░░░░░]   0%
Etapa 10:[░░░░░░░░░░░░░░░░░░░░]   0%

TOTAL: 10% ✅ | 90% ⏳
```

---

## 📝 Notas Importantes

### Spring AI

Spring AI 2.0.0 ainda não foi lançado no Maven Central. Quando for:

```xml
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-openai</artifactId>
    <version>2.0.0</version>
</dependency>
```

Configure também:

```bash
export OPENAI_API_KEY="sk-..."
```

### H2 Console

Apenas usar em desenvolvimento. Para produção, use PostgreSQL ou MySQL.

### Records vs Classes

Java 14+ suporta records. São perfeitos para DTOs e value objects imutáveis.

---

**Última Atualização:** 12 de outubro de 2026  
**Status:** Em Desenvolvimento ⚙️
