package com.example.notes.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

/**
 * PUBLIC_INTERFACE
 * Standardized error payload for API responses.
 */
public class ErrorResponse {
    @Schema(description = "Timestamp in ISO-8601", example = "2024-01-01T12:00:00Z")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private OffsetDateTime timestamp;

    @Schema(description = "Request path", example = "/api/v1/notes")
    private String path;

    @Schema(description = "High-level error message")
    private String message;

    @Schema(description = "Detailed information")
    private String details;

    @Schema(description = "Stable error code", example = "VALIDATION_ERROR")
    private String errorCode;

    public ErrorResponse() {}

    public ErrorResponse(OffsetDateTime timestamp, String path, String message, String details, String errorCode) {
        this.timestamp = timestamp;
        this.path = path;
        this.message = message;
        this.details = details;
        this.errorCode = errorCode;
    }

    // Builder pattern
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private OffsetDateTime timestamp;
        private String path;
        private String message;
        private String details;
        private String errorCode;

        public Builder timestamp(OffsetDateTime timestamp) { this.timestamp = timestamp; return this; }
        public Builder path(String path) { this.path = path; return this; }
        public Builder message(String message) { this.message = message; return this; }
        public Builder details(String details) { this.details = details; return this; }
        public Builder errorCode(String errorCode) { this.errorCode = errorCode; return this; }
        public ErrorResponse build() { return new ErrorResponse(timestamp, path, message, details, errorCode); }
    }

    // Getters and setters
    public OffsetDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(OffsetDateTime timestamp) { this.timestamp = timestamp; }
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
}
