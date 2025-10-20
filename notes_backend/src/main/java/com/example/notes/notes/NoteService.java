package com.example.notes.notes;

import com.example.notes.audit.AuditAction;
import com.example.notes.audit.AuditService;
import com.example.notes.config.SecurityStubs.RequiresRole;
import com.example.notes.config.SecurityStubs.RequestContext;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

/**
 * PUBLIC_INTERFACE
 * Service layer for Note CRUD with GxP audit trail.
 */
@Service
public class NoteService {

    private final NoteRepository repository;
    private final AuditService auditService;

    public NoteService(NoteRepository repository, AuditService auditService) {
        this.repository = repository;
        this.auditService = auditService;
    }

    // PUBLIC_INTERFACE
    @Transactional
    @RequiresRole("USER")
    public Note create(@Valid @NotNull Note note) {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        note.setId(null);
        note.setCreatedAt(now);
        note.setUpdatedAt(now);
        note.setCreatedBy(RequestContext.getUser());
        note.setUpdatedBy(RequestContext.getUser());
        Note saved = repository.save(note);
        auditService.record("Note", saved.getId().toString(), AuditAction.CREATE, null, saved, null, null, null);
        return saved;
    }

    // PUBLIC_INTERFACE
    @RequiresRole("USER")
    public Page<Note> getAll(Optional<String> title, int page, int size, Optional<String> sort) {
        Pageable pageable = PageRequest.of(page, size, sort.map(Sort::by).orElse(Sort.by("createdAt").descending()));
        Page<Note> result = title.filter(t -> !t.isBlank())
                .map(t -> repository.findByTitleContainingIgnoreCase(t, pageable))
                .orElseGet(() -> repository.findAll(pageable));
        // READ audit (aggregate) is optional; skip to reduce noise
        return result;
    }

    // PUBLIC_INTERFACE
    @RequiresRole("USER")
    public Note getById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Note not found: " + id));
    }

    // PUBLIC_INTERFACE
    @Transactional
    @RequiresRole("USER")
    public Note update(UUID id, @Valid @NotNull Note input) {
        Note existing = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Note not found: " + id));
        Note before = copy(existing);

        existing.setTitle(input.getTitle());
        existing.setContent(input.getContent());
        existing.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        existing.setUpdatedBy(RequestContext.getUser());

        Note saved = repository.save(existing);
        auditService.record("Note", id.toString(), AuditAction.UPDATE, before, saved, null, null, null);
        return saved;
    }

    // PUBLIC_INTERFACE
    @Transactional
    @RequiresRole("ADMIN")
    public void delete(UUID id, String signature, boolean requireSignature) {
        Note existing = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Note not found: " + id));
        if (requireSignature && (signature == null || signature.isBlank())) {
            throw new IllegalArgumentException("Electronic signature required via X-Signature header");
        }
        Note before = copy(existing);
        repository.delete(existing);
        auditService.record("Note", id.toString(), AuditAction.DELETE, before, null, "User requested delete", signature, null);
    }

    private Note copy(Note src) {
        Note n = new Note();
        BeanUtils.copyProperties(src, n);
        return n;
    }
}
