package rag.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.AdvisedRequest;
import org.springframework.ai.chat.client.advisor.api.RequestAdvisor;
import org.springframework.ai.chat.client.advisor.api.ResponseAdvisor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RAGAdvisor implements RequestAdvisor, ResponseAdvisor {
    private static final String PROMPT = """
            Beantworte die Frage basierend auf den gegebenen Kontext. Die Antwort muss kurz und prägnant sein. Antworte mit "Ich bin mir nicht sicher", wenn Du dir nicht sicher bist.
            Kontext: {context}
            """;

    private final VectorStore vectorStore;

    @Override
    public AdvisedRequest adviseRequest(AdvisedRequest request, Map<String, Object> adviseContext) {
        // Find relevant documents
        List<Document> documents = vectorStore.similaritySearch(
                SearchRequest.defaults()
                        .withQuery(request.userText())
                        .withTopK(10)
        );
        List<String> files = documents.stream()
                .map(doc -> doc.getMetadata().get("source"))
                .filter(Objects::nonNull)
                .map(Object::toString)
                .distinct()
                .collect(Collectors.toList());
        String context = documents.stream()
                .map(Document::getContent)
                .collect(Collectors.joining("\n"));

        // Extend Params
        Map<String, Object> advisedSystemParams = new HashMap<>(request.systemParams());
        adviseContext.put("context-used", context);
        adviseContext.put("files-used", files);
        advisedSystemParams.put("context", context);

        return AdvisedRequest.from(request)
                .withSystemText(PROMPT)
                .withSystemParams(advisedSystemParams)
                .build();
    }

    /**
     * Adds metadata which could, for example, be used to show which documents were used to generate the response.
     */
    @Override
    public ChatResponse adviseResponse(ChatResponse response, Map<String, Object> adviseContext) {
        return ChatResponse.builder().from(response)
                .withMetadata("context-used", adviseContext.get("context-used"))
                .withMetadata("files-used", adviseContext.get("files-used"))
                .build();
    }

    @Override
    public String getName() {
        return "RAGAdvisor";
    }
}
