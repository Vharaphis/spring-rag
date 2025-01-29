package fr.efrei.rag.web.rest;

import fr.efrei.rag.domain.Document;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/document")
public class DocumentResource {

    private final List<Document> documents = new ArrayList<>();

    @GetMapping("/all")
    public List<Document> getAllDocuments() {
        return documents;
    }

    @GetMapping("/{documentId}")
    public Document getDocumentById(@PathVariable Long documentId) {
        return documents.stream().filter(doc -> doc.getId().equals(documentId)).findFirst().orElse(null);
    }

    @PostMapping("/create")
    public Document createDocument(@RequestBody Document document) {
        documents.add(document);
        return document;
    }

    @PutMapping("/update/{documentId}")
    public Document updateDocument(@PathVariable Long documentId, @RequestBody Document updatedDocument) {
        for (int i = 0; i < documents.size(); i++) {
            if (documents.get(i).getId().equals(documentId)) {
                Document doc = documents.get(i);
                doc.setTitle(updatedDocument.getTitle());
                doc.setDescription(updatedDocument.getDescription());
                doc.setAuthor(updatedDocument.getAuthor());
                doc.setPublisher(updatedDocument.getPublisher());
                doc.setPublishedDate(updatedDocument.getPublishedDate());
                doc.setContentType(updatedDocument.getContentType());
                return doc;
            }
        }
        return null;
    }

    @DeleteMapping("/delete/{documentId}")
    public void deleteDocument(@PathVariable Long documentId) {
        documents.removeIf(doc -> doc.getId().equals(documentId));
    }
}
