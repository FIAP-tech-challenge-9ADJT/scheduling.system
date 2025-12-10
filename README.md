# FIAP Tech Challenge - Scheduling System

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.3-brightgreen)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)
![Docker](https://img.shields.io/badge/Docker-✓-blue)
![Maven](https://img.shields.io/badge/Maven-4.0.0-red)
![Flyway](https://img.shields.io/badge/Flyway-✓-green)
![GraphQL](https://img.shields.io/badge/GraphQL-✓-e10098)

Este projeto é a resolução do Tech Challenge da FIAP - Fase 3, desenvolvido em: Java, Spring Boot, MySQL, GraphQL e Docker.

## 🚀 Funcionalidades Implementadas

### ✅ Requisitos da Especificação Atendidos:

#### 1. **Segurança em Aplicações Java**
- ✅ Autenticação com Spring Security e JWT
- ✅ Níveis de acesso por perfil (Médicos, Enfermeiros, Pacientes, Administradores)
- ✅ Autorização granular nos endpoints REST e GraphQL

#### 2. **Consultas e Histórico do Paciente com GraphQL**
- ✅ **Implementação completa de GraphQL** para consultas flexíveis
- ✅ **Listar todos os atendimentos** de um paciente
- ✅ **Consultar apenas consultas futuras**
- ✅ **Filtros avançados** por médico, data, paciente
- ✅ **Paginação eficiente** com cursor-based pagination
- ✅ **Serviço de Agendamento** via GraphQL mutations
- ✅ **Verificação de slots disponíveis** para agendamento
- ✅ **Operações de reagendamento e cancelamento**

## Como Utilizar

Esta seção fornece instruções completas para configurar e executar a aplicação.

### Pré-requisitos

- Docker e Docker Compose
- Java 21 (para desenvolvimento local)
- Maven (para build local)

### Variáveis de Ambiente

Crie um arquivo `.env` na raiz do projeto com as seguintes variáveis ou copie o arquivo `.env.example` fornecido:

```bash
cp .env.example .env
```

Em seguida, edite o arquivo `.env` com seus valores:

```env
MYSQL_DATABASE=nome_do_banco
MYSQL_USER=usuario
MYSQL_PASSWORD=senha
MYSQL_ROOT_PASSWORD=senha_root
```

### Executando a Aplicação

#### Implantação com Docker

Execute o container:

```bash
docker-compose up -d --build
```

> Lembre-se de substituir as variáveis no arquivo `.env` com os valores apropriados para o seu ambiente.

Isso iniciará:

- **Banco de dados MySQL** na porta 3306
- **Aplicação Spring Boot** na porta 8080
- **PHPMyAdmin** na porta 8081 para gerenciamento do banco de dados

#### Acessando a Aplicação

- **API REST**: http://localhost:8080
- **GraphQL Playground**: http://localhost:8080/graphiql
- **GraphQL Endpoint**: http://localhost:8080/graphql
- **PHPMyAdmin**: http://localhost:8081
  - Servidor: mysql
  - Usuário: [valor de MYSQL_USER]
  - Senha: [valor de MYSQL_PASSWORD]

### Desenvolvimento Local

Para desenvolvimento local sem Docker:

1. Configure um banco de dados MySQL local na porta 3306

2. Configure as variáveis de ambiente ou crie um arquivo `.env` na raiz do projeto

3. Execute a aplicação:

```bash
mvn spring-boot:run
```

## Arquitetura do Projeto

### Estrutura Docker

O projeto utiliza um processo de build em múltiplas etapas:

1. **Etapa de cache**: Baixa as dependências Maven
2. **Etapa de build**: Compila a aplicação
3. **Etapa de execução**: Executa a aplicação em um container JRE

### Perfis de Configuração

- **local**: Configuração padrão para desenvolvimento local
- **docker**: Configuração otimizada para execução em containers Docker

### Banco de Dados

O projeto utiliza MySQL 8.0 com Flyway para migrações. As migrações estão localizadas em `src/main/resources/db/migration`.

## 📊 Exemplos de Uso do GraphQL

### Consultar Histórico de um Paciente

```graphql
query {
  consultationsByPatient(patientId: 1) {
    id
    dateTime
    description
    notes
    patient {
      name
      email
    }
    doctor {
      name
      crm
    }
  }
}
```

### Consultar Apenas Consultas Futuras

```graphql
query {
  futureConsultations(patientId: 1) {
    id
    dateTime
    description
    doctor {
      name
    }
  }
}
```

### Consulta Flexível com Filtros

```graphql
query {
  consultations(filter: {
    startDate: "2024-01-01T00:00:00"
    endDate: "2024-12-31T23:59:59"
    first: 10
  }) {
    edges {
      node {
        id
        dateTime
        description
        patient { name }
        doctor { name }
      }
    }
    pageInfo {
      hasNextPage
      hasPreviousPage
    }
    totalCount
  }
}
```

### Agendar Nova Consulta

```graphql
mutation {
  createConsultation(input: {
    patientId: 1
    doctorId: 2
    nurseId: 3
    dateTime: "2024-12-15T14:00:00"
    description: "Consulta de rotina"
    notes: "Paciente com histórico de hipertensão"
  }) {
    id
    dateTime
    description
  }
}
```

### Atualizar Consulta Existente

```graphql
mutation {
  updateConsultation(id: 1, input: {
    dateTime: "2024-12-15T15:00:00"
    notes: "Reagendado a pedido do paciente"
  }) {
    id
    dateTime
    notes
  }
}
```

## Comando auxliares

- Buildar o projeto sem cache

```bash
docker-compose build --no-cache
```

- Reparar checksums das migrações (`flyway:repair`)

```bash
docker run --rm -it \
  --env-file .env \
  -v "$PWD":/app \
  -w /app \
  --network host \
  maven:3.9.7-eclipse-temurin-21 \
  bash -c "chmod +x ./mvnw && ./mvnw flyway:repair \
    -Dflyway.url=jdbc:mysql://localhost:3306/\$MYSQL_DATABASE \
    -Dflyway.user=\$MYSQL_USER \
    -Dflyway.password=\$MYSQL_PASSWORD"
```

## 📣 Notificações de Consultas (RabbitMQ)

### Objetivo

- Notificar pacientes um dia antes da consulta agendada. A notificação é preparada no ato da criação da consulta e processada diariamente às 08:00.

### Arquitetura e Componentes

- Fila RabbitMQ: `consultation.notifications` (configurável via `notifications.queue`).
- Publicação de mensagens ao criar consultas:
  - `ConsultationGraphQLController#createConsultation` cria o registro e publica a mensagem com dados da consulta e paciente.
  - Publisher: `ConsultationNotificationPublisher`.
- Processamento diário:
  - `NotificationSchedulerService` possui duas rotinas às 08:00:
    - Marca consultas de amanhã com `notificationStatus = SCHEDULED`.
    - Processa a fila, verifica se a consulta é de amanhã, envia a notificação (simulada) e atualiza `notificationStatus`, `notificationSentAt` e `notificationAttempts`.
- Modelo:
  - `ConsultationHistory` inclui os campos de notificação: `notificationStatus`, `notificationSentAt`, `notificationAttempts`.
- Configuração:
  - `RabbitMQConfig` configura `Queue`, `RabbitTemplate` e conversor JSON com suporte a `LocalDateTime`.

### Variáveis de Ambiente (RabbitMQ)

Adicione as seguintes variáveis ao `.env` conforme seu ambiente:

```env
RABBITMQ_HOST=rabbitmq
RABBITMQ_USER=admin
RABBITMQ_PASSWORD=admin
RABBITMQ_PORT=5672

# Horário do Scheduler de Notificações (formato cron Spring)
NOTIFICATIONS_CRON="0 0 8 * * *" # padrão 08:00 diariamente
```

E opcionalmente habilite TLS em produção:

```env
SPRING_RABBITMQ_SSL_ENABLED=true
# SPRING_RABBITMQ_SSL_ALGORITHM=TLSv1.2
# SPRING_RABBITMQ_SSL_KEY-STORE=... 
# SPRING_RABBITMQ_SSL_KEY-STORE-PASSWORD=...
# SPRING_RABBITMQ_SSL_TRUST-STORE=...
# SPRING_RABBITMQ_SSL_TRUST-STORE-PASSWORD=...
```

### Migrações

- `V4__Add_notification_columns.sql` adiciona as colunas:
  - `notification_status VARCHAR(20)`, `notification_sent_at DATETIME`, `notification_attempts INT DEFAULT 0`.
- As migrações executam automaticamente ao iniciar a aplicação com Flyway habilitado.

### Como Usar

1. Suba a stack com Docker:
   
   ```bash
   docker-compose up -d --build
   ```

2. Crie uma consulta via GraphQL com `dateTime` para o dia de amanhã:

   ```graphql
   mutation {
     createConsultation(input: {
       patientId: 1
       doctorId: 2
       nurseId: 3
       dateTime: "2025-12-10T14:00:00"
       description: "Consulta"
       notes: ""
     }) {
       id
       dateTime
       description
     }
   }
   ```

3. O sistema publicará uma mensagem na fila. No horário configurado em `NOTIFICATIONS_CRON` (por padrão 08:00) do dia anterior à consulta:
   - Marcará a consulta com `notificationStatus = SCHEDULED`.
   - Consumirá a fila e enviará a notificação (simulada), atualizando `notificationStatus = SENT`, `notificationSentAt` e incrementando `notificationAttempts`.

4. Verifique os logs:

   ```bash
   docker-compose logs -f app
   ```

5. Acesse o painel do RabbitMQ (opcional):

   - Broker: `http://localhost:5672`
   - Management UI: `http://localhost:15672` (login: `RABBITMQ_USER` / senha: `RABBITMQ_PASSWORD`)

### Idempotência, Retentativas e Logs

- Idempotência: uma consulta com `notificationStatus = SENT` não é notificada novamente.
- Retentativas: incrementa `notificationAttempts` e reencaminha mensagens em falhas, marcando `FAILED` quando aplicável.
- Logs registram todas as operações de agendamento, publicação, consumo e erros.

### Testes

- Unitários:
  - `ConsultationNotificationPublisherTest` valida publicação na fila.
  - `NotificationSchedulerServiceTest` valida marcação `SCHEDULED` e envio `SENT`.
- Execução:
  
  ```bash
  ./mvnw test
  ```

### Troubleshooting

- Erros de conexão RabbitMQ:
  - Verifique `RABBITMQ_HOST`, `RABBITMQ_USER`, `RABBITMQ_PASSWORD`.
  - Confirme que o serviço `rabbitmq` está saudável (`docker-compose ps`).
- Mensagens não processadas:
  - Certifique-se de que a consulta foi criada com `dateTime` para amanhã.
  - Veja os logs às 08:00 para confirmar processamento.
