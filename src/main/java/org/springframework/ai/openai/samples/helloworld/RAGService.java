package org.springframework.ai.openai.samples.helloworld;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RAGService {
    private final VectorStore vectorStore;

    private final String prompt = """
            Beantworte die Frage basierend auf den gegebenen Kontext. Die Antwort muss kurz und prägnant sein. Antworte mit "Ich bin mir nicht sicher", wenn Du dir nicht sicher bist.
            Context: {context}
            Answer:
            """;

    public Prompt getPrompt(String question, int topk) {
        List<Document> documents = vectorStore.similaritySearch(
                SearchRequest.defaults()
                        .withQuery(question)
                        .withTopK(topk)
        );
        String context = documents.stream().map(Document::getContent).collect(Collectors.joining("\n"));

        Message userMessage = new UserMessage(question);

        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(prompt);
        Message systemMessage = systemPromptTemplate.createMessage(Map.of("context", context));

        return new Prompt(List.of(userMessage, systemMessage));
    }
}
