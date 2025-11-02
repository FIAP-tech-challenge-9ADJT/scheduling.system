# FIAP Tech Challenge - Scheduling System

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.3-brightgreen)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)
![Docker](https://img.shields.io/badge/Docker-✓-blue)
![Maven](https://img.shields.io/badge/Maven-4.0.0-red)
![Flyway](https://img.shields.io/badge/Flyway-✓-green)

Este projeto é a resolução do Tech Challenge da FIAP, desenvolvido em: Java, Spring Boot, MySQL e Docker.

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

- Banco de dados MySQL na porta 3306
- Aplicação Spring Boot na porta 8080
- PHPMyAdmin na porta 8081 para gerenciamento do banco de dados

#### Acessando a Aplicação

- API: http://localhost:8080
- PHPMyAdmin: http://localhost:8081
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
