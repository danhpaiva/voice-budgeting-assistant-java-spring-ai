# 📖 Guia de Desenvolvimento - Voice Budgeting Assistant

Documentação técnica para desenvolvedores que desejam estender ou manter o projeto.

---

## 🏛️ Arquitetura DDD (Domain-Driven Design)

### Princípios Implementados

1. **Domain Layer (Puro)** - Sem dependências Spring
   - Contém lógica de negócio pura
   - Entidades e ValueObjects
   - Interfaces de repositórios (sem implementação)

2. **Application Layer** - Use Cases
   - Orquestra o domínio
   - Não depende de frameworks
   - Fácil de testar

3. **Infrastructure Layer** - Implementações
   - Spring Data JPA
   - Controllers REST
   - Configurações Spring

### Exemplo de Fluxo

```
Request HTTP (POST /transactions)
    ↓
TransactionController.create()
    ↓
CreateTransactionUseCase.execute()
    ↓
TransactionRepository.save() [interface do domain]
    ↓
SpringDataTransactionRepository [implementação adapter]
    ↓
TransactionJpaRepository [Spring Data]
    ↓
H2 Database
    ↓
Response JSON
```

---

## 🔄 Padrões Implementados

### 1. **Adapter Pattern** (Repository)

**Problema:** Domain layer não deve conhecer Spring Data

**Solução:** Duas interfaces de repositório

```java
// Domain (pura)
public interface TransactionRepository {
    Transaction save(Transaction transaction);
    List<Transaction> findAll();
}

// Infrastructure (adapter)
@Component
public class SpringDataTransactionRepository implements TransactionRepository {
    private final TransactionJpaRepository jpaRepo;
    
    @Override
    public Transaction save(Transaction transaction) {
        return jpaRepo.save(transaction);
    }
}

// Infrastructure (Spring Data)
@Repository
public interface TransactionJpaRepository extends JpaRepository<Transaction, UUID> {
}
```

### 2. **Constructor Injection**

**Benefícios:**
- Imutabilidade
- Testabilidade
- Explícito

```java
@Component
public class BudgetingTools {
    private final CreateTransactionUseCase createUseCase;
    private final GetTransactionsUseCase getUseCase;
    
    public BudgetingTools(CreateTransactionUseCase createUseCase,
                         GetTransactionsUseCase getUseCase) {
        this.createUseCase = createUseCase;
        this.getUseCase = getUseCase;
    }
}
```

### 3. **Record com JPA**

**Vantagem:** Imutabilidade + menos boilerplate

```java
@Entity
@Table(name = "transactions")
public record Transaction(
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    UUID id,
    
    String description,
    BigDecimal amount,
    
    @Enumerated(EnumType.STRING)
    TransactionType type,
    
    @CreationTimestamp
    LocalDateTime createdAt
) {
}
```

---

## 📡 REST API Design

### Convenções

1. **Recursos (Nouns) - NÃO Ações (Verbs)**
   ```
   ✅ POST /transactions       (criar)
   ✅ GET /transactions        (listar)
   ❌ POST /create-transaction (errado)
   ```

2. **Status HTTP Apropriados**
   ```
   201 Created    → POST bem-sucedido
   200 OK         → GET, PUT bem-sucedido
   400 Bad Request    → Validação falhou
   404 Not Found      → Recurso não existe
   500 Internal Error → Erro do servidor
   ```

3. **DTOs para Validação**
   ```java
   public class CreateTransactionRequest {
       @NotBlank(message = "Description is required")
       private String description;
       
       @DecimalMin("0.01")
       private BigDecimal amount;
       
       @NotNull
       private TransactionType type;
   }
   ```

---

## 🤖 Spring AI Tool Calling

### Como Funciona

1. **Registrar Tools**
   ```java
   @Bean
   public ChatClient chatClient(OpenAiChatModel chatModel) {
       return ChatClient.create(chatModel)
           .defaultSystem("System prompt com instruções")
           .build();
   }
   ```

2. **Expor Métodos Públicos**
   ```java
   @Component
   public class BudgetingTools {
       // Todos os métodos públicos ficam disponíveis
       public Transaction createTransaction(String desc, BigDecimal amt, TransactionType type) {
           return createUseCase.execute(desc, amt, type);
       }
   }
   ```

3. **Modelo Chama Automaticamente**
   ```
   Usuário: "Qual é meu saldo?"
       ↓
   IA detecta: precisa chamar calculateBalance()
       ↓
   IA chama: budgetingTools.calculateBalance()
       ↓
   Retorna: BigDecimal(4925.00)
       ↓
   IA responde: "Seu saldo é R$4925,00"
   ```

### Limitações

- Spring AI 2.0.1 requer modelo com suporte a function calling
- Nem todos os modelos suportam (ex: versões antigas)
- Refletion automática - sem @Tool decorator necessário

---

## 🛡️ Error Handling

### GlobalExceptionHandler

**Intercepta** todas as exceções e converte em respostas padronizadas.

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map> handleNotFound(ResourceNotFoundException ex) {
        // Retorna 404 com mensagem padronizada
    }
}
```

### Exceções Customizadas

```java
// Uso
throw new BusinessException("Amount must be positive");

// Resultado
{
  "status": 400,
  "error": "Business Error",
  "message": "Amount must be positive",
  "timestamp": "2026-10-12T16:00:00"
}
```

---

## 🧪 Testing (Futuro)

### Estrutura Recomendada

```
src/test/java/one/digitalinnovation/budgeting/
├── domain/
│   └── TransactionTest.java
├── application/
│   ├── CreateTransactionUseCaseTest.java
│   └── GetTransactionsUseCaseTest.java
├── infrastructure/
│   ├── persistence/
│   │   └── SpringDataTransactionRepositoryTest.java
│   └── web/
│       ├── TransactionControllerTest.java
│       └── AiVoiceControllerTest.java
```

### Unit Test Example

```java
class CreateTransactionUseCaseTest {
    private CreateTransactionUseCase useCase;
    private TransactionRepository repository;
    
    @BeforeEach
    void setUp() {
        repository = mock(TransactionRepository.class);
        useCase = new CreateTransactionUseCase(repository);
    }
    
    @Test
    void shouldCreateTransaction() {
        // Given
        Transaction expected = new Transaction(...);
        when(repository.save(any())).thenReturn(expected);
        
        // When
        Transaction result = useCase.execute("Almoço", BigDecimal.valueOf(50), EXPENSE);
        
        // Then
        assertThat(result).isEqualTo(expected);
        verify(repository).save(any());
    }
}
```

---

## 🚀 Deploy

### Docker (Recomendado)

```dockerfile
FROM maven:3.9-eclipse-temurin-25 as builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:25-jdk
WORKDIR /app
COPY --from=builder /app/target/budgeting-0.0.1-SNAPSHOT.jar app.jar
ENV OPENAI_API_KEY=${OPENAI_API_KEY}
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
```

```bash
# Build
docker build -t budgeting:latest .

# Run
docker run -e OPENAI_API_KEY="sk-..." -p 8080:8080 budgeting:latest
```

### Variáveis de Ambiente (Produção)

```bash
OPENAI_API_KEY=sk-your-key
DATABASE_URL=jdbc:mysql://localhost:3306/budgeting
DATABASE_USER=root
DATABASE_PASSWORD=password
SPRING_PROFILES_ACTIVE=prod
```

---

## 📊 Fluxo de Voz Completo (Futuro - Etapa 8+)

### Atual (Placeholder)
```
POST /ai/voice
    ↓
Retorna: {"status": "not_implemented"}
```

### Implementação Futura

```
1. TRANSCRIÇÃO (Whisper)
   POST /ai/voice (audio.mp3)
       ↓
   Whisper transcreve → "Qual é meu saldo?"

2. PROCESSAMENTO (ChatClient)
   ChatClient.prompt(text).call()
       ↓
   IA chama budgetingTools.calculateBalance()
       ↓
   Retorna resultado

3. SÍNTESE (TTS)
   OpenAI TTS gera áudio
       ↓
   Retorna: audio.mp3 binário

4. RESPOSTA
   Response { audio: "base64encoded..." }
```

### Exemplo de Código (Futuro)

```java
@PostMapping("/voice")
public ResponseEntity<byte[]> processVoice(@RequestParam MultipartFile audio) {
    // 1. Transcrever
    String text = transcriptionService.transcribe(audio);
    
    // 2. Processar com IA
    String response = chatModel.call(text);
    
    // 3. Sintetizar
    byte[] audioResponse = ttsService.speak(response);
    
    // 4. Retornar
    return ResponseEntity.ok()
        .header("Content-Type", "audio/mpeg")
        .body(audioResponse);
}
```

---

## 📈 Performance

### Otimizações Implementadas

1. **H2 em Memória** - Acesso rápido (não usar em produção)
2. **Constructor Injection** - Sem reflexão excessiva
3. **Record Imutável** - Sem overhead de getters/setters

### Otimizações Futuras

1. **Cache** (Redis)
   ```java
   @Cacheable("transactions")
   List<Transaction> findAll()
   ```

2. **Pagination**
   ```java
   Page<Transaction> findAll(Pageable pageable);
   ```

3. **Batch Operations**
   ```java
   List<Transaction> saveAll(List<Transaction> transactions);
   ```

---

## 🔐 Segurança Recomendada

### Autenticação (Spring Security)

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults()));
        return http.build();
    }
}
```

### Validação de Input

```java
@PostMapping
public Transaction create(@Valid @RequestBody CreateTransactionRequest req) {
    // @Valid garante validações de @NotNull, @NotBlank, etc
}
```

### Auditoria

```java
@Entity
@Audited
public record Transaction(...) {
    @CreatedBy
    String createdBy;
    
    @CreationTimestamp
    LocalDateTime createdAt;
    
    @LastModifiedBy
    String lastModifiedBy;
}
```

---

## 📚 Referências

- [Spring Framework Documentation](https://spring.io/projects/spring-framework)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Spring AI](https://spring.io/projects/spring-ai)
- [OpenAI API Reference](https://platform.openai.com/docs)
- [Domain-Driven Design - Evans](https://www.domainlanguage.com/ddd/)

---

## 🤝 Contributing

1. Fork o projeto
2. Crie branch para sua feature: `git checkout -b feature/nova-funcionalidade`
3. Commit suas mudanças: `git commit -m "Add nova funcionalidade"`
4. Push para a branch: `git push origin feature/nova-funcionalidade`
5. Abra Pull Request

---

## ✅ Checklist para Novas Features

- [ ] Criar classe no domain layer
- [ ] Criar use case na application layer
- [ ] Implementar adapter na infrastructure layer
- [ ] Adicionar controller REST
- [ ] Escrever testes unitários
- [ ] Atualizar documentação
- [ ] Testar manualmente via Swagger
- [ ] Validar error handling

---

## 📞 Suporte Desenvolvimento

**Problemas ao compilar:**
```bash
./mvnw clean compile
./mvnw spring-boot:run
```

**Verificar dependências:**
```bash
./mvnw dependency:tree
```

**Executar testes:**
```bash
./mvnw test
```

---

**Última atualização:** 2026-10-12
**Versão:** 1.0 (Etapa 10 - Completa)
