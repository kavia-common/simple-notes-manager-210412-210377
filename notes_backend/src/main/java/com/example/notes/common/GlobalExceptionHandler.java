package com.example.notes.common;

import com.example.notes.audit.AuditAction;
import com.example.notes.audit.AuditService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.OffsetDateTime;

/**
 * PUBLIC_INTERFACE
 * Centralized exception handler to map exceptions to standardized error responses.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private final AuditService auditService;

    public GlobalExceptionHandler(AuditService auditService) {
        this.auditService = auditService;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        String details = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + " " + fe.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("Validation failed");
        return build(req, HttpStatus.BAD_REQUEST, "Validation error", details, "VALIDATION_ERROR", ex);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraint(ConstraintViolationException ex, HttpServletRequest req) {
        return build(req, HttpStatus.BAD_REQUEST, "Constraint violation", ex.getMessage(), "CONSTRAINT_VIOLATION", ex);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(EntityNotFoundException ex, HttpServletRequest req) {
        return build(req, HttpStatus.NOT_FOUND, "Not found", ex.getMessage(), "NOT_FOUND", ex);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest req) {
        return build(req, HttpStatus.CONFLICT, "Data integrity violation", ex.getMostSpecificCause().getMessage(), "DATA_INTEGRITY", ex);
    }

    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<ErrorResponse> handleTransaction(TransactionSystemException ex, HttpServletRequest req) {
        return build(req, HttpStatus.BAD_REQUEST, "Transaction error", ex.getMostSpecificCause().getMessage(), "TRANSACTION_ERROR", ex);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest req) {
        return build(req, HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", ex.getMessage(), "INTERNAL_ERROR", ex);
    }

    private ResponseEntity<ErrorResponse> build(HttpServletRequest req,
                                                HttpStatus status,
                                                String message,
                                                String details,
                                                String code,
                                                Exception ex) {
        ErrorResponse body = ErrorResponse.builder()
                .timestamp(OffsetDateTime.now())
                .path(req.getRequestURI())
                .message(message)
                .details(details)
                .errorCode(code)
                .build();
        // Record in audit trail as ERROR
        try {
            auditService.record("SYSTEM", "-", AuditAction.ERROR, null, null, null, null,
                    ex.getClass().getSimpleName() + ": " + ex.getMessage());
        } catch (Exception ignore) {
            // Avoid masking original error
        }
        return ResponseEntity.status(status).body(body);
    }
}
