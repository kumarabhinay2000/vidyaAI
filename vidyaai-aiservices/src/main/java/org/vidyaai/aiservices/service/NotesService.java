package org.vidyaai.aiservices.service;

import org.vidyaai.aiservices.dto.request.NotesRequest;
import org.vidyaai.aiservices.dto.response.NotesResponse;

import java.util.UUID;

public interface NotesService {

    NotesResponse generateNotes(NotesRequest request);

    NotesResponse getNotes(UUID videoId);

    void deleteNotes(UUID videoId);
}