# 🎤 Voice Budgeting Assistant - Spring AI

![Java](https://img.shields.io/badge/Java-25-ED8B00?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1-6DB33F?style=for-the-badge&logo=spring-boot)
![Spring AI](https://img.shields.io/badge/Spring%20AI-Coming%20Soon-FF6B35?style=for-the-badge&logo=spring)
![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)
![Status](https://img.shields.io/badge/Status-In%20Development-yellow?style=for-the-badge)

**Uma API inteligente que processa comandos de voz para gerenciar transações financeiras usando Spring Boot e Spring AI.**

---

## 📋 Visão Rápida

```
🎤 Áudio (português/inglês)
  ↓
📝 Transcrição (Whisper)
  ↓
🧠 Interpretação com IA
  ↓
💾 Persistência em banco de dados
  ↓
🔊 Resposta em áudio (TTS)
```

---

## 🚀 Quick Start

### 1. Clone o Repositório
```bash
git clone https://github.com/seu-usuario/voice-budgeting-assistant-java-spring-ai.git
cd voice-budgeting-assistant-java-spring-ai/budgeting
```

### 2. Compile
```bash
./mvnw clean compile
```

### 3. Execute
```bash
./mvnw spring-boot:run
```

### 4. Teste a API
```bash
# Swagger UI
open http://localhost:8080/swagger-ui.html

# H2 Console
open http://localhost:8080/h2-console
```

---

## 📚 Documentação

- **[📖 README Detalhado](budgeting/README.md)** — Documentação completa com exemplos
- **[🏗️ Plano de Tarefas](TAREFAS.md)** — Roadmap das implementações
- **[🎓 Conceitos DDD](CONCEITOS.md)** — Explicação de Domain-Driven Design

---

## 🛠️ Stack Tecnológico

| Layer | Tecnologia | Uso |
|---|---|---|
| **Web** | Spring Boot 4.1 | Framework web e REST |
| **Database** | H2 + Spring Data JPA | Persistência ORM |
| **AI** | Spring AI (futuro) | Chat, Transcription, TTS |
| **Documentation** | Swagger/OpenAPI | Documentação automática |
| **Build** | Maven 3.8+ | Gerenciamento de dependências |

---

## 📁 Estrutura do Projeto

```
voice-budgeting-assistant-java-spring-ai/
│
├── budgeting/                          # Aplicação principal
│   ├── src/main/java/one/digitalinnovation/budgeting/
│   │   ├── domain/                    # Entidades e repositórios
│   │   ├── application/               # Use cases
│   │   ├── infrastructure/            # Controllers, adaptadores
│   │   └── BudgetingApplication.java  # Entry point
│   │
│   ├── src/main/resources/
│   │   └── application.properties     # Configurações
│   │
│   ├── pom.xml                        # Maven config
│   └── README.md                      # Documentação da app
│
├── README.md                          # Este arquivo
└── .gitignore
```

---

## 🎯 Objetivos do Projeto

✅ **Camada de Domínio**
- Modelar transações financeiras com DDD
- Implementar repositório de domínio
- Usar enums para tipos de transação

✅ **Camada de Aplicação**
- Criar use cases para criar e consultar transações
- Validar negócio de forma independente

✅ **Camada de Infraestrutura**
- Adaptar repositório de domínio para JPA
- Implementar REST CRUD
- Integrar com Spring AI para voz

✅ **Spring AI Integration**
- Transcrever áudio com Whisper
- Usar tool calling para executar funções
- Gerar áudio com Text-to-Speech

---

## 🎓 O Que Você Aprenderá

### 1. **Domain-Driven Design (DDD)**
   - Separação de camadas (Domain, Application, Infrastructure)
   - Repositórios como abstrações de domínio
   - Value objects e agregados

### 2. **Spring Boot Expert**
   - Injeção de dependência
   - Auto-configuração
   - Properties e profiles
   - Spring Data JPA

### 3. **Spring AI**
   - ChatClient e tool calling
   - Modelos de IA (Chat, Embedding, TTS)
   - Transcription APIs
   - Integration patterns

### 4. **REST API Design**
   - Resource-oriented endpoints
   - HTTP methods e status codes
   - Content negotiation
   - OpenAPI documentation

### 5. **Testing & Quality**
   - Testes unitários
   - Testes de integração
   - Mocking com Mockito
   - Test containers

---

## 📋 Fases de Desenvolvimento

### Fase 1: Setup Base ✅
- [x] Projeto Maven com Spring Boot 4.1
- [x] Dependências configuradas
- [x] H2 Database em memória
- [x] Swagger/OpenAPI
- [x] README documentado

### Fase 2: Camadas (➡️ Próxima)
- [ ] Domínio (Transaction, TransactionType, Repository)
- [ ] Aplicação (CreateTransactionUseCase, GetTransactionsUseCase)
- [ ] Persistência (TransactionJpaRepository)
- [ ] REST CRUD (TransactionController)

### Fase 3: AI Integration
- [ ] Spring AI adicionado
- [ ] ChatClient configurado
- [ ] BudgetingTools @Tool methods
- [ ] Endpoint `/ai/voice`

### Fase 4: Voice Features
- [ ] Transcription (Speech-to-Text)
- [ ] Tool calling com IA
- [ ] Text-to-Speech (TTS)
- [ ] Fluxo completo áudio ↔ texto ↔ IA

### Fase 5: Quality & Testing
- [ ] Testes unitários
- [ ] Testes de integração
- [ ] Tratamento robusto de erros
- [ ] Documentação técnica

### Fase 6: Deploy
- [ ] Docker container
- [ ] CI/CD pipeline
- [ ] Exemplo de deploy

---

## 💡 Exemplos de Uso

### Criar uma Transação via REST

```bash
curl -X POST http://localhost:8080/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Almoço",
    "amount": 50.00,
    "type": "EXPENSE"
  }'
```

**Response:**
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "description": "Almoço",
  "amount": 50.00,
  "type": "EXPENSE",
  "createdAt": "2026-10-12T15:10:30"
}
```

### Listar Transações

```bash
curl http://localhost:8080/transactions
```

### Usar IA para Processar Voz (futuro)

```bash
curl -X POST http://localhost:8080/ai/voice \
  -F "file=@audio.mp3"
```

---

## 🔧 Requisitos

- **Java 25** ou superior
- **Maven 3.8.1** ou superior
- **Git** para controle de versão
- **OpenAI API Key** (para usar Spring AI — adicione depois)

### Verificar Requisitos

```bash
java -version       # Deve ser 25+
mvn -version        # Deve ser 3.8.1+
git --version       # Qualquer versão recente
```

---

## 🚀 Próximos Passos

1. **Estude o README da aplicação:**
   ```bash
   cat budgeting/README.md
   ```

2. **Explore a arquitetura:**
   - Leia sobre DDD em `CONCEITOS.md`
   - Veja o plano de tarefas em `TAREFAS.md`

3. **Comece a implementar Fase 2:**
   - Crie a camada de domínio
   - Implemente use cases
   - Adapte para persistência

4. **Teste com Swagger:**
   - Acesse `http://localhost:8080/swagger-ui.html`
   - Teste endpoints conforme implementar

5. **Adicione Spring AI:**
   - Configure OPENAI_API_KEY
   - Integre transcription e TTS
   - Implemente tool calling

---

## 📞 Suporte & Comunidade

- **DIO Platform:** https://www.dio.me
- **Spring Docs:** https://spring.io/projects/spring-boot
- **Spring AI Docs:** https://docs.spring.io/spring-ai/reference/
- **Stack Overflow:** [tag: spring-boot](https://stackoverflow.com/questions/tagged/spring-boot)

---

## 📄 Licença

MIT License — veja [LICENSE](LICENSE) para detalhes

---

## 👨‍💻 Autor

**Seu Nome**
- 🔗 GitHub: [@seu-usuario](https://github.com/seu-usuario)
- 💼 LinkedIn: [seu-perfil](https://linkedin.com/in/seu-perfil)

---

## 🎓 Créditos

Projeto educacional do **DIO Spring Boot Learning Track**
- Expert: **Poiani**
- Trilha: **Spring Boot Expert**
- Desafio Final: **05 — Spring AI**

---

**Status:** Em Desenvolvimento ⚙️  
**Última Atualização:** 12 de outubro de 2026  
**Versão:** 0.0.1-SNAPSHOT
