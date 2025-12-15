package com.server.controller;

import com.server.entity.VerificationDocument;
import com.server.repository.VerificationDocumentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documents")
@Slf4j
public class DocumentController {

    @Autowired
    private VerificationDocumentRepository documentRepository;

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable UUID id) {
        log.info("Request to download document with ID: {}", id);
        return documentRepository.findById(id)
                .map(doc -> {
                    String filename = doc.getDocumentType() != null ? doc.getDocumentType().replace(" ", "_")
                            : "document";
                    // Append extension based on content if possible, or default to bin/pdf logic if
                    // we knew it.
                    // For now, simpler approach or try to guess.
                    // The entity doesn't seem to store content type or filename, defaulting to
                    // specific behavior or standard valid name.
                    // Assuming PDF for checking earlier instructions or generic response.
                    // Let's use "document" and let browser/client handle or just set
                    // inline/attachment.

                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                            .contentType(MediaType.APPLICATION_OCTET_STREAM) // Or specific type if stored
                            .body(doc.getFileContent());
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
