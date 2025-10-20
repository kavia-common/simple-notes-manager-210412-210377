package com.example.notes.notes;

import com.example.notes.audit.AuditService;
import com.example.notes.common.GlobalExceptionHandler;
import com.example.notes.config.SecurityStubs;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NoteController.class)
@Import({GlobalExceptionHandler.class, SecurityStubs.RoleInterceptorConfig.class})
class NoteControllerTest {

    @Resource
    private MockMvc mvc;

    @MockBean
    private NoteService service;

    @MockBean
    private AuditService auditService;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        // Default headers are set in each request
    }

    @Test
    void create_ValidationError_BlankTitle() throws Exception {
        Note bad = new Note();
        bad.setTitle(" "); // invalid
        bad.setContent("c");
        mvc.perform(post("/api/v1/notes")
                        .header("X-Role", "USER")
                        .header("X-User-Id", "tester")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bad)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
    }

    @Test
    void list_Success() throws Exception {
        Mockito.when(service.getAll(any(), anyInt(), anyInt(), any()))
                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 20), 0));
        mvc.perform(get("/api/v1/notes")
                        .header("X-Role", "USER")
                        .header("X-User-Id", "tester"))
                .andExpect(status().isOk());
    }

    @Test
    void delete_ForbidForUserRole() throws Exception {
        mvc.perform(delete("/api/v1/notes/" + UUID.randomUUID())
                        .header("X-Role", "USER")
                        .header("X-User-Id", "tester"))
                .andExpect(status().isForbidden());
    }
}
