# 🎙️ Voice Budgeting Assistant

![Java](https://img.shields.io/badge/Java-25-ED8B00?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1-6DB33F?style=for-the-badge&logo=spring-boot)
![Spring AI](https://img.shields.io/badge/Spring%20AI-2.0.1-FF6B35?style=for-the-badge&logo=spring)
![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)
![Status](https://img.shields.io/badge/Status-Complete-brightgreen?style=for-the-badge)

**Um assistente de orçamento inteligente baseado em Spring Boot e Spring AI que permite gerenciar suas finanças através de REST API, chat com IA e integração com voz.**

---

## 🎯 Visão Geral

Este projeto implementa uma API REST completa com arquitetura **Domain-Driven Design (DDD)** em 10 etapas, incluindo:

- ✅ REST CRUD API para gerenciar transações
- ✅ Chat com IA para processamento de comandos
- ✅ Tool Calling para automação de operações
- ✅ Tratamento centralizado de erros
- ✅ Documentação completa com Swagger/OpenAPI
- ✅ Placeholder para Whisper (transcrição) e TTS (síntese de voz)

---

## 🚀 Quick Start

### 1. Clone o Repositório
```bash
git clone https://github.com/danhpaiva/voice-budgeting-assistant-java-spring-ai.git
cd voice-budgeting-assistant-java-spring-ai/budgeting
```

### 2. Configure a API Key do OpenAI
```bash
# Linux/Mac
export OPENAI_API_KEY="sk-your-api-key"

# Windows (PowerShell)
$env:OPENAI_API_KEY="sk-your-api-key"
```

### 3. Compile e Execute
```bash
./mvnw clean compile
./mvnw spring-boot:run
```

### 4. Acesse a Documentação
```
Swagger UI: http://localhost:8080/swagger-ui.html
H2 Console: http://localhost:8080/h2-console
```

---

## 📚 Documentação Completa

**[Veja o README Detalhado →](budgeting/README.md)**

Lá você encontrará:
- 🏗️ Arquitetura completa (DDD com 3 camadas)
- 📡 Todos os endpoints REST documentados
- 🧠 Explicação de cada componente
- 🧪 Exemplos de uso com curl
- 🔒 Configuração de segurança
- 🛠️ Troubleshooting

---

## 📁 Estrutura do Projeto

```
budgeting/
├── src/main/java/one/digitalinnovation/budgeting/
│   ├── domain/                    # Entidades, tipos, repositórios
│   ├── application/               # Use cases
│   ├── infrastructure/
│   │   ├── ai/                    # Tools e ChatClient
│   │   ├── persistence/           # Adapters JPA
│   │   ├── web/                   # Controllers, DTOs, exception handlers
│   │   └── exception/             # Custom exceptions
│   └── BudgetingApplication.java
├── src/main/resources/
│   └── application.properties
├── pom.xml
└── README.md                      # Documentação detalhada
```

---

## 🏗️ 10 Etapas Implementadas

| # | Etapa | Status |
|---|-------|--------|
| 1 | Configuração Base (pom.xml, properties) | ✅ |
| 2 | Domain Layer (Transaction, TransactionType) | ✅ |
| 3 | Use Cases (CreateTransaction, GetTransactions) | ✅ |
| 4 | Persistence Layer (Adapter Pattern) | ✅ |
| 5 | REST CRUD (3 endpoints) | ✅ |
| 6 | Spring AI Tools (BudgetingTools) | ✅ |
| 7 | ChatClient Configuration | ✅ |
| 8 | Voice Endpoints (4 endpoints) | ✅ |
| 9 | Error Handling (GlobalExceptionHandler) | ✅ |
| 10 | Documentação Final | ✅ |

---

## 🛠️ Stack Tecnológico

- **Spring Boot 4.1.1** - Framework web e REST
- **Spring Data JPA** - Persistência
- **Spring AI 2.0.1** - Integração com OpenAI
- **H2 Database** - Banco em memória
- **SpringDoc OpenAPI 2.1.0** - Swagger/OpenAPI
- **Maven 3.9+** - Gerenciamento de dependências

---

## 📡 Endpoints Principais

```
POST   /transactions              # Criar transação
GET    /transactions              # Listar transações
GET    /transactions/{id}         # Obter transação por ID
POST   /ai/chat                   # Chat com IA (funcional)
POST   /ai/voice                  # Endpoint de voz (placeholder)
POST   /ai/transcribe             # Transcrição de áudio (placeholder)
POST   /ai/speak                  # Text-to-Speech (placeholder)
```

---

## 💡 Exemplo de Uso

### Criar Transação
```bash
curl -X POST http://localhost:8080/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Almoço",
    "amount": 50.00,
    "type": "EXPENSE"
  }'
```

### Chat com IA
```bash
curl -X POST http://localhost:8080/ai/chat \
  -H "Content-Type: application/json" \
  -d '{"text": "Qual é meu saldo?"}'
```

---

## 🔧 Requisitos

- **Java 25+** (testado com Java 25.0.4.1)
- **Maven 3.9+**
- **OpenAI API Key** (obtido em https://platform.openai.com/api-keys)

---

## 🏢 Compilação e Testes

```bash
# Compilar
./mvnw clean compile          # BUILD SUCCESS (17 source files)

# Executar server
./mvnw spring-boot:run        # Server on :8080

# Ver logs
./mvnw clean compile 2>&1 | tail -20
```

---

## 📞 Contato e Suporte

**Desenvolvedor:** Daniel Paiva
- 🐙 GitHub: [@danhpaiva](https://github.com/danhpaiva)
- 💼 LinkedIn: [in/danhpaiva](https://www.linkedin.com/in/danhpaiva/?isSelfProfile=true)

**Projeto:**
- 📦 Repositório: [voice-budgeting-assistant-java-spring-ai](https://github.com/danhpaiva/voice-budgeting-assistant-java-spring-ai)

**Documentação:**
- [README Detalhado](budgeting/README.md)
- [GUIA de Desenvolvimento](budgeting/GUIA-DESENVOLVIMENTO.md)
- [CHANGELOG](budgeting/CHANGELOG.md)

---

## 📄 Licença

[MIT License](https://github.com/danhpaiva/voice-budgeting-assistant-java-spring-ai/blob/develop/LICENSE)

Este projeto está licenciado sob a Licença MIT.

---

**Status:** ✅ Completo  
**Última Atualização:** 12 de outubro de 2026  
**Versão:** 0.0.1-SNAPSHOT (Todas as 10 etapas implementadas)
