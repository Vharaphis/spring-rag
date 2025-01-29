package fr.efrei.rag.web.rest;

import fr.efrei.rag.domain.Document;
import fr.efrei.rag.repository.DocumentRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/document")
public class DocumentResource {

    private final DocumentRepository documentRepository;

    public DocumentResource(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
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
}