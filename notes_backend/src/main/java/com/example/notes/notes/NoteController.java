package com.example.notes.notes;

import com.example.notes.config.SecurityStubs.RequiresRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

/**
 * PUBLIC_INTERFACE
 * REST controller for Note CRUD endpoints.
 */
@RestController
@RequestMapping("/api/v1/notes")
@Tag(name = "Notes")
@Validated
public class NoteController {

    private final NoteService service;

    @Value("${notes.features.requireSignatureOnDelete:false}")
    private boolean requireSignatureOnDelete;

    public NoteController(NoteService service) {
        this.service = service;
    }

    // PUBLIC_INTERFACE
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create note", description = "Creates a new note with title and content", responses = {
            @ApiResponse(responseCode = "201", description = "Created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Note.class))),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    @RequiresRole("USER")
    public ResponseEntity<Note> create(@Valid @RequestBody Note note) {
        Note created = service.create(note);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // PUBLIC_INTERFACE
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "List notes", description = "List notes with pagination and optional title search", responses = {
            @ApiResponse(responseCode = "200", description = "OK")
    })
    @RequiresRole("USER")
    public Page<Note> list(
            @RequestParam(name = "title", required = false) @Size(max = 200) String title,
            @RequestParam(name = "page", defaultValue = "0") @Min(0) int page,
            @RequestParam(name = "size", defaultValue = "20") @Min(1) int size,
            @RequestParam(name = "sort", required = false) String sort
    ) {
        return service.getAll(Optional.ofNullable(title), page, size, Optional.ofNullable(sort));
    }

    // PUBLIC_INTERFACE
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get note by id", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @RequiresRole("USER")
    public Note get(@PathVariable("id") UUID id) {
        return service.getById(id);
    }

    // PUBLIC_INTERFACE
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update note", description = "Updates an existing note", responses = {
            @ApiResponse(responseCode = "200", description = "Updated"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    @RequiresRole("USER")
    public Note update(@PathVariable("id") UUID id, @Valid @RequestBody Note note) {
        return service.update(id, note);
    }

    // PUBLIC_INTERFACE
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete note", description = "Deletes a note; optionally requires electronic signature via X-Signature header", responses = {
            @ApiResponse(responseCode = "204", description = "Deleted"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @RequiresRole("ADMIN")
    public ResponseEntity<Void> delete(
            @PathVariable("id") UUID id,
            @RequestHeader(value = "X-Signature", required = false)
            @Parameter(description = "Electronic signature for critical operations") String signature
    ) {
        service.delete(id, signature, requireSignatureOnDelete);
        return ResponseEntity.noContent().build();
    }
}
