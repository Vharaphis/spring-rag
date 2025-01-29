package fr.efrei.rag.web.rest;

import fr.efrei.rag.domain.Document;
import fr.efrei.rag.repository.DocumentRepository;
import fr.efrei.rag.service.DocumentAiService;
import fr.efrei.rag.service.DocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequestMapping("/api/document")
public class DocumentResource {

    private final Logger log = LoggerFactory.getLogger(DocumentResource.class);
    private final DocumentRepository documentRepository;
    private final ExecutorService nonBlockingService = Executors.newCachedThreadPool();

    private final DocumentService documentService;
    private final DocumentAiService documentAiService;

    public DocumentResource(DocumentRepository documentRepository, DocumentService documentService, DocumentAiService documentAiService) {
        this.documentRepository = documentRepository;
        this.documentService = documentService;
        this.documentAiService = documentAiService;
    }

    @GetMapping("/all")
    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }

    @GetMapping("/{documentId}")
    public Optional<Document> getDocumentById(@PathVariable Long documentId) {
        return documentRepository.findById(documentId);
    }

    @PostMapping("/create")
    public Document createDocument(@RequestBody Document document) {
        return documentRepository.save(document);
    }

    @PutMapping("/update/{documentId}")
    public Document updateDocument(@PathVariable Long documentId, @RequestBody Document updatedDocument) {
        return documentRepository.findById(documentId).map(document -> {
            document.setTitle(updatedDocument.getTitle());
            document.setDescription(updatedDocument.getDescription());
            document.setAuthor(updatedDocument.getAuthor());
            document.setPublisher(updatedDocument.getPublisher());
            document.setPublishedDate(updatedDocument.getPublishedDate());
            document.setContentType(updatedDocument.getContentType());
            return documentRepository.save(document);
        }).orElse(null);
    }

    @DeleteMapping("/delete/{documentId}")
    public void deleteDocument(@PathVariable Long documentId) {
        documentRepository.deleteById(documentId);
    }

    @PostMapping("/documents/chat/{user}")
    public String chat(@PathVariable UUID user, @RequestBody String query) throws InterruptedException {
        SseEmitter emitter = new SseEmitter();
        AtomicBoolean completed = new AtomicBoolean(false);
        AtomicReference<String> myMessage = new AtomicReference<>();
        nonBlockingService.execute(() -> documentAiService.chat(user, query)
                .onNext(message -> {
                    try {
                        sendMessage(emitter, message);
                        myMessage.set(message);
                    }
                    catch (IOException e) {
                        log.error("Error while writing next token", e);
                        emitter.completeWithError(e);
                    }
                })
                .onComplete(token -> {
                    emitter.complete();
                    completed.set(true);
                })
                .onError(error -> {
                    log.error("Unexpected chat error", error);
                    try {
                        sendMessage(emitter, error.getMessage());
                    }
                    catch (IOException e) {
                        log.error("Error while writing next token", e);
                    }
                    emitter.completeWithError(error);
                })
                .start());
        while (!completed.get()) {
            Thread.sleep(1000);
        }
//        return emitter;
        return myMessage.get();
    }

    @PostMapping("/documents/chat2/{user}")
    public String chat2(@RequestBody String query) throws InterruptedException {
        String result = documentService.chat(query);

        return result;
    }

    private static void sendMessage(SseEmitter emitter, String message) throws IOException {
        String token = message
                // Hack line break problem when using Server Sent Events (SSE)
                .replace("\n", "<br>")
                // Escape JSON quotes
                .replace("\"", "\\\"");
        emitter.send("{\"t\": \"" + token + "\"}");
    }
}