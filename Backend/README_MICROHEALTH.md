MicroHealth Backend - Quickstart

This repo contains the MicroHealth (VitaTrack prototype) Spring Boot backend.

Quick run (dev)

1. Build

```powershell
cd C:\Users\mudit\Downloads\Backend\Backend
.\mvnw.cmd -DskipTests package
```

2. Run

```powershell
java -jar target\Backend-0.0.1-SNAPSHOT.jar
```

Local API endpoints
- POST /patient/register — registers patient. JSON: { "email":"...", "name":"...", "password":"..." }
- POST /auth/login — logs in and returns a token. JSON: { "email":"...", "password":"..." }
- POST /patient/uploadRecord — upload health record (requires Authorization: Bearer <token>)

Example cURL (register)

```powershell
curl -X POST "http://localhost:8080/patient/register" -H "Content-Type: application/json" -d '{"email":"alice@example.com","name":"Alice","password":"secret123"}'
```

Login and use the token

```powershell
$resp = curl -s -X POST "http://localhost:8080/auth/login" -H "Content-Type: application/json" -d '{"email":"alice@example.com","password":"secret123"}'
# parse token from JSON using jq (Windows: use appropriate tool) or inspect response
```

Example upload with token (PowerShell)

```powershell
$token = "<copy token here>"
curl -X POST "http://localhost:8080/patient/uploadRecord" -H "Authorization: Bearer $token" -H "Content-Type: application/json" -d '{"patientId":1, "heartRate":78, "oxygen":98.5}'
```

WebSocket (STOMP) example
- Endpoint: ws://localhost:8080/ws-alerts?token=<jwt>
- After connecting, subscribe to topic: /topic/alerts/{doctorId}
- Example using a JS STOMP client:

```javascript
const socket = new SockJS('http://localhost:8080/ws-alerts?token=' + token);
const client = Stomp.over(socket);
client.connect({}, frame => {
  client.subscribe('/topic/alerts/2', msg => {
    console.log('Alert', JSON.parse(msg.body));
  });
});
```

Postman
- A Postman collection `postman_collection_microhealth.json` is included in the repo root. Import it into Postman and set `{{baseUrl}}` and use the login response token.

Notes
- The OpenAPI UI is available at: http://localhost:8080/swagger-ui.html (after running)
- Configure `frontend.origin` in `application.properties` to allow your frontend origin for CORS.
- JWT secret must be set in `application.properties` as `jwt.secret` for production.

Next steps I can take
- Harden auth and add role-based guards for WebSocket subscriptions.
- Create safe DTO responses (avoid returning entity with password).
- Add automated tests for alerts and reminders.

