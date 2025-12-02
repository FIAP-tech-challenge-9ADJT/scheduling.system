# 🧪 Guia de Testes - Sistema de Agendamento Hospitalar

## 📋 Pré-requisitos

1. Docker e Docker Compose instalados
2. Postman ou Insomnia (opcional)
3. Navegador web para acessar GraphiQL

## 🚀 Como Executar

### 1. Iniciar os Serviços

```bash
cd scheduling.system
docker-compose up -d --build
```

### 2. Verificar se os Serviços Estão Rodando

- **Aplicação**: http://localhost:8080
- **GraphQL Playground**: http://localhost:8080/graphiql
- **PHPMyAdmin**: http://localhost:8081

### 3. Aguardar Inicialização Completa

Aguarde até que todos os serviços estejam saudáveis (pode levar 1-2 minutos).

## 🔐 Autenticação

### Primeiro, faça login para obter o token JWT:

**Endpoint**: `POST http://localhost:8080/auth/login`

**Body**:
```json
{
  "login": "admin",
  "password": "admin123"
}
```

**Response**:
```json
{
  "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9..."
}
```

⚠️ **Use este token no header Authorization**: `Bearer {token}`

## 📊 Testando GraphQL

### Acesse o GraphQL Playground

1. Vá para: http://localhost:8080/graphiql
2. Adicione o header de autorização:
   ```
   {
     "Authorization": "Bearer SEU_TOKEN_AQUI"
   }
   ```

### Exemplos de Queries

#### 1. Listar Consultas de um Paciente

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

#### 2. Consultar Apenas Consultas Futuras

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

#### 3. Consulta Flexível com Filtros e Paginação

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

#### 4. Verificar Slots Disponíveis

```graphql
query {
  availableSlots(doctorId: 1, date: "2024-12-15") {
    dateTime
    available
    duration
  }
}
```

### Exemplos de Mutations

#### 1. Criar Nova Consulta

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

#### 2. Atualizar Consulta

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

#### 3. Cancelar Consulta

```graphql
mutation {
  cancelConsultation(id: 1, reason: "Paciente não pode comparecer")
}
```

## 📊 Testando Funcionalidades GraphQL

### 1. Verificar Slots Disponíveis

Use a query `availableSlots` para verificar horários disponíveis:

```graphql
query {
  availableSlots(doctorId: 1, date: "2024-12-15") {
    dateTime
    available
    duration
  }
}
```

### 2. Verificar Logs da Aplicação

```bash
docker-compose logs -f app
```

Você deve ver logs das operações GraphQL e consultas ao banco de dados.

## 📝 Collection do Postman

Importe o arquivo `postman_collection.json` no Postman para ter todos os exemplos prontos.

### Variáveis de Ambiente no Postman:

- `base_url`: `http://localhost:8080`
- `token`: (cole o token obtido no login)

## ✅ Checklist de Testes

### Requisitos da Especificação:

- [ ] **GraphQL funcionando** - Acesse http://localhost:8080/graphiql
- [ ] **Consultas flexíveis** - Teste as queries de exemplo
- [ ] **Listar todos os atendimentos** - Query `consultationsByPatient`
- [ ] **Apenas consultas futuras** - Query `futureConsultations`
- [ ] **Agendamento por enfermeiros** - Mutation `createConsultation`
- [ ] **Modificação por médicos** - Mutation `updateConsultation`
- [ ] **Slots disponíveis** - Query `availableSlots`
- [ ] **Paginação e filtros** - Query `consultations` com filtros

### Segurança:

- [ ] **Autenticação JWT** - Login obrigatório
- [ ] **Autorização por perfil** - Diferentes permissões
- [ ] **Pacientes só veem suas consultas** - Teste com token de paciente

## 🔧 Troubleshooting

### Problema: Erro de conexão com banco

```bash
docker-compose down
docker-compose up -d mysql
# Aguarde 30 segundos
docker-compose up -d app
```

### Problema: GraphQL não responde

```bash
docker-compose logs app
# Verifique se não há erros de inicialização
```

### Problema: GraphQL não carrega

- Verifique se a aplicação está rodando: http://localhost:8080/actuator/health
- Acesse diretamente: http://localhost:8080/graphql

## 📞 Suporte

Se encontrar problemas:

1. Verifique os logs: `docker-compose logs -f`
2. Reinicie os serviços: `docker-compose restart`
3. Limpe e reconstrua: `docker-compose down && docker-compose up -d --build`
