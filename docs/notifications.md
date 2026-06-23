# API de Notificaciones

API sencilla para el envío de notificaciones, construida con Spring Boot 4.1 (Java 21), JPA y PostgreSQL. Este documento cubre el uso (Parte 1) y el diseño de pruebas QA + análisis (Parte 2).

## Arquitectura

```
controller  -> NotificationController        Endpoints REST, validación, mapeo HTTP
service     -> NotificationService           Orquestación: validar canal, enviar, persistir
provider    -> NotificationProvider          Interfaz Strategy (una implementación por canal)
               EmailNotificationProvider
               SmsNotificationProvider
               NotificationProviderFactory   Factory: selecciona el proveedor según el canal
model       -> Notification, Channel         Entidad JPA + enum de canal
repository  -> NotificationRepository        Persistencia del historial vía JPA
dto         -> NotificationRequest/Response, DataResponse
exception   -> GlobalExceptionHandler        Errores JSON consistentes 400/500
```

### Patrón de diseño: Strategy + Factory

Cada canal tiene su propia implementación de `NotificationProvider` (la **Strategy**). Spring inyecta todos los proveedores en `NotificationProviderFactory`, que los indexa por `Channel` y devuelve el correcto en tiempo de ejecución (la **Factory**). Agregar un nuevo canal (por ejemplo, push) solo requiere un nuevo componente proveedor — sin cambios en el código existente (principio abierto/cerrado).

### Persistencia

Cada envío exitoso se almacena como una fila `Notification`, conformando el historial que devuelve `GET /notifications`. En ejecución se usa PostgreSQL (`application.properties`); las pruebas automatizadas usan H2 en memoria (`src/test/resources/application.properties`) para que se ejecuten sin una base de datos externa.

## Ejecución

Requiere una instancia de PostgreSQL acorde a `src/main/resources/application.properties` (`localhost:5432/university`, usuario `postgres`).

```bash
./mvnw spring-boot:run
```

### Endpoints

#### POST /notifications

Envía una notificación y la almacena. Todos los campos son obligatorios; `channel` debe ser `email` o `sms`.

Petición:
```json
{ "userId": "123", "message": "Hola", "channel": "email" }
```

Respuesta `201 Created`:
```json
{ "id": 1, "userId": "123", "message": "Hola", "channel": "email", "createdAt": "2026-06-23T15:00:00Z" }
```

Validación / canal inválido → `400 Bad Request`:
```json
{ "timestamp": "...", "status": 400, "error": "Bad Request",
  "message": "Invalid channel 'WhatsApp'. Supported channels: email, sms.", "details": [] }
```

#### GET /notifications

Devuelve el historial completo, del más reciente al más antiguo.

Respuesta `200 OK`:
```json
{ "data": [ { "id": 2, "userId": "456", "message": "Hi", "channel": "sms", "createdAt": "..." } ] }
```

### Ejemplos con curl

```bash
curl -X POST localhost:8080/notifications -H "Content-Type: application/json" \
  -d '{"userId":"123","message":"Hola","channel":"email"}'

curl -X POST localhost:8080/notifications -H "Content-Type: application/json" \
  -d '{"userId":"123","message":"Hola","channel":"whatsapp"}'   # 400

curl localhost:8080/notifications
```

---

## Parte 2: QA + Automatización

### Diseño de casos de prueba

#### POST /notifications

| #  | Caso                                  | Entrada                              | Esperado                              |
|----|---------------------------------------|--------------------------------------|---------------------------------------|
| 1  | Email válido                          | `{userId, message, channel:"email"}` | 201, cuerpo persistido, canal `email` |
| 2  | SMS válido                            | `{userId, message, channel:"sms"}`   | 201, canal `sms`                      |
| 3  | Canal sin distinción de mayúsculas    | `channel:"EMAIL"`                    | 201 (normalizado a `email`)           |
| 4  | Canal inválido                        | `channel:"whatsapp"`                 | 400, mensaje de canales soportados    |
| 5  | Falta message                         | `{userId, channel}`                  | 400, error de validación              |
| 6  | Falta userId                          | `{message, channel}`                 | 400                                   |
| 7  | Falta channel                         | `{userId, message}`                  | 400                                   |
| 8  | Campos en blanco                      | `{"":"","":""}`                      | 400                                   |
| 9  | JSON malformado / content-type errado | cuerpo inválido                      | 400                                   |
| 10 | Efecto secundario de persistencia     | POST válido y luego GET              | el registro aparece en el historial   |

#### GET /notifications

| # | Caso              | Precondición                  | Esperado                              |
|---|-------------------|-------------------------------|---------------------------------------|
| 1 | Historial vacío   | sin envíos                    | 200, `{"data": []}`                   |
| 2 | Devuelve envíos   | 2 POSTs previos               | 200, `data` tiene 2 elementos         |
| 3 | Ordenamiento      | envíos en distintos momentos  | del más reciente al más antiguo       |
| 4 | Forma de respuesta| cualquiera                    | los items tienen `userId, message, channel` |

### Pruebas automatizadas

Implementadas con JUnit 5 + Spring MockMvc contra H2 — se ejecutan con `./mvnw test`.

- `NotificationControllerTest` ($5$ casos): POST de email válido, POST de sms válido, canal inválido → 400, campo faltante → 400, ida y vuelta de GET que devuelve las notificaciones creadas.
- `NotificationProviderFactoryTest` ($3$ casos): proveedor correcto por canal, error cuando no hay proveedor registrado.

### Análisis

**¿Qué automatizarías primero y por qué?**
La ruta de escritura de `POST /notifications`: validación de campos obligatorios, validación de canal, selección de proveedor y persistencia. Concentra todas las reglas de negocio, es la de mayor riesgo (datos erróneos o un proveedor incorrecto tienen consecuencias reales) y es la precondición de `GET` — automatizarla primero protege el comportamiento central y habilita las pruebas de ida y vuelta de GET.

**¿Qué riesgos identificas en esta API?**
- **Sin autenticación/autorización** — cualquiera puede enviar notificaciones como cualquier `userId`.
- **Sin límite de tasa / protección contra abuso** — expuesta a spam y a costos descontrolados en canales reales.
- **Sin garantías reales de entrega** — los proveedores son "fire-and-forget", sin reintentos, manejo de cola de mensajes muertos (dead-letter), ni seguimiento del estado de entrega.
- **Entrada sin límites** — la longitud de `message` y el tamaño del payload solo están débilmente restringidos.
- **Crecimiento ilimitado del historial** — `GET` devuelve toda la tabla sin paginación.
- **Sin idempotencia** — un POST reintentado crea envíos duplicados.
- **Credenciales de BD en texto plano** en `application.properties` — deberían provenir de secretos/variables de entorno.
- **Acoplamiento de canales** — solo `email`/`sms`; los nuevos canales requieren un (pequeño) cambio de código + despliegue.