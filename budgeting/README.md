# 🎤 Voice Budgeting Assistant - Spring AI

![Java](https://img.shields.io/badge/Java-25-ED8B00?style=flat-square&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.1-6DB33F?style=flat-square&logo=spring-boot)
![Spring Data JPA](https://img.shields.io/badge/Spring%20Data%20JPA-Latest-6DB33F?style=flat-square&logo=spring)
![H2 Database](https://img.shields.io/badge/H2-In--Memory%20SQL-004B87?style=flat-square&logo=h2&logoColor=white)
![Maven](https://img.shields.io/badge/Build-Maven-C71A36?style=flat-square&logo=apache-maven)
![License](https://img.shields.io/badge/License-MIT-green?style=flat-square)
![Status](https://img.shields.io/badge/Status-In%20Development-yellow?style=flat-square)

**Uma API inteligente de orçamento que processa comandos de voz para criar e consultar transações financeiras usando IA.**

---

## 📋 Índice

- [Visão Geral](#visão-geral)
- [Tecnologias](#-tecnologias)
- [Arquitetura](#-arquitetura)
- [Requisitos](#-requisitos)
- [Instalação](#-instalação)
- [Configuração](#-configuração)
- [Como Executar](#-como-executar)
- [Endpoints da API](#-endpoints-da-api)
- [Fluxo Principal](#-fluxo-principal)
- [Exemplos de Uso](#-exemplos-de-uso)
- [Desenvolvimento](#-desenvolvimento)
- [Roadmap](#-roadmap)
- [Contribuindo](#-contribuindo)

---

## 🎯 Visão Geral

O **Voice Budgeting Assistant** é uma aplicação educacional final do programa **DIO Spring Boot Track** que demonstra como integrar:

- ✅ **Spring Boot 4.1** — Framework web e aplicação
- ✅ **Spring Data JPA** — Persistência de dados
- ✅ **H2 Database** — Banco de dados em memória para desenvolvimento
- ✅ **Spring AI (futuro)** — Processamento de áudio e IA para tool calling
- ✅ **OpenAPI/Swagger** — Documentação automática da API

### Objetivo

Criar uma API REST inteligente que:

1. Recebe comandos de voz em português/inglês
2. Transcreve áudio em texto usando Whisper (OpenAI)
3. Utiliza IA para interpretar a intenção do usuário
4. Executa funções reais de negócio (criar/consultar transações)
5. Retorna resposta em voz (Text-to-Speech)

---

## 🛠️ Tecnologias

| Tecnologia | Versão | Uso |
|---|---|---|
| **Java** | 25 | Linguagem principal |
| **Spring Boot** | 4.1.1 | Framework web e orquestração |
| **Spring Data JPA** | Latest | ORM e persistência |
| **Hibernate** | Latest | Mapeamento relacional de objetos |
| **H2 Database** | Latest | Base de dados em memória (dev) |
| **SpringDoc OpenAPI** | 2.1.0 | Documentação Swagger automática |
| **Spring AI** | (a adicionar) | Chat models e transcription |
| **Maven** | 3.6+ | Build e gerenciamento de dependências |

---

## 🏗️ Arquitetura

Projeto organizado em **camadas DDD (Domain-Driven Design)**:

```
src/main/java/one/digitalinnovation/budgeting
├── domain/                          # Camada de Domínio
│   ├── Transaction.java            # Entidade de transação
│   ├── TransactionType.java        # Enum (INCOME, EXPENSE)
│   └── TransactionRepository.java  # Contrato de repositório
│
├── application/                     # Camada de Aplicação (Use Cases)
│   ├── CreateTransactionUseCase.java
│   └── GetTransactionsUseCase.java
│
├── infrastructure/                  # Camada de Infraestrutura
│   ├── persistence/
│   │   └── TransactionJpaRepository.java
│   ├── ai/
│   │   ├── AiConfig.java           # Configuração do ChatClient
│   │   └── BudgetingTools.java     # @Tool methods para IA
│   └── web/
│       ├── TransactionController.java
│       ├── AiVoiceController.java
│       └── GlobalExceptionHandler.java
│
└── BudgetingApplication.java       # Entry point
```

### Fluxo de Requisição

```
Cliente
  ↓
[POST /ai/voice] ← AiVoiceController
  ↓
TranscriptionModel (Whisper) → Texto
  ↓
ChatClient + Tools (BudgetingTools)
  ↓
[CreateTransaction | GetTransactions] ← Use Cases
  ↓
TransactionRepository → Persistência (H2)
  ↓
TextToSpeechModel → MP3
  ↓
Response: audio/mpeg
```

---

## 📦 Requisitos

### Pré-requisitos

- **Java 25** ou superior
  ```bash
  java -version
  ```
  
- **Maven 3.8.1** ou superior
  ```bash
  mvn -version
  ```

- **Git** (para clonar o repositório)
  ```bash
  git clone https://github.com/seu-usuario/voice-budgeting-assistant.git
  ```

### Dependências de Ambiente (opcional)

- **OpenAI API Key** (necessário para usar IA depois)
  ```bash
  export OPENAI_API_KEY="sk-..."
  ```

---

## 💻 Instalação

### 1. Clone o Repositório

```bash
git clone https://github.com/seu-usuario/voice-budgeting-assistant.git
cd voice-budgeting-assistant/budgeting
```

### 2. Verifique Java e Maven

```bash
java -version      # Deve ser Java 25+
mvn -version       # Deve ser Maven 3.8.1+
```

### 3. Compile o Projeto

```bash
./mvnw clean compile
```

**Esperado:** `BUILD SUCCESS`

---

## ⚙️ Configuração

### `application.properties`

O arquivo `src/main/resources/application.properties` contém as configurações principais:

```properties
# Servidor
server.port=8080

# H2 Database (em memória)
spring.datasource.url=jdbc:h2:mem:budgeting
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=false

# Logging
logging.level.root=INFO
logging.level.one.digitalinnovation.budgeting=DEBUG
```

### Variáveis de Ambiente (futuro com Spring AI)

Quando Spring AI for adicionado, configure:

```bash
export OPENAI_API_KEY="your-api-key-here"
```

**Como obter a chave:**
1. Acesse [platform.openai.com](https://platform.openai.com)
2. Crie uma conta ou faça login
3. Acesse Settings → API Keys
4. Gere uma nova chave secreta
5. Copie e armazene de forma segura

---

## 🚀 Como Executar

### Opção 1: Maven Wrapper (Recomendado)

```bash
cd budgeting

# Iniciar a aplicação
./mvnw spring-boot:run

# Ou compilar e rodar diretamente
./mvnw clean package
java -jar target/budgeting-0.0.1-SNAPSHOT.jar
```

### Opção 2: IDE (VS Code, IntelliJ IDEA)

1. Abra a pasta `budgeting` na sua IDE
2. Clique em "Run" ou pressione `F5`
3. A aplicação iniciará em `http://localhost:8080`

### Verificação de Inicialização

Quando a aplicação iniciar, você verá algo como:

```
2026-10-12 15:10:00.123  INFO 12345 --- [main] o.d.b.BudgetingApplication : 
Started BudgetingApplication in 3.456 seconds (process running for 4.789s)
```

---

## 📡 Endpoints da API

### 🏥 Health Check

```http
GET /swagger-ui.html
```

Acessa a documentação interativa da API (Swagger UI).

### 💰 Transações

| Método | Endpoint | Descrição |
|---|---|---|
| **GET** | `/transactions` | Lista todas as transações |
| **GET** | `/transactions/{id}` | Busca uma transação por ID |
| **POST** | `/transactions` | Cria uma nova transação |

#### Criar Transação

**Request:**
```bash
curl -X POST http://localhost:8080/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Almoço no restaurante",
    "amount": 50.00,
    "type": "EXPENSE"
  }'
```

**Response (201 Created):**
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "description": "Almoço no restaurante",
  "amount": 50.00,
  "type": "EXPENSE",
  "createdAt": "2026-10-12T15:10:30"
}
```

#### Listar Transações

**Request:**
```bash
curl http://localhost:8080/transactions
```

**Response (200 OK):**
```json
[
  {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "description": "Almoço no restaurante",
    "amount": 50.00,
    "type": "EXPENSE",
    "createdAt": "2026-10-12T15:10:30"
  },
  {
    "id": "223e4567-e89b-12d3-a456-426614174001",
    "description": "Salário",
    "amount": 5000.00,
    "type": "INCOME",
    "createdAt": "2026-10-12T15:15:00"
  }
]
```

#### Buscar Transação por ID

**Request:**
```bash
curl http://localhost:8080/transactions/123e4567-e89b-12d3-a456-426614174000
```

**Response (200 OK):**
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "description": "Almoço no restaurante",
  "amount": 50.00,
  "type": "EXPENSE",
  "createdAt": "2026-10-12T15:10:30"
}
```

### 🤖 AI / Voice (Futuro)

| Método | Endpoint | Descrição |
|---|---|---|
| **POST** | `/ai/voice` | Processa áudio e retorna resposta em voz |

---

## 🎯 Fluxo Principal

### Fluxo Completo: Áudio → Texto → IA → Transação → Áudio

```sequence
Cliente
  ↓ [POST /ai/voice + audio.mp3]
AiVoiceController
  ↓ [TranscriptionModel]
"Add expense 50 for lunch"
  ↓ [ChatClient.call()]
BudgetingTools
  ↓ [createTransaction()]
CreateTransactionUseCase
  ↓ [save()]
TransactionRepository
  ↓ [persist]
H2 Database
  ↓ [response text]
TextToSpeechModel
  ↓ [generate MP3]
Cliente [audio/mpeg]
```

---

## 📚 Exemplos de Uso

### Exemplo 1: Criar Transação via REST

```bash
# Registrar uma despesa de $75 com café
curl -X POST http://localhost:8080/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Café da manhã",
    "amount": 75.00,
    "type": "EXPENSE"
  }'
```

### Exemplo 2: Listar Todas as Transações

```bash
curl http://localhost:8080/transactions | jq
```

### Exemplo 3: Acessar H2 Console (para debug)

1. Acesse: `http://localhost:8080/h2-console`
2. Use as credenciais padrão:
   - **JDBC URL:** `jdbc:h2:mem:budgeting`
   - **User Name:** `sa`
   - **Password:** (deixe vazio)
3. Execute SQL:
   ```sql
   SELECT * FROM transaction;
   ```

### Exemplo 4: Acessar Swagger UI

1. Acesse: `http://localhost:8080/swagger-ui.html`
2. Veja todos os endpoints documentados
3. Teste endpoints diretamente pela UI

---

## 🔨 Desenvolvimento

### Estrutura de Pastas

```
budgeting/
├── .mvn/                           # Maven wrapper
├── src/
│   ├── main/
│   │   ├── java/one/digitalinnovation/budgeting/
│   │   │   ├── domain/
│   │   │   ├── application/
│   │   │   ├── infrastructure/
│   │   │   └── BudgetingApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── static/             # CSS, JS, imagens
│   │       └── templates/          # HTML (Thymeleaf)
│   └── test/
│       └── java/one/digitalinnovation/budgeting/
│           └── BudgetingApplicationTests.java
├── pom.xml                         # Dependências Maven
└── README.md                       # Este arquivo
```

### Compilar com Debug

```bash
./mvnw clean compile -X
```

### Executar Testes

```bash
./mvnw test
```

### Build para Produção

```bash
./mvnw clean package -DskipTests
```

Gera: `target/budgeting-0.0.1-SNAPSHOT.jar`

### Executar o JAR

```bash
java -jar target/budgeting-0.0.1-SNAPSHOT.jar
```

---

## 📍 Roadmap

### ✅ Fase 1 — Setup Base (Concluída)
- [x] Projeto Maven com Spring Boot 4.1
- [x] Dependências configuradas
- [x] H2 Database em memória
- [x] Swagger/OpenAPI

### 🔄 Fase 2 — Camadas (Em Progresso)
- [ ] Domínio (Transaction, TransactionType, Repository)
- [ ] Aplicação (CreateTransactionUseCase, GetTransactionsUseCase)
- [ ] Persistência (TransactionJpaRepository)
- [ ] REST CRUD (TransactionController)

### 🚧 Fase 3 — AI Integration
- [ ] Spring AI adicionado
- [ ] ChatClient configurado
- [ ] BudgetingTools @Tool methods
- [ ] Endpoint `/ai/voice`

### 🎤 Fase 4 — Voice Features
- [ ] Transcription (Whisper)
- [ ] Tool calling com IA
- [ ] Text-to-Speech (TTS)
- [ ] Fluxo completo áudio

### 🧪 Fase 5 — Qualidade
- [ ] Testes unitários
- [ ] Testes de integração
- [ ] Tratamento de erros robusto
- [ ] Documentação API

### 🚀 Fase 6 — Deploy
- [ ] Docker container
- [ ] CI/CD pipeline
- [ ] Deploy em produção

---

## 🤝 Contribuindo

Quer evoluir o projeto? Siga estes passos:

### 1. Fork o Repositório
```bash
git clone https://github.com/seu-usuario/voice-budgeting-assistant.git
cd voice-budgeting-assistant
```

### 2. Crie uma Branch para sua Feature
```bash
git checkout -b feature/minha-feature
```

### 3. Faça Commits Descritivos
```bash
git commit -m "feat: adiciona validação de transações"
```

### 4. Push para a Branch
```bash
git push origin feature/minha-feature
```

### 5. Abra um Pull Request
- Descreva o que você fez
- Referencie issues relacionadas
- Inclua testes

---

## 📝 Conceitos Aprendidos

Este projeto demonstra:

### Domain-Driven Design (DDD)
- Separação clara entre camadas de domínio, aplicação e infraestrutura
- Repositórios como abstração de persistência
- Use cases como orquestradores de negócio

### Spring Boot Best Practices
- Injeção de dependência
- Configuração via properties
- Auto-configuração do Spring

### Spring Data JPA
- Mapeamento relacional de objetos
- Criação automática de schema
- Queries derivadas

### Spring AI (futuro)
- Chat models e tool calling
- Processamento de áudio
- Integração com APIs de IA

### OpenAPI / Swagger
- Documentação automática de APIs
- Testes interativos via Swagger UI

---

## 🐛 Troubleshooting

### Problema: "Porta 8080 já está em uso"

**Solução:**
```bash
# Usar outra porta
./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=9090"

# Ou matar o processo que está usando a porta
lsof -i :8080  # Linux/Mac
netstat -ano | findstr :8080  # Windows
```

### Problema: "H2 Console não aparece"

**Solução:**
- Verifique `application.properties`:
  ```properties
  spring.h2.console.enabled=true
  ```
- Acesse: `http://localhost:8080/h2-console`

### Problema: Build falha com Java 25

**Solução:**
- Atualize Maven: `mvn --version`
- Limpe cache: `./mvnw clean`
- Recompile: `./mvnw compile`

---

## 📞 Suporte

### Documentação
- [Spring Boot Official Docs](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Spring AI Docs](https://docs.spring.io/spring-ai/reference/index.html)

### Comunidades
- [DIO Community](https://www.dio.me)
- [Spring Community](https://spring.io/community)
- [Stack Overflow](https://stackoverflow.com/questions/tagged/spring-boot)

---

## 📄 Licença

Este projeto está licenciado sob a **MIT License** — veja o arquivo [LICENSE](LICENSE) para detalhes.

---

## 👨‍💻 Autor

**Seu Nome / DIO Spring Boot Track**

- 📧 Email: seu.email@example.com
- 🔗 LinkedIn: [seu-perfil](https://linkedin.com/in/seu-perfil)
- 🐙 GitHub: [@seu-usuario](https://github.com/seu-usuario)

---

## 🎓 Créditos

Projeto educacional desenvolvido como parte do **DIO Spring Boot Learning Track**:
- Expert: **Poiani**
- Trilha: **Spring Boot Expert**
- Desafio Final: **05 — Spring AI**

---

**Última atualização:** 12 de outubro de 2026  
**Status:** Em Desenvolvimento ⚙️  
**Versão:** 0.0.1-SNAPSHOT
