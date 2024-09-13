package org.springframework.ai.openai.samples.helloworld.etl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.ai.vectorstore.observation.AbstractObservationVectorStore;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class VectorStoreInitializer {
    private static final int MAX_TOP_K = 16384;

    private final VectorStore vectorStore;
    private final List<DocumentReader> textDocumentReader;
    /**
     * Capable of deleting vectors from the store. Still generic over the concrete vector store implementation.
     */
    private final AbstractObservationVectorStore abstractObservationVectorStore;

    @PostConstruct
    public void init() {
        List<Document> docs = textDocumentReader.stream()
                .map(DocumentReader::read)
                .flatMap(List::stream)
                .toList();

        Map<String, List<Document>> source = docs.stream()
                .collect(Collectors.groupingBy(doc -> doc.getMetadata().get("source").toString()));

        for (Map.Entry<String, List<Document>> entry : source.entrySet()) {
            String sourceName = entry.getKey();
            List<Document> documents = entry.getValue();

            // Optionally we could check, if the id is already in the vector store
            // The id is simply a hash of the document content
            List<Document> existing = existingDocumentsForSource(sourceName);
            if (existing.size() != documents.size()) {
                abstractObservationVectorStore.delete(existing.stream().map(Document::getId).toList());
                vectorStore.add(documents);
            } else {
                vectorStore.add(documents);
            }
        }
    }

    /**
     * Easiest way to get the existing documents for a source while still using the generic vector store
     */
    private List<Document> existingDocumentsForSource(String source) {
        FilterExpressionBuilder b = new FilterExpressionBuilder();

        return vectorStore.similaritySearch(SearchRequest
                .defaults()
                .withQuery("42")
                .withTopK(MAX_TOP_K)
                .withFilterExpression(b.eq("source", source).build()));
    }
}
