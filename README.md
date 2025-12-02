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
