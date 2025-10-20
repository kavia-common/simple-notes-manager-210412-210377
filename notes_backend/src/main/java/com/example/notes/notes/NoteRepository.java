package com.example.notes.notes;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * PUBLIC_INTERFACE
 * Repository for Note entity.
 */
public interface NoteRepository extends JpaRepository<Note, UUID> {
    Page<Note> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}
