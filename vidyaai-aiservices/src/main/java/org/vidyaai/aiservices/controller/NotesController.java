package org.vidyaai.aiservices.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.vidyaai.aiservices.dto.request.NotesRequest;
import org.vidyaai.aiservices.dto.response.ApiResponse;
import org.vidyaai.aiservices.dto.response.NotesResponse;
import org.vidyaai.aiservices.service.NotesService;

import java.util.UUID;

@RestController
@RequestMapping("/api/ai/notes")
@RequiredArgsConstructor
@Slf4j
public class NotesController {

    private final NotesService notesService;

    @PostMapping("/generate")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<NotesResponse>> generate(
            @Valid @RequestBody NotesRequest request) {
        log.info("POST /api/ai/notes/generate for videoId={}", request.getVideoId());
        NotesResponse response = notesService.generateNotes(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Notes generated"));
    }

    @GetMapping("/{videoId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<NotesResponse>> getNotes(@PathVariable UUID videoId) {
        log.info("GET /api/ai/notes/{}", videoId);
        NotesResponse response = notesService.getNotes(videoId);
        return ResponseEntity.ok(ApiResponse.success(response, "Notes fetched"));
    }

    @DeleteMapping("/{videoId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deleteNotes(@PathVariable UUID videoId) {
        log.info("DELETE /api/ai/notes/{}", videoId);
        notesService.deleteNotes(videoId);
        return ResponseEntity.ok(ApiResponse.success(null, "Notes deleted"));
    }
}