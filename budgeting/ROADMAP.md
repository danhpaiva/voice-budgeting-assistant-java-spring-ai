# 🗺️ ROADMAP — Voice Budgeting Assistant

**Status Atual:** v1.0.0 (Completo)  
**Data:** 2026-10-12

---

## 🎯 Visão do Projeto

Transformar o Voice Budgeting Assistant em uma plataforma **production-ready** com:
- ✅ Voice commands completos (Whisper + TTS)
- ✅ Autenticação e autorização
- ✅ Persistência em banco real
- ✅ Testes 100% cobertos
- ✅ Deploy em produção

---

## 📅 Roadmap 2026

### 🔴 Q4 2026 (Imediato - Próximas 4 semanas)

#### Sprint 1: Voice Completo (Whisper + TTS)
- [ ] Implementar Whisper API (Transcrição)
  - Endpoint `/ai/transcribe` funcional
  - Suporte para múltiplos formatos (MP3, WAV, M4A)
  - Teste com áudios reais
- [ ] Implementar TTS API (Síntese de Voz)
  - Endpoint `/ai/speak` funcional
  - Vozes em português
  - Cache de respostas
- [ ] Integrar fluxo completo
  - `/ai/voice` com Whisper → Chat → TTS
  - Resposta em áudio MP3

**Estimativa:** 2 semanas  
**Prioridade:** 🔴 ALTA

#### Sprint 2: Testes Unitários
- [ ] Testes para Domain Layer
  - Transaction model
  - TransactionType enum
- [ ] Testes para Application Layer
  - CreateTransactionUseCase
  - GetTransactionsUseCase
- [ ] Testes para Infrastructure Layer
  - TransactionController
  - GlobalExceptionHandler
- [ ] Cobertura mínima: 80%

**Estimativa:** 1.5 semanas  
**Prioridade:** 🔴 ALTA

---

### 🟡 Q1 2027 (Próximos 3 meses)

#### Sprint 3: Autenticação e Autorização
- [ ] Spring Security Configuration
  - User roles (admin, user)
  - JWT authentication
- [ ] User Management
  - User entity
  - Registration endpoint
  - Login endpoint
- [ ] Authorization
  - API key protection
  - User-scoped transactions
- [ ] Testes de segurança

**Estimativa:** 3 semanas  
**Prioridade:** 🟡 MÉDIA

#### Sprint 4: Database Real
- [ ] Migrate H2 → PostgreSQL
  - Schema design
  - Migration scripts
- [ ] Connection Pool (HikariCP)
- [ ] Backup strategy
- [ ] Performance testing

**Estimativa:** 1.5 semanas  
**Prioridade:** 🟡 MÉDIA

#### Sprint 5: Advanced Features
- [ ] Multi-language support
  - PT-BR, EN
  - i18n configuration
- [ ] Transaction Categories
- [ ] Budget planning
- [ ] Reports and Analytics
- [ ] Export (CSV, PDF)

**Estimativa:** 2.5 semanas  
**Prioridade:** 🟡 MÉDIA

---

### 🟢 Q2 2027+ (Longo prazo)

#### Phase: DevOps & Deployment
- [ ] Docker containerization
  - Dockerfile multi-stage
  - docker-compose.yml
- [ ] Kubernetes deployment
  - Helm charts
  - Service definition
  - Ingress configuration
- [ ] CI/CD Pipeline
  - GitHub Actions
  - Auto tests
  - Auto deploy
- [ ] Monitoring & Logging
  - Prometheus metrics
  - Grafana dashboards
  - ELK stack

**Estimativa:** 4 semanas

#### Phase: Performance & Scale
- [ ] Redis Cache
  - Chat responses
  - Frequently accessed data
- [ ] Database Optimization
  - Indexes
  - Query optimization
- [ ] Load Testing
  - JMeter/K6
  - Identify bottlenecks
- [ ] API Rate Limiting
  - Spring Cloud Gateway

**Estimativa:** 3 semanas

#### Phase: Mobile App
- [ ] React Native app
  - Voice input
  - Real-time updates
- [ ] Push notifications
- [ ] Offline support
- [ ] App store publishing

**Estimativa:** 8-12 weeks

---

## 📋 Detalhes de Cada Sprint

### Sprint 1: Voice Completo

**User Stories**

```gherkin
Feature: Transcrição de Áudio
  Scenario: Transcrever áudio em português
    Given: Áudio em português válido
    When: POST /ai/transcribe com arquivo
    Then: Retorna texto transcrito
    And: Status 200 OK

Feature: Síntese de Voz
  Scenario: Gerar áudio a partir de texto
    Given: Texto em português
    When: POST /ai/speak com texto
    Then: Retorna MP3
    And: Content-Type: audio/mpeg

Feature: Fluxo Completo de Voz
  Scenario: Processar comando de voz
    Given: Áudio com comando
    When: POST /ai/voice
    Then: Transcreve
    And: Processa com Chat
    And: Gera resposta em áudio
```

**Tarefas Técnicas**

- [ ] Adicionar dependências de audio
- [ ] Implementar `AudioTranscriptionService`
- [ ] Implementar `TextToSpeechService`
- [ ] Integrar com `AiVoiceController`
- [ ] Testes de integração
- [ ] Documentação atualizada

---

### Sprint 2: Testes Unitários

**Test Files a Criar**

```
src/test/java/
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
│       ├── AiVoiceControllerTest.java
│       └── GlobalExceptionHandlerTest.java
```

**Cobertura Esperada**

- Domain Layer: 100%
- Application Layer: 100%
- Infrastructure Layer: 80%
- Overall: 85%

---

### Sprint 3: Autenticação

**Security Configuration**

```yaml
Endpoints:
  Public:
    - POST /auth/register
    - POST /auth/login
    - GET /swagger-ui/**
  Protected:
    - POST /transactions
    - GET /transactions
    - POST /ai/chat
    - POST /ai/voice
```

**User Entity**

```java
public record User(
    UUID id,
    String username,
    String email,
    String passwordHash,
    Set<Role> roles,
    LocalDateTime createdAt
)

enum Role {
    USER,
    ADMIN,
    MODERATOR
}
```

---

### Sprint 4: PostgreSQL

**Migration Plan**

1. Setup PostgreSQL locally
2. Create Spring Data Migration scripts
3. Performance testing vs H2
4. Production deployment

**Connection Pool**

```properties
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.url=jdbc:postgresql://localhost:5432/budgeting
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=validate
```

---

## 🎯 Success Criteria

### v1.1.0 (Voice Complete)
- ✅ Whisper fully functional
- ✅ TTS fully functional
- ✅ 80% test coverage
- ✅ All voice endpoints tested

### v1.2.0 (Security)
- ✅ Spring Security integrated
- ✅ JWT authentication working
- ✅ User scoped transactions
- ✅ Security tests passing

### v2.0.0 (Production Ready)
- ✅ PostgreSQL in production
- ✅ Kubernetes deployed
- ✅ CI/CD automated
- ✅ 95% test coverage
- ✅ Monitoring enabled

---

## 💰 Resource Requirements

### Team
- 1x Backend Developer (Full-time)
- 1x DevOps Engineer (Part-time)
- 1x QA Engineer (Part-time)
- (Optional) 1x Mobile Developer

### Infrastructure
- Development: Local machine
- Staging: AWS EC2
- Production: AWS ECS or Kubernetes

### Tools & Services
- GitHub (Code)
- GitHub Actions (CI/CD)
- AWS (Hosting)
- PostgreSQL (Database)
- Sentry (Error tracking)
- DataDog (Monitoring)

---

## ⚠️ Risks & Mitigation

| Risk | Probability | Impact | Mitigation |
|------|-------------|--------|-----------|
| Whisper API failures | Medium | High | Fallback to text input |
| TTS quality issues | Low | Medium | Test with various texts |
| Authentication bugs | Medium | High | Security audit + tests |
| DB migration problems | Low | High | Backup & test migration |
| Performance degradation | Medium | Medium | Load testing early |

---

## 📊 Metrics & KPIs

### Technical Metrics
- Build time: < 1 min
- Test coverage: > 80%
- API latency: < 500ms
- Voice latency: < 2s

### User Metrics
- Monthly active users (future)
- Average session duration
- Command success rate
- Chat satisfaction score

---

## 🔄 Version Strategy

```
v1.0.0 - Foundation (Current)
├── v1.1.0 - Voice Complete
├── v1.2.0 - Security
├── v1.3.0 - Database Migration
└── v2.0.0 - Production Ready
    ├── v2.1.0 - Mobile App
    ├── v2.2.0 - Advanced Analytics
    └── v3.0.0 - AI Improvements
```

---

## 📝 Maintenance Schedule

### Weekly
- [ ] Monitor error logs
- [ ] Check API latency
- [ ] Review open issues

### Monthly
- [ ] Security updates
- [ ] Dependency updates
- [ ] Performance review
- [ ] User feedback review

### Quarterly
- [ ] Major version planning
- [ ] Architecture review
- [ ] Capacity planning

---

## 🤝 Community & Contributions

### Open Source (Planned)
- GitHub public repository
- Contributing guidelines
- Issue templates
- Pull request templates

### Documentation
- API documentation
- Architecture decision records (ADRs)
- Deployment guides
- Troubleshooting guides

---

## 📞 Contact & Support

### Development
- GitHub Issues: Feature requests, bugs
- Discussions: Questions, ideas

### Support (Future)
- Email support
- Chat support
- Community forum

---

## 🎉 Conclusion

**Voice Budgeting Assistant** tem um futuro promissor com:
- ✅ Funcionalidades voice completas
- ✅ Segurança enterprise
- ✅ Performance otimizada
- ✅ Experiência mobile

**Tempo estimado até v2.0.0:** 4-6 meses  
**Time estimado:** 2-3 pessoas

---

**Este roadmap é um guia vivo e pode ser atualizado conforme as necessidades evoluem.**

**Última atualização:** 2026-10-12  
**Próxima revisão:** 2026-11-12
