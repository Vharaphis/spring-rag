package fr.efrei.rag.service;

import fr.efrei.rag.domain.Document;
import fr.efrei.rag.repository.DocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DocumentService {

    private final Logger log = LoggerFactory.getLogger(DocumentService.class);
    private final DocumentRepository documentRepository;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public List<Document> getAllDocuments() {
        log.debug("Request to get all Documents");
        return documentRepository.findAll();
    }

    public Optional<Document> getDocumentById(Long id) {
        log.debug("Request to get Document with id: {}", id);
        return documentRepository.findById(id);
    }

    public Document createDocument(Document document) {
        log.debug("Request to create Document: {}", document);
        return documentRepository.save(document);
    }

    public Document updateDocument(Long id, Document updatedDocument) {
        log.debug("Request to update Document with id: {}", id);
        return documentRepository.findById(id).map(document -> {
            document.setTitle(updatedDocument.getTitle());
            document.setDescription(updatedDocument.getDescription());
            document.setAuthor(updatedDocument.getAuthor());
            document.setPublisher(updatedDocument.getPublisher());
            document.setPublishedDate(updatedDocument.getPublishedDate());
            document.setContentType(updatedDocument.getContentType());
            return documentRepository.save(document);
        }).orElse(null);
    }

    public void deleteDocument(Long id) {
        log.debug("Request to delete Document with id: {}", id);
        documentRepository.deleteById(id);
    }

    public List<Document> findAll() {
        log.debug("Request to find all Documents");
        return documentRepository.findAll();
    }
}