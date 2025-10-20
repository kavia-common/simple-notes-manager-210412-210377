# Notes Backend (Spring Boot)

Production-ready Notes CRUD API with GxP compliance stubs: audit trail, RBAC placeholders, validation, standardized error handling, and OpenAPI docs.

## Run
- Port: 3001
- Build: ./gradlew bootRun
- Swagger UI: http://localhost:3001/swagger-ui.html
- OpenAPI JSON: http://localhost:3001/openapi.json

## Headers
- X-User-Id: user identifier recorded in audit logs (default: "system")
- X-Role: USER or ADMIN (default: USER); DELETE requires ADMIN
- X-Signature: optional electronic signature for DELETE (enforced if feature flag enabled)

## Endpoints
- POST /api/v1/notes
- GET /api/v1/notes
  - Query: title (optional), page, size, sort
- GET /api/v1/notes/{id}
- PUT /api/v1/notes/{id}
- DELETE /api/v1/notes/{id}

### Models
Note:
- id (UUID)
- title (String 1..200)
- content (String 0..10000)
- createdAt, updatedAt (ISO-8601)
- createdBy, updatedBy (String)

ErrorResponse:
- timestamp, path, message, details, errorCode

## GxP Compliance Elements
- Audit Trail: `AuditLog` + `AuditService` records userId, timestamp, action, entity type/id, before/after snapshots, reason and errors.
- Access Control: `@RequiresRole` with interceptor checking `X-Role`.
- Electronic Signature: `X-Signature` header recorded for DELETE; feature flag `notes.features.requireSignatureOnDelete` (default false).
- Validation: Bean Validation annotations on Note and controller parameters; global error mapping.
- Error Handling: `GlobalExceptionHandler` with standardized payload and audit recording for errors.
- Transactions: Write operations (`create`, `update`, `delete`) wrapped with `@Transactional`.

## Testing
- Unit tests: `NoteServiceTest`, `NoteControllerTest`
- Integration smoke: `IntegrationSmokeTest`

## Compliance Checklist (Release Gate)
- [x] All inputs validated
- [x] Audit trail implemented for data modifications
- [x] Unit and controller tests present
- [x] Error handling comprehensive
- [x] Documentation: OpenAPI enabled and this README
- [x] Security controls: RBAC placeholders
- [x] Performance: Pagination support

```json
{
  "traceability": {
    "requirement": "Notes CRUD with GxP",
    "implementation": [
      "NoteController.java",
      "NoteService.java",
      "AuditService.java",
      "GlobalExceptionHandler.java"
    ],
    "tests": [
      "NoteServiceTest.java",
      "NoteControllerTest.java",
      "IntegrationSmokeTest.java"
    ]
  }
}
```
