package com.example.notes.audit;

import com.example.notes.config.SecurityStubs.RequestContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

/**
 * PUBLIC_INTERFACE
 * Service to record audit logs for CRUD operations and errors.
 */
@Service
public class AuditService {

    private final EntityManager entityManager;
    private final ObjectMapper mapper = new ObjectMapper();

    public AuditService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    // PUBLIC_INTERFACE
    @Transactional
    public void record(String entityType, String entityId, AuditAction action, Object before, Object after, String reason, String signature, String error) {
        AuditLog log = new AuditLog();
        log.setTimestamp(OffsetDateTime.now(ZoneOffset.UTC));
        log.setUserId(RequestContext.getUser());
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setBeforeState(toJsonSafe(before));
        log.setAfterState(toJsonSafe(after));
        log.setReason(reason);
        log.setSignature(signature);
        log.setError(error);
        entityManager.persist(log);
    }

    private String toJsonSafe(Object o) {
        if (o == null) return null;
        try {
            return mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            return "{\"_serializationError\":\"" + e.getMessage().replace("\"","'") + "\"}";
        }
    }
}
