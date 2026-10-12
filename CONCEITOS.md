# 📚 Conceitos de Domain-Driven Design (DDD)

![DDD](https://img.shields.io/badge/Arquitetura-DDD-blue?style=flat-square)
![Clean%20Code](https://img.shields.io/badge/Code-Clean%20Architecture-green?style=flat-square)

Este documento explica os conceitos fundamentais de **Domain-Driven Design (DDD)** que serão aplicados no projeto Voice Budgeting Assistant.

---

## 📖 Índice

- [O que é DDD?](#o-que-é-ddd)
- [As 3 Camadas de Arquitetura](#as-3-camadas-de-arquitetura)
- [Entidades vs Value Objects](#entidades-vs-value-objects)
- [Repositórios](#repositórios)
- [Use Cases (Application Services)](#use-cases-application-services)
- [Injeção de Dependência](#injeção-de-dependência)
- [Exemplo Prático: Transaction](#exemplo-prático-transaction)
- [Comparação: Classes vs Records](#comparação-classes-vs-records)
- [Identificadores Fortemente Tipados](#identificadores-fortemente-tipados)

---

## 🎯 O que é DDD?

**Domain-Driven Design (DDD)** é uma filosofia de design de software que coloca o **domínio do negócio** no centro do design.

### Princípios Principais

1. **Domínio em Primeiro Lugar** — O código reflete o negócio
2. **Linguagem Ubíqua** — Mesmo vocabulário entre código e negócio
3. **Separação de Responsabilidades** — Cada camada tem um papel claro
4. **Independência** — Lógica de negócio não depende de frameworks

### Benefícios

✅ Código mais limpo e legível  
✅ Fácil de testar e manutenção  
✅ Lógica de negócio independente de infraestrutura  
✅ Facilita comunicação com stakeholders  
✅ Escalabilidade e evolução

---

## 🏗️ As 3 Camadas de Arquitetura

### 1. **Domain Layer** (Camada de Domínio)

Contém a **lógica pura de negócio**, completamente independente de qualquer framework.

```java
// Entidades, Value Objects, Repositórios (interfaces)
// SEM dependências do Spring ou outras libs externas
```

**Responsabilidades:**
- Definir entidades e value objects
- Regras de negócio (validações, cálculos)
- Contrato de repositório (interface)

**Exemplo:**
```java
public record Transaction(
    UUID id,
    String description,
    BigDecimal amount,
    TransactionType type,
    LocalDateTime createdAt
) {
    // Validações de negócio puro
    public boolean isValid() {
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }
}
```

**Vantagem:** Pode ser testado SEM Spring, SEM banco de dados:
```java
@Test
void shouldValidateTransaction() {
    var transaction = new Transaction(...);
    assertTrue(transaction.isValid());
}
```

---

### 2. **Application Layer** (Camada de Aplicação)

Camada **orquestradora** que implementa os **use cases** (casos de uso) do sistema.

```java
// Use Cases, Application Services
// Dependências do Domain (interfaces, não implementações)
```

**Responsabilidades:**
- Coordenar fluxo de negócio
- Usar repositórios e outras dependências
- Retornar dados de negócio (não DTOs)

**Exemplo:**
```java
@Service
public class CreateTransactionUseCase {
    
    private final TransactionRepository repository;
    
    public CreateTransactionUseCase(TransactionRepository repository) {
        this.repository = repository;
    }
    
    public Transaction execute(String description, BigDecimal amount, TransactionType type) {
        // 1. Criar transação
        var transaction = new Transaction(
            UUID.randomUUID(),
            description,
            amount,
            type,
            LocalDateTime.now()
        );
        
        // 2. Validar
        if (!transaction.isValid()) {
            throw new InvalidTransactionException("Amount must be > 0");
        }
        
        // 3. Persistir (via interface)
        return repository.save(transaction);
    }
}
```

**Vantagem:** Testável com mock do repositório:
```java
@Test
void shouldCreateTransaction() {
    var mockRepo = mock(TransactionRepository.class);
    var useCase = new CreateTransactionUseCase(mockRepo);
    
    var result = useCase.execute("Lunch", BigDecimal.valueOf(50), EXPENSE);
    
    verify(mockRepo).save(any());
}
```

---

### 3. **Infrastructure Layer** (Camada de Infraestrutura)

Implementações concretas e **adaptadores** para o mundo externo.

```java
// Repositórios (implementação JPA), Controllers, Configs
// Dependências do Spring, frameworks, etc
```

**Responsabilidades:**
- Implementar repositórios com JPA
- HTTP Controllers (REST)
- Configurações do Spring
- Adaptadores para APIs externas (IA, etc)

**Exemplo — Repositório JPA:**
```java
@Repository
public class TransactionJpaRepository 
    implements TransactionRepository {
    
    @Autowired
    private JpaRepository<TransactionEntity, UUID> jpa;
    
    @Override
    public Transaction save(Transaction transaction) {
        var entity = new TransactionEntity(...);
        var saved = jpa.save(entity);
        return toDomain(saved);
    }
}
```

**Exemplo — Controller:**
```java
@RestController
@RequestMapping("/transactions")
public class TransactionController {
    
    @Autowired
    private CreateTransactionUseCase createUseCase;
    
    @PostMapping
    public ResponseEntity<TransactionDTO> create(@RequestBody CreateTransactionRequest req) {
        var result = createUseCase.execute(req.description(), req.amount(), req.type());
        return ResponseEntity.status(201).body(toDTO(result));
    }
}
```

---

## 📊 Diagrama das 3 Camadas

```
┌─────────────────────────────────────────────────────────┐
│              Infrastructure Layer                        │
│  (Controllers, JPA Repos, Configs, Adapters)           │
│  - Depende de: Spring, JPA, HTTP, etc                  │
└────────────────────┬────────────────────────────────────┘
                     │ (depende de)
                     ↓
┌─────────────────────────────────────────────────────────┐
│              Application Layer                           │
│  (Use Cases, Application Services)                       │
│  - Depende de: Domain interfaces, NOT implementations   │
└────────────────────┬────────────────────────────────────┘
                     │ (depende de)
                     ↓
┌─────────────────────────────────────────────────────────┐
│              Domain Layer                                │
│  (Entidades, Value Objects, Repository interfaces)      │
│  - Depende de: NADA (código puro de negócio)           │
└─────────────────────────────────────────────────────────┘
```

**Importante:** Fluxo é de cima para baixo. O Domain NUNCA depende de Infrastructure.

---

## 🆚 Entidades vs Value Objects

### Entidades

**Definição:** Objetos que têm **identidade única** (ID). Continuam sendo os mesmos mesmo se seus atributos mudem.

```java
// Transação com ID único
public record Transaction(
    UUID id,              // Identidade única
    String description,   // Pode mudar
    BigDecimal amount,    // Pode mudar
    TransactionType type,
    LocalDateTime createdAt
) {}

// Duas transações são iguais se têm o MESMO ID
Transaction t1 = new Transaction(UUID1, "Lunch", BigDecimal.50, EXPENSE, now);
Transaction t2 = new Transaction(UUID1, "Dinner", BigDecimal.75, EXPENSE, now);

t1.equals(t2) // true (mesmo ID)
```

**Ciclo de vida:** Criação → Modificação → Deleção

---

### Value Objects

**Definição:** Objetos que representam um **valor** sem identidade. São iguais se têm os mesmos atributos.

```java
// Valor monetário (sem ID)
public record Money(BigDecimal amount, Currency currency) {
    public Money(BigDecimal amount) {
        this(amount, Currency.USD);
    }
}

// Duas instâncias são iguais se têm mesmo amount e currency
Money m1 = new Money(BigDecimal.valueOf(50), USD);
Money m2 = new Money(BigDecimal.valueOf(50), USD);

m1.equals(m2) // true (mesmo valor)
```

**Características:**
- Imutáveis (records são perfeitos)
- Sem ID
- Reutilizáveis
- Fáceis de testar

---

## 🏛️ Repositórios

### O Padrão Repository

Um repositório é uma **abstração** entre a lógica de negócio e a persistência.

```java
// Interface no Domain Layer (puro)
public interface TransactionRepository {
    Transaction save(Transaction transaction);
    Optional<Transaction> findById(UUID id);
    List<Transaction> findAll();
    void delete(UUID id);
}
```

```java
// Implementação na Infrastructure Layer
@Repository
public class TransactionJpaRepository implements TransactionRepository {
    
    @Autowired
    private JpaRepository<TransactionEntity, UUID> jpa;
    
    @Override
    public Transaction save(Transaction transaction) {
        var entity = toEntity(transaction);
        var saved = jpa.save(entity);
        return toDomain(saved);
    }
    
    // ...
}
```

### Benefícios

✅ **Desacoplamento:** Domínio não sabe sobre JPA  
✅ **Testabilidade:** Pode mockar repositório  
✅ **Flexibilidade:** Trocar implementação sem afetar domínio  
✅ **Clareza:** Contrato explícito entre camadas

---

## 🎯 Use Cases (Application Services)

Um **use case** representa uma **ação de negócio** que o usuário quer fazer.

### Estrutura

```java
@Service
public class CreateTransactionUseCase {
    
    private final TransactionRepository repository;
    
    // 1. Injetar dependências (via constructor)
    public CreateTransactionUseCase(TransactionRepository repository) {
        this.repository = repository;
    }
    
    // 2. Método execute com entrada clara
    public Transaction execute(String description, BigDecimal amount, TransactionType type) {
        // 3. Lógica de negócio
        var transaction = new Transaction(
            UUID.randomUUID(),
            description,
            amount,
            type,
            LocalDateTime.now()
        );
        
        // 4. Validação
        validate(transaction);
        
        // 5. Persistência (via interface)
        return repository.save(transaction);
    }
    
    private void validate(Transaction transaction) {
        if (!transaction.isValid()) {
            throw new InvalidTransactionException("Invalid transaction");
        }
    }
}
```

### Characteristics

- Um use case = Uma ação
- Método `execute()` é claro e testável
- Recebe dados primitivos (String, BigDecimal, enums)
- Retorna objeto de domínio
- Lança exceções de negócio (não genéricas)

---

## 💉 Injeção de Dependência

### Por Quê?

Torna código testável e desacoplado.

### ✅ Com Injeção (Use Cases)

```java
public class CreateTransactionUseCase {
    
    private final TransactionRepository repository;
    
    // Constructor injection — claro e imutável
    public CreateTransactionUseCase(TransactionRepository repository) {
        this.repository = repository;
    }
    
    public Transaction execute(...) { ... }
}

// Teste com mock
@Test
void shouldCreateTransaction() {
    var mockRepo = mock(TransactionRepository.class);
    var useCase = new CreateTransactionUseCase(mockRepo);
    
    useCase.execute("Lunch", 50, EXPENSE);
    
    verify(mockRepo).save(any());
}
```

### ❌ Sem Injeção (Difícil de testar)

```java
public class CreateTransactionUseCase {
    
    // Acoplamento hard-coded — não consegue mockar
    private final TransactionRepository repository = 
        new TransactionJpaRepository();
    
    public Transaction execute(...) { ... }
}

// Impossível testar sem banco de dados real!
```

---

## 📝 Exemplo Prático: Transaction

Aqui está como ficará a entidade `Transaction` no projeto:

### Step 1: Enum (Value Object)

```java
// domain/TransactionType.java
public enum TransactionType {
    INCOME,    // Renda
    EXPENSE    // Despesa
}
```

### Step 2: Entity (Record)

```java
// domain/Transaction.java
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
    
    // Validações de domínio (puro)
    public boolean isValid() {
        return description != null && !description.isBlank() &&
               amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }
}
```

### Step 3: Repository Interface

```java
// domain/TransactionRepository.java
public interface TransactionRepository {
    Transaction save(Transaction transaction);
    Optional<Transaction> findById(UUID id);
    List<Transaction> findAll();
}
```

### Step 4: Use Case

```java
// application/CreateTransactionUseCase.java
@Service
public class CreateTransactionUseCase {
    
    private final TransactionRepository repository;
    
    public CreateTransactionUseCase(TransactionRepository repository) {
        this.repository = repository;
    }
    
    public Transaction execute(String description, BigDecimal amount, TransactionType type) {
        var transaction = new Transaction(
            UUID.randomUUID(),
            description,
            amount,
            type,
            LocalDateTime.now()
        );
        
        if (!transaction.isValid()) {
            throw new IllegalArgumentException("Invalid transaction");
        }
        
        return repository.save(transaction);
    }
}
```

### Step 5: JPA Repository

```java
// infrastructure/persistence/TransactionJpaRepository.java
@Repository
public class TransactionJpaRepository implements TransactionRepository {
    
    @Autowired
    private JpaRepository<Transaction, UUID> jpa;
    
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
}
```

### Step 6: Controller

```java
// infrastructure/web/TransactionController.java
@RestController
@RequestMapping("/transactions")
public class TransactionController {
    
    private final CreateTransactionUseCase createUseCase;
    
    public TransactionController(CreateTransactionUseCase createUseCase) {
        this.createUseCase = createUseCase;
    }
    
    @PostMapping
    public ResponseEntity<Transaction> create(
        @RequestBody CreateTransactionRequest request
    ) {
        var transaction = createUseCase.execute(
            request.description(),
            request.amount(),
            request.type()
        );
        return ResponseEntity.status(201).body(transaction);
    }
}
```

---

## 🆚 Comparação: Classes vs Records

### Records (Preferido para Value Objects)

```java
// Imutável, conciso, perfeito para dados
public record Transaction(
    UUID id,
    String description,
    BigDecimal amount,
    TransactionType type,
    LocalDateTime createdAt
) {}

// Automaticamente gera:
// - Constructor
// - getters (sem "get" prefix)
// - equals() e hashCode()
// - toString()
```

### Classes Tradicionais (Mais verboso)

```java
public class Transaction {
    private final UUID id;
    private final String description;
    private final BigDecimal amount;
    private final TransactionType type;
    private final LocalDateTime createdAt;
    
    public Transaction(UUID id, String description, ...) {
        this.id = id;
        this.description = description;
        // ...
    }
    
    public UUID id() { return id; }
    public String description() { return description; }
    
    @Override
    public boolean equals(Object o) { ... }
    
    @Override
    public int hashCode() { ... }
    
    @Override
    public String toString() { ... }
}
```

**Conclusão:** Records são muito mais limpas! Use para value objects e entidades simples.

---

## 🆔 Identificadores Fortemente Tipados

### Problema: Confundir IDs

```java
// ❌ Sem tipagem forte — fácil confundir
transferMoney(userId, accountId, amount); // Qual é qual?
transferMoney("123", "456", 100);         // Impossível saber

// Pode passar errado sem compilador reclamar
transferMoney(accountId, userId, 100);    // Bug!
```

### Solução: Identificadores Tipados

```java
// ✅ Com tipagem forte — compilador ajuda
public record UserId(UUID value) {}
public record AccountId(UUID value) {}

transferMoney(new UserId(userId), new AccountId(accountId), amount);

// Compilador avisa se tentar passar errado
transferMoney(accountId, userId, 100);  // ❌ Erro de compilação!
```

### Implementação

```java
// domain/TransactionId.java
public record TransactionId(UUID value) {
    public TransactionId {
        if (value == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
    }
    
    public static TransactionId generate() {
        return new TransactionId(UUID.randomUUID());
    }
}
```

---

## 🧪 Testando com DDD

### Teste Unitário (Sem Spring)

```java
public class CreateTransactionUseCaseTest {
    
    private CreateTransactionUseCase useCase;
    private TransactionRepository mockRepository;
    
    @Before
    public void setup() {
        // Mock do repositório
        mockRepository = mock(TransactionRepository.class);
        useCase = new CreateTransactionUseCase(mockRepository);
    }
    
    @Test
    public void shouldCreateValidTransaction() {
        // Given
        String description = "Lunch";
        BigDecimal amount = BigDecimal.valueOf(50);
        TransactionType type = TransactionType.EXPENSE;
        
        // When
        var result = useCase.execute(description, amount, type);
        
        // Then
        assertNotNull(result.id());
        assertEquals(description, result.description());
        assertEquals(amount, result.amount());
        verify(mockRepository, times(1)).save(any());
    }
    
    @Test
    public void shouldRejectInvalidTransaction() {
        // Given: amount negativo
        String description = "Invalid";
        BigDecimal amount = BigDecimal.valueOf(-50);
        TransactionType type = TransactionType.EXPENSE;
        
        // When & Then
        assertThrows(
            IllegalArgumentException.class,
            () -> useCase.execute(description, amount, type)
        );
    }
}
```

### Teste de Integração (Com Spring)

```java
@SpringBootTest
public class TransactionControllerIntegrationTest {
    
    @Autowired
    private MockMvc mvc;
    
    @Autowired
    private TransactionRepository repository;
    
    @Test
    public void shouldCreateTransactionViaRest() throws Exception {
        // Given
        var request = """
            {
                "description": "Lunch",
                "amount": 50.00,
                "type": "EXPENSE"
            }
            """;
        
        // When & Then
        mvc.perform(post("/transactions")
            .contentType(APPLICATION_JSON)
            .content(request))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.description").value("Lunch"));
        
        // Verificar persistência
        assertEquals(1, repository.findAll().size());
    }
}
```

---

## 📚 Referências

### Livros
- **Domain-Driven Design** — Eric Evans
- **Building Microservices** — Sam Newman
- **Clean Architecture** — Robert C. Martin

### Recursos Online
- [Spring Data JPA Reference](https://spring.io/projects/spring-data-jpa)
- [Domain-Driven Design com Spring](https://spring.io/blog/2015/10/28/domain-driven-design-with-spring)
- [Clean Code by Uncle Bob](https://www.youtube.com/results?search_query=clean+code+uncle+bob)

---

## 🎓 Resumo

| Conceito | Objetivo | Exemplo |
|---|---|---|
| **Domain** | Lógica pura de negócio | Transaction, TransactionType |
| **Application** | Orquestradores de use cases | CreateTransactionUseCase |
| **Infrastructure** | Adaptadores e frameworks | TransactionController, JpaRepository |
| **Entity** | Objeto com identidade | Transaction (tem ID) |
| **Value Object** | Objeto com valor | Money, TransactionType |
| **Repository** | Abstração de persistência | TransactionRepository interface |
| **Dependency Injection** | Desacoplamento | Constructor injection |

---

**Próximo:** Continue para o README.md para entender a estrutura do projeto.
