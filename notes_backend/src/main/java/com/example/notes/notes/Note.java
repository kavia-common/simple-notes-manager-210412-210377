package com.example.notes.notes;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * PUBLIC_INTERFACE
 * Note entity persisted via JPA.
 */
@Entity
@Table(name = "notes", indexes = {
        @Index(name = "idx_note_title", columnList = "title")
})
public class Note {

    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank(message = "title must not be blank")
    @Size(min = 1, max = 200, message = "title length must be between 1 and 200")
    @Column(nullable = false, length = 200)
    private String title;

    @Size(max = 10000, message = "content length must be <= 10000")
    @Column(length = 10000)
    private String content;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    @Column(nullable = false, length = 128)
    private String createdBy;

    @Column(nullable = false, length = 128)
    private String updatedBy;

    // Getters and setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
}
