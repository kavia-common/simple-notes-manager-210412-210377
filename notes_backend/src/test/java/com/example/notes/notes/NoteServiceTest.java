package com.example.notes.notes;

import com.example.notes.audit.AuditService;
import com.example.notes.config.SecurityStubs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class NoteServiceTest {

    @Autowired
    private NoteService service;

    @Autowired
    private NoteRepository repository;

    @Autowired
    private AuditService auditService;

    @BeforeEach
    void setUp() {
        SecurityStubs.RequestContext.setUser("tester");
        SecurityStubs.RequestContext.setRole("ADMIN");
        repository.deleteAll();
    }

    @Test
    void createReadUpdateDelete_HappyPath() {
        Note n = new Note();
        n.setTitle("Test");
        n.setContent("Content");

        Note created = service.create(n);
        assertNotNull(created.getId());
        assertEquals("tester", created.getCreatedBy());

        Note got = service.getById(created.getId());
        assertEquals("Test", got.getTitle());

        Note upd = new Note();
        upd.setTitle("Updated");
        upd.setContent("New");
        Note updated = service.update(created.getId(), upd);
        assertEquals("Updated", updated.getTitle());

        assertDoesNotThrow(() -> service.delete(created.getId(), null, false));
        assertThrows(EntityNotFoundException.class, () -> service.getById(created.getId()));
    }

    @Test
    void update_NotFound_Throws() {
        Note upd = new Note();
        upd.setTitle("x");
        upd.setContent("y");
        assertThrows(EntityNotFoundException.class, () -> service.update(java.util.UUID.randomUUID(), upd));
    }
}
