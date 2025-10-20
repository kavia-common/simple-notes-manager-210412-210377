package com.example.notes.audit;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * PUBLIC_INTERFACE
 * GxP audit log capturing user, action, entity info, and state snapshots.
 */
@Entity
@Table(name = "audit_logs", indexes = {
        @Index(name = "idx_audit_entity", columnList = "entityType,entityId"),
        @Index(name = "idx_audit_time", columnList = "timestamp")
})
public class AuditLog {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private OffsetDateTime timestamp;

    @Column(nullable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private AuditAction action;

    @Column(nullable = false, length = 64)
    private String entityType;

    @Column(nullable = false, length = 64)
    private String entityId;

    @Lob
    @Column(columnDefinition = "CLOB")
    private String beforeState;

    @Lob
    @Column(columnDefinition = "CLOB")
    private String afterState;

    private String reason;

    private String error;

    private String signature; // optional X-Signature for critical ops

    // Getters and setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public OffsetDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(OffsetDateTime timestamp) { this.timestamp = timestamp; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public AuditAction getAction() { return action; }
    public void setAction(AuditAction action) { this.action = action; }

    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }

    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }

    public String getBeforeState() { return beforeState; }
    public void setBeforeState(String beforeState) { this.beforeState = beforeState; }

    public String getAfterState() { return afterState; }
    public void setAfterState(String afterState) { this.afterState = afterState; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public String getSignature() { return signature; }
    public void setSignature(String signature) { this.signature = signature; }
}
