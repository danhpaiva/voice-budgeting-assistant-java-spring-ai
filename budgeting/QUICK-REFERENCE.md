# ⚡ Quick Reference — Voice Budgeting Assistant

Consulta rápida para desenvolvedores.

---

## 🚀 Iniciar Projeto

```bash
# Clonar
git clone <repo>
cd budgeting

# Compilar
./mvnw clean compile

# Executar
./mvnw spring-boot:run

# Acessar
http://localhost:8080/swagger-ui.html
```

---

## 📡 API Endpoints

### Transações

| Método | Endpoint | Status | Descrição |
|--------|----------|--------|-----------|
| POST | `/transactions` | 201 | Criar transação |
| GET | `/transactions` | 200 | Listar todas |
| GET | `/transactions/{id}` | 200/404 | Obter uma |

### Chat IA

| Método | Endpoint | Formato | Status |
|--------|----------|---------|--------|
| POST | `/ai/chat` | JSON | 200/400 |
| POST | `/ai/voice` | Multipart | 200/501 |
| POST | `/ai/transcribe` | Multipart | 200/501 |
| POST | `/ai/speak` | JSON | 200/501 |

---

## 🏗️ Estrutura de Pastas

```
src/main/java/one/digitalinnovation/budgeting/
├── domain/                 ← Lógica pura
│   ├── Transaction.java
│   ├── TransactionType.java
│   └── TransactionRepository.java
├── application/           ← Use cases
│   ├── CreateTransactionUseCase.java
│   └── GetTransactionsUseCase.java
└── infrastructure/        ← Spring
    ├── persistence/
    ├── web/
    ├── ai/
    └── exception/
```

---

## 💾 Criar Transação

### cURL
```bash
curl -X POST http://localhost:8080/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Almoço",
    "amount": 50.00,
    "type": "EXPENSE"
  }'
```

### Python
```python
import requests

resp = requests.post(
    'http://localhost:8080/transactions',
    json={
        'description': 'Almoço',
        'amount': 50.00,
        'type': 'EXPENSE'
    }
)
print(resp.json())
```

### JavaScript
```javascript
fetch('http://localhost:8080/transactions', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    description: 'Almoço',
    amount: 50.00,
    type: 'EXPENSE'
  })
}).then(r => r.json()).then(console.log)
```

---

## 📊 Listar Transações

```bash
curl http://localhost:8080/transactions
```

**Resposta:**
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "description": "Almoço",
    "amount": 50.00,
    "type": "EXPENSE",
    "createdAt": "2026-10-12T16:00:00"
  }
]
```

---

## 💬 Chat com IA

```bash
curl -X POST http://localhost:8080/ai/chat \
  -H "Content-Type: application/json" \
  -d '{"text": "Qual é meu saldo?"}'
```

**Resposta:**
```json
{
  "response": "Seu saldo atual é R$0.00",
  "status": "success"
}
```

---

## ❌ Erros Comuns

### 400 Bad Request
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Text field is required",
  "path": "/ai/chat"
}
```

**Solução:** Verificar campos obrigatórios

### 404 Not Found
```json
{
  "status": 404,
  "error": "Resource Not Found",
  "message": "Transaction not found",
  "path": "/transactions/999"
}
```

**Solução:** Verificar ID da transação

### 500 Internal Server Error
```json
{
  "status": 500,
  "error": "Internal Server Error",
  "message": "Database connection failed",
  "path": "/transactions"
}
```

**Solução:** Verificar logs, banco de dados

---

## 🔧 Configuração

### application.properties
```properties
# Banco
spring.datasource.url=jdbc:h2:mem:budgeting
spring.jpa.hibernate.ddl-auto=create-drop

# OpenAI
spring.ai.openai.api-key=${OPENAI_API_KEY:demo}
spring.ai.openai.chat.model=gpt-3.5-turbo

# Logging
logging.level.one.digitalinnovation.budgeting=DEBUG
```

### Environment Variables
```bash
export OPENAI_API_KEY="sk-..."
```

---

## 🏗️ Adicionar Nova Feature

### 1. Domain
```java
// Novo entity/interface no domain/
public record MyEntity(...) { }
public interface MyRepository { }
```

### 2. Application
```java
// Novo use case em application/
public class MyUseCase {
    public void execute() { }
}
```

### 3. Infrastructure
```java
// Implementação adapter
public class MyRepositoryImpl implements MyRepository { }

// Controller REST
@RestController
@PostMapping("/myendpoint")
public void create(...) { }
```

---

## 📝 Padrões

### Constructor Injection
```java
@Component
public class MyClass {
    private final Dependency dep;
    
    public MyClass(Dependency dep) {
        this.dep = dep;
    }
}
```

### Exception Handling
```java
if (value == null) {
    throw new BusinessException("Value is required");
}
```

### REST Controller
```java
@RestController
@RequestMapping("/endpoint")
public class MyController {
    @PostMapping
    public ResponseEntity<DTO> create(@RequestBody @Valid Request req) {
        return ResponseEntity.status(201).body(...);
    }
}
```

---

## 🧪 Build & Test

```bash
# Compilar
./mvnw clean compile

# Testar
./mvnw test

# Build JAR
./mvnw clean package

# Executar
java -jar target/budgeting-0.0.1-SNAPSHOT.jar
```

---

## 📚 Documentação

| Arquivo | Conteúdo |
|---------|----------|
| README.md | Guia principal |
| GUIA-DESENVOLVIMENTO.md | Para desenvolvedores |
| CHANGELOG.md | Histórico |
| ROADMAP.md | Futuro |
| ETAPA-*.md | Cada etapa |

---

## 🔗 Links Úteis

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **H2 Console:** http://localhost:8080/h2-console
- **Spring Docs:** https://spring.io/
- **OpenAI Docs:** https://platform.openai.com/docs

---

## ⚡ Dicas Rápidas

### Limpar Cache Maven
```bash
./mvnw clean
```

### Forçar recompile
```bash
./mvnw clean compile
```

### Ver dependências
```bash
./mvnw dependency:tree
```

### Logs de debug
```properties
logging.level.org.springframework.web=DEBUG
logging.level.org.springframework.ai=DEBUG
```

### Restart rápido
```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

---

## 💡 Troubleshooting Rápido

| Problema | Solução |
|----------|---------|
| Port 8080 já em uso | `lsof -i :8080` e kill ou mudar porta |
| API key não funciona | Verificar `OPENAI_API_KEY` environment var |
| Build falha | `./mvnw clean compile -U` |
| H2 não conecta | Verificar `spring.h2.console.enabled=true` |
| Erro 500 no chat | Verificar chave OpenAI e logs |

---

## 📞 Comandos Git

```bash
# Ver status
git status

# Commit
git add .
git commit -m "Descrição"

# Push
git push origin develop

# Nova branch
git checkout -b feature/minha-feature

# Merge
git merge feature/minha-feature
```

---

## 🎯 Checklist Antes de Commit

- [ ] Código compila (`./mvnw clean compile`)
- [ ] Sem warnings
- [ ] Testes passam (`./mvnw test`)
- [ ] Sem código comentado
- [ ] Documentação atualizada
- [ ] Nenhuma API key no código

---

**Última atualização:** 2026-10-12  
**Versão:** 1.0.0
