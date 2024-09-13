package org.springframework.ai.openai.samples.helloworld.etl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.ai.vectorstore.observation.AbstractObservationVectorStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Responsible for initializing the vector store with all the relevant documents.
 * If the amount of found {@link Document} in a file differ from before, the old documents are deleted and the new ones are added.
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(value = "initialize")
public class VectorStoreInitializer {
    private static final int MAX_TOP_K = 16384;

    private final VectorStore vectorStore;
    /**
     * Inject all the {@link DocumentReader} beans to read the documents.
     */
    private final List<DocumentReader> textDocumentReader;
    /**
     * Capable of deleting vectors from the store. Still generic over the concrete vector store implementation.
     */
    private final AbstractObservationVectorStore abstractObservationVectorStore;

    @PostConstruct
    public void ensureVectorStoreIsInitialized() {
        List<Document> docs = textDocumentReader.stream()
                .map(DocumentReader::read)
                .flatMap(List::stream)
                .toList();

        Map<String, List<Document>> source = docs.stream()
                .collect(Collectors.groupingBy(doc -> doc.getMetadata().get("source").toString()));

        for (Map.Entry<String, List<Document>> entry : source.entrySet()) {
            String sourceName = entry.getKey();
            List<Document> documents = entry.getValue();
            List<Document> existing = existingDocumentsForSource(sourceName);

            if (documentsDiffer(existing, documents)) {
                abstractObservationVectorStore.delete(existing.stream().map(Document::getId).toList());
                vectorStore.add(documents);
            } else {
                vectorStore.add(documents);
            }
        }
    }

    private boolean documentsDiffer(Collection<Document> a, Collection<Document> b) {
        var as = a.stream().map(Document::getId).collect(Collectors.toSet());
        var bs = b.stream().map(Document::getId).collect(Collectors.toSet());
        return !as.equals(bs);
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
