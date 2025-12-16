# Notificações de Consultas (RabbitMQ) — Arquitetura e Operação

## Objetivo
Notificar pacientes um dia antes da consulta. O sistema publica uma mensagem na criação da consulta e processa as notificações diariamente com tolerância a reagendamentos e atrasos.

## Componentes
- Exchange principal `consultation.notifications.exchange` (Direct).
- Fila principal `consultation.notifications` para consumo imediato.
- Exchange de delay `consultation.notifications.delay.exchange` (Direct).
- Fila de delay `consultation.notifications.delay` com `x-message-ttl` e DLX apontando para a exchange principal.
- Publisher `ConsultationNotificationPublisher` com envio imediato e com delay.
- Consumer `@RabbitListener` em `NotificationSchedulerService`.

## Configuração (Spring)
- Conversor JSON com `JavaTimeModule`.
- `RabbitAdmin` habilitado (`autoStartup=true`) para declarar exchanges, filas e bindings ao subir a aplicação.
- Propriedades:
  - `notifications.queue`, `notifications.exchange`, `notifications.routing-key`
  - `notifications.delay.queue`, `notifications.delay.exchange`, `notifications.delay.routing-key`, `notifications.delay.ttl-ms`

## Fluxo
1. Criação da consulta (GraphQL): publica `ConsultationNotificationMessage` na exchange principal.
2. Consumer assíncrono:
   - Carrega a consulta do banco e valida estado.
   - Se já `SENT`, não reenvia.
   - Se não for “de amanhã” pela data atual da consulta, reencaminha para a fila de delay.
   - Em caso de erro, reencaminha para delay.
   - Marca `notificationAttempts`, `notificationStatus` e `notificationSentAt`.
3. Agendador diário às 08:00:
   - Marca consultas do dia seguinte com `notificationStatus = SCHEDULED`.
4. Polling da fila de delay:
   - Um scheduler (`fixedDelay`) lê periodicamente a fila de delay.
   - Quando a mensagem tiver atingido o horário definido em `processAfter`, ela é reprocessada.
   - Alternativamente, o broker encaminha automaticamente pelo TTL para a exchange principal.

## Idempotência e Retentativas
- Idempotência: checagem de `notificationStatus == SENT`.
- Retentativas: contador `notificationAttempts` e reencaminhamento para delay em falhas.

## Docker
- `rabbitmq:3.13-management` com healthcheck.
- A aplicação declara automaticamente a fila e exchanges via `RabbitAdmin` ao iniciar.

## API de Teste
- Endpoint `POST /notifications/test/{id}?delayMs=300000`
  - Se a consulta estiver `SENT`, loga e retorna 200.
  - Caso contrário, enfileira na fila de delay para processamento futuro, define `processAfter` e retorna 202.
  - A aplicação fará o polling da fila de delay e logará quando a mensagem estiver pronta para processamento.

## Variáveis de Ambiente
```env
RABBITMQ_HOST=rabbitmq
RABBITMQ_USER=admin
RABBITMQ_PASSWORD=admin
RABBITMQ_PORT=5672
NOTIFICATIONS_CRON="0 0 8 * * *"
```

## Testes
- Atualizados para validar publisher com exchange e método de delay.
- Consumer testado via chamada direta ao método `consumeNotification`.
