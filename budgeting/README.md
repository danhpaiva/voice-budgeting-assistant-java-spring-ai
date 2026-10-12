# 🎙️ Voice Budgeting Assistant

Um assistente de orçamento inteligente baseado em **Spring Boot** e **Spring AI** que permite gerenciar suas finanças através de:
- ✅ REST API para CRUD de transações
- ✅ Chat com IA para processamento de comandos de voz
- ✅ Transcrição de áudio (Whisper)
- ✅ Síntese de fala (TTS)
- ✅ Tool Calling para executar operações automáticas

---

## 🚀 Quick Start

### Pré-requisitos

- **Java 25+** (testado com Java 25.0.4.1)
- **Maven 3.9+**
- **OpenAI API Key** (obtido em https://platform.openai.com/api-keys)

### 1. Clonar e Configurar

```bash
# Clonar repositório
git clone <seu-repo>
cd budgeting

# Definir variável de ambiente com sua chave OpenAI
export OPENAI_API_KEY="sk-your-api-key-here"
```

### 2. Compilar Projeto

```bash
./mvnw clean compile
```

### 3. Executar Servidor

```bash
./mvnw spring-boot:run
```

Servidor inicia em: **http://localhost:8080**

### 4. Acessar Swagger UI

Documentação interativa:
```
http://localhost:8080/swagger-ui.html
```

---

## 📊 Arquitetura

O projeto segue **Domain-Driven Design (DDD)** com 3 camadas principais:

### 1. **Domain Layer** (Camada de Domínio)
Entidades e interfaces de negócio:
- `Transaction` (Record com JPA @Entity)
- `TransactionType` (Enum: INCOME, EXPENSE)
- `TransactionRepository` (Interface pura, sem dependências Spring)

**Arquivo:** `domain/`

### 2. **Application Layer** (Camada de Aplicação)
Casos de uso e lógica de negócio:
- `CreateTransactionUseCase` - Criar transação com validações
- `GetTransactionsUseCase` - Consultar transações com 6 métodos de filtro

**Arquivo:** `application/`

### 3. **Infrastructure Layer** (Camada de Infraestrutura)

#### a) **Persistence**
- `SpringDataTransactionRepository` - Adapter JPA (implements domain interface)
- `TransactionJpaRepository` - Spring Data JPA interface

**Arquivo:** `infrastructure/persistence/`

#### b) **Web**
- `TransactionController` - REST endpoints (POST, GET)
- `AiVoiceController` - Endpoints de voz e chat
- `GlobalExceptionHandler` - Tratamento centralizado de erros
- DTOs com validações

**Arquivo:** `infrastructure/web/`

#### c) **AI**
- `BudgetingTools` - Expõe use cases como ferramentas para IA
- `AiConfig` - Configuração do ChatClient OpenAI

**Arquivo:** `infrastructure/ai/`

#### d) **Exception Handling**
- `ResourceNotFoundException` - Recurso não encontrado (404)
- `BusinessException` - Erro de negócio (400)

**Arquivo:** `infrastructure/exception/`

---

## 📡 REST API Endpoints

### Transações

#### 1. Criar Transação
```http
POST /transactions
Content-Type: application/json

{
  "description": "Almoço",
  "amount": 50.00,
  "type": "EXPENSE"
}
```

**Resposta (201 Created):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "description": "Almoço",
  "amount": 50.00,
  "type": "EXPENSE",
  "createdAt": "2026-10-12T16:00:00"
}
```

#### 2. Listar Transações
```http
GET /transactions
```

**Resposta (200 OK):**
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "description": "Almoço",
    "amount": 50.00,
    "type": "EXPENSE"
  }
]
```

#### 3. Obter Transação
```http
GET /transactions/{id}
```

**Resposta (200 OK):** Objeto Transaction

---

### Chat com IA

#### 1. Chat (Texto)
```http
POST /ai/chat
Content-Type: application/json

{
  "text": "Qual é meu saldo?"
}
```

**Resposta (200 OK):**
```json
{
  "response": "Seu saldo atual é R$0.00",
  "status": "success"
}
```

#### 2. Voice (Áudio) — Placeholder
```http
POST /ai/voice
Content-Type: multipart/form-data

audio=@seu-audio.mp3
```

**Resposta:** MP3 binário com resposta em áudio

#### 3. Transcrição (Áudio → Texto) — Placeholder
```http
POST /ai/transcribe
Content-Type: multipart/form-data

audio=@seu-audio.mp3
```

**Resposta (200 OK):**
```json
{
  "transcript": "Qual é meu saldo?",
  "status": "success"
}
```

#### 4. Síntese de Voz (Texto → Áudio) — Placeholder
```http
POST /ai/speak
Content-Type: application/json

{
  "text": "Seu saldo é zero"
}
```

**Resposta:** MP3 binário com síntese de voz

---

## 🔧 Configuração

### application.properties

```properties
# Aplicação
spring.application.name=budgeting
server.port=8080

# Database (H2 em memória)
spring.datasource.url=jdbc:h2:mem:budgeting
spring.datasource.driverClassName=org.h2.Driver
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop

# OpenAI Configuration
spring.ai.openai.api-key=${OPENAI_API_KEY:demo}
spring.ai.openai.chat.model=gpt-3.5-turbo
spring.ai.openai.audio.transcription.model=whisper-1
spring.ai.openai.audio.speech.model=tts-1
spring.ai.openai.audio.speech.voice=alloy

# Logging
logging.level.root=INFO
logging.level.one.digitalinnovation.budgeting=DEBUG
logging.level.org.springframework.ai=DEBUG
```

---

## 🧪 Exemplos de Uso

### Exemplo 1: Criar Transação e Consultar

```bash
# 1. Criar uma despesa
curl -X POST http://localhost:8080/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Café da manhã",
    "amount": 25.50,
    "type": "EXPENSE"
  }'

# 2. Listar todas as transações
curl http://localhost:8080/transactions

# 3. Chat com IA
curl -X POST http://localhost:8080/ai/chat \
  -H "Content-Type: application/json" \
  -d '{
    "text": "Quanto gastei?"
  }'
```

### Exemplo 2: Fluxo Completo de Voz (Future)

```bash
# 1. Enviar áudio em português
curl -X POST http://localhost:8080/ai/voice \
  -H "Content-Type: multipart/form-data" \
  -F "audio=@comando-voz.mp3"

# Fluxo interno:
# 1. Whisper transcreve: "Qual é meu saldo?"
# 2. ChatClient processa com tool calling
# 3. IA chama automaticamente: budgetingTools.calculateBalance()
# 4. TTS gera resposta: "Seu saldo atual é R$100,00"
# 5. Retorna: audio-resposta.mp3
```

---

## 🛠️ Tratamento de Erros

Todos os erros retornam formato padronizado:

```json
{
  "timestamp": "2026-10-12T16:10:30.123",
  "status": 400,
  "error": "Bad Request",
  "message": "Descrição do erro",
  "path": "/ai/chat"
}
```

### Status Codes

| Status | Descrição |
|--------|-----------|
| 200 | Sucesso |
| 201 | Criado com sucesso |
| 400 | Erro de validação ou negócio |
| 404 | Recurso não encontrado |
| 500 | Erro interno do servidor |

### Exemplos

**Erro 400 - Texto vazio:**
```bash
curl -X POST http://localhost:8080/ai/chat \
  -H "Content-Type: application/json" \
  -d '{"text": ""}'
```

Resposta:
```json
{
  "timestamp": "2026-10-12T16:10:30.123",
  "status": 400,
  "error": "Bad Request",
  "message": "Text field is required and cannot be empty",
  "path": "/ai/chat"
}
```

---

## 🏗️ Estrutura de Pastas

```
budgeting/
├── src/main/
│   ├── java/one/digitalinnovation/budgeting/
│   │   ├── domain/
│   │   │   ├── Transaction.java (record)
│   │   │   ├── TransactionType.java (enum)
│   │   │   └── TransactionRepository.java (interface)
│   │   ├── application/
│   │   │   ├── CreateTransactionUseCase.java
│   │   │   └── GetTransactionsUseCase.java
│   │   └── infrastructure/
│   │       ├── persistence/
│   │       │   ├── SpringDataTransactionRepository.java
│   │       │   └── TransactionJpaRepository.java
│   │       ├── web/
│   │       │   ├── TransactionController.java
│   │       │   ├── AiVoiceController.java
│   │       │   ├── GlobalExceptionHandler.java
│   │       │   └── dto/
│   │       ├── ai/
│   │       │   ├── BudgetingTools.java
│   │       │   └── AiConfig.java
│   │       └── exception/
│   │           ├── ResourceNotFoundException.java
│   │           └── BusinessException.java
│   └── resources/
│       └── application.properties
└── pom.xml
```

---

## 📚 Dependências Principais

- **Spring Boot 4.1.1** - Framework base
- **Spring Data JPA** - Persistência
- **Spring AI 2.0.1** - Integração com OpenAI
- **H2 Database** - Banco em memória
- **SpringDoc OpenAPI 2.1.0** - Swagger/OpenAPI
- **Hibernate 7.4.5** - ORM

---

## 🔐 Segurança

### API Key OpenAI

**NUNCA commit a API key no Git!**

Configurar como variável de ambiente:

```bash
# Linux/Mac
export OPENAI_API_KEY="sk-your-api-key"

# Windows (PowerShell)
$env:OPENAI_API_KEY="sk-your-api-key"

# Windows (CMD)
set OPENAI_API_KEY=sk-your-api-key
```

---

## 📈 Plano de Desenvolvimento (10 Etapas)

| Etapa | Descrição | Status |
|-------|-----------|--------|
| 1 | Configuração Base (pom.xml, properties) | ✅ Completa |
| 2 | Domain Layer (Transaction, TransactionType) | ✅ Completa |
| 3 | Use Cases (Create, Get) | ✅ Completa |
| 4 | Persistence Layer (Adapter Pattern) | ✅ Completa |
| 5 | REST CRUD (POST, GET endpoints) | ✅ Completa |
| 6 | Spring AI Tools (BudgetingTools) | ✅ Completa |
| 7 | ChatClient Configuration | ✅ Completa |
| 8 | Voice Endpoints (Placeholder) | ✅ Completa |
| 9 | Error Handling (GlobalExceptionHandler) | ✅ Completa |
| 10 | Documentação Final | ✅ Completa |

---

## 🚦 Como Testar

### Teste 1: Criar Transação
```bash
curl -X POST http://localhost:8080/transactions \
  -H "Content-Type: application/json" \
  -d '{"description":"Test","amount":100,"type":"INCOME"}'
```

### Teste 2: Listar Transações
```bash
curl http://localhost:8080/transactions
```

### Teste 3: Chat com IA
```bash
curl -X POST http://localhost:8080/ai/chat \
  -H "Content-Type: application/json" \
  -d '{"text":"Quantas transações tenho?"}'
```

### Teste 4: Erro (Validação)
```bash
curl -X POST http://localhost:8080/ai/chat \
  -H "Content-Type: application/json" \
  -d '{"text":""}'
```

---

## 📝 Notas Importantes

### Database
- H2 em memória: dados são perdidos ao reiniciar
- Para persistência, adicione MySQL/PostgreSQL

### Spring AI
- Requer Spring AI 2.0.1 compatível com Spring Boot 4.1.1
- Tool calling depende da versão do modelo OpenAI
- Placeholder para Whisper e TTS (implementação futura)

### Performance
- Primeira requisição é mais lenta (cold start do Spring Boot)
- Recomendado usar container Docker para produção

---

## 🐛 Troubleshooting

### Erro: "OPENAI_API_KEY not configured"
```bash
Solução: Definir a variável de ambiente OPENAI_API_KEY
```

### Erro: "Cannot find symbol: class OpenAiChatModel"
```bash
Solução: Verificar se spring-ai-starter-model-openai 2.0.1 está no pom.xml
./mvnw clean compile
```

### Erro: "H2 console not accessible"
```bash
Solução: Verificar em application.properties:
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

Acessar: http://localhost:8080/h2-console
```

---

## 📞 Contato e Suporte

### Desenvolvedor
**Daniel Paiva**
- 🐙 GitHub: [@danhpaiva](https://github.com/danhpaiva)
- 💼 LinkedIn: [in/danhpaiva](https://www.linkedin.com/in/danhpaiva/?isSelfProfile=true)

### Projeto
- 📦 Repositório: [voice-budgeting-assistant-java-spring-ai](https://github.com/danhpaiva/voice-budgeting-assistant-java-spring-ai)
- 🔗 Branch: `develop`

### Suporte Técnico
Para dúvidas ou issues:
1. Verificar logs em `application.properties` (logging.level)
2. Acessar Swagger UI: http://localhost:8080/swagger-ui.html
3. Verificar documentação das etapas (ETAPA-*.md)
4. Abrir uma issue no [GitHub](https://github.com/danhpaiva/voice-budgeting-assistant-java-spring-ai/issues)

---

## 📄 Licença

[MIT License](https://github.com/danhpaiva/voice-budgeting-assistant-java-spring-ai/blob/develop/LICENSE)

Este projeto está licenciado sob a Licença MIT. Veja o arquivo [LICENSE](https://github.com/danhpaiva/voice-budgeting-assistant-java-spring-ai/blob/develop/LICENSE) para mais detalhes.

---

## ✅ Conclusão

A **Etapa 10 (Documentação)** está completa! 🎉

O Voice Budgeting Assistant está:
- ✅ Compilando sem erros
- ✅ Com API REST funcional
- ✅ Com tratamento de erros centralizado
- ✅ Com documentação completa
- ✅ Pronto para desenvolvimento futuro (Whisper, TTS)

**Próximas melhorias:** Implementar endpoints de voz (Whisper + TTS), adicionar autenticação, e deploy em produção.
