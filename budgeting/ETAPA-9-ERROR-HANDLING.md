# Etapa 9 — Error Handling (Tratamento de Erros)

## 📋 Implementado

### 1. GlobalExceptionHandler (@RestControllerAdvice)

Classe centralizada que intercepta exceções e converte em respostas HTTP padronizadas.

**Localização:** `infrastructure/web/GlobalExceptionHandler.java`

**Mapeamentos de Exceções:**

| Exceção | Status HTTP | Descrição |
|---------|-------------|-----------|
| `ResourceNotFoundException` | 404 Not Found | Recurso não encontrado |
| `BusinessException` | 400 Bad Request | Erro de negócio/validação |
| `IllegalArgumentException` | 400 Bad Request | Argumento inválido |
| `MethodArgumentNotValidException` | 400 Bad Request | Validação de @Valid falhou |
| `Exception` (genérica) | 500 Internal Server Error | Erro inesperado |

### 2. Exceções Customizadas

#### ResourceNotFoundException
- **Uso:** Quando um recurso não é encontrado
- **Status:** 404 Not Found
- **Exemplo:** GET /transactions/999 (ID não existe)

#### BusinessException
- **Uso:** Erros de negócio e validações de domínio
- **Status:** 400 Bad Request
- **Exemplo:** Valor de transação negativo, descrição vazia, etc

### 3. Padrão de Resposta Padronizado

Todas as respostas de erro seguem este formato:

```json
{
  "timestamp": "2026-10-12T16:10:30.123",
  "status": 404,
  "error": "Resource Not Found",
  "message": "Transaction with ID 999 not found",
  "path": "/transactions/999"
}
```

## 🧪 Como Testar

### Teste 1: Request Body Inválido (400)
```bash
curl -X POST http://localhost:8080/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "description": "",
    "amount": -50,
    "type": "EXPENSE"
  }'
```

**Resposta esperada:**
```json
{
  "timestamp": "2026-10-12T16:10:30.123",
  "status": 400,
  "error": "Validation Failed",
  "message": "Validation errors: description: must not be blank; amount: must be greater than 0",
  "path": "/transactions"
}
```

### Teste 2: Recurso Não Encontrado (404)
```bash
curl -X GET http://localhost:8080/transactions/999
```

**Resposta esperada:**
```json
{
  "timestamp": "2026-10-12T16:10:30.123",
  "status": 404,
  "error": "Resource Not Found",
  "message": "Transaction with ID 999 not found",
  "path": "/transactions/999"
}
```

### Teste 3: Chat com API Indisponível (500)
```bash
curl -X POST http://localhost:8080/ai/chat \
  -H "Content-Type: application/json" \
  -d '{"text":"Qual é meu saldo?"}'
```

**Se a API OpenAI não estiver configurada:**
```json
{
  "timestamp": "2026-10-12T16:10:30.123",
  "status": 500,
  "error": "Internal Server Error",
  "message": "API key not configured",
  "path": "/ai/chat"
}
```

## 🔄 Fluxo de Tratamento

```
Request HTTP
    ↓
[Controller ou Service]
    ↓
[Lança Exceção]
    ↓
[GlobalExceptionHandler intercepta]
    ↓
[Mapeia para Status HTTP correto]
    ↓
[Formata resposta JSON]
    ↓
Response HTTP com Status Code
```

## ✅ Benefícios

1. **Centralização:** Todos os erros em um único lugar
2. **Consistência:** Formato padronizado em todas as respostas
3. **Manutenibilidade:** Fácil adicionar novos tratamentos
4. **API Profissional:** Respostas estruturadas e informativas
5. **Debugging:** Informações suficientes para entender o que deu errado

## 📝 Próximas Etapas (Etapa 10)

- [ ] Documentação final em README
- [ ] Exemplos de uso da API
- [ ] Instruções para setup do projeto
- [ ] Workflow completo de voice budgeting

## 📂 Arquivos Criados

```
infrastructure/
├── web/
│   └── GlobalExceptionHandler.java (novo)
└── exception/
    ├── ResourceNotFoundException.java (novo)
    └── BusinessException.java (novo)
```

## 🎯 Status

✅ **Etapa 9 Completa** — Error handling centralizado implementado com sucesso
