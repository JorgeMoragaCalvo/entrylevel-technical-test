# technicaltest — API de Notificaciones

API REST sencilla para el envío de notificaciones (`email` / `sms`), con persistencia del historial de envíos. Construida con **Spring Boot 4.1**, **Java 21**, **JPA** y **PostgreSQL**.

## Requisitos

- Java 21
- PostgreSQL en ejecución (`localhost:5432/university`, usuario `postgres`) — ver `src/main/resources/application.properties`

## Ejecución

```bash
./mvnw spring-boot:run
```

La aplicación queda disponible en `http://localhost:8080`.

## Endpoints

- `POST /notifications` — envía una notificación y la guarda. Campos obligatorios: `userId`, `message`, `channel` (`email` o `sms`).
- `GET /notifications` — devuelve el historial completo, del más reciente al más antiguo.

```bash
curl -X POST localhost:8080/notifications -H "Content-Type: application/json" \
  -d '{"userId":"123","message":"Hola","channel":"email"}'

curl localhost:8080/notifications
```

## Pruebas

```bash
./mvnw test
```

Las pruebas usan H2 en memoria, por lo que no requieren una base de datos externa.

## Documentación

La documentación detallada (arquitectura, contratos de los endpoints y diseño de pruebas QA + análisis) está en [`docs/notifications.md`](docs/notifications.md).