package org.springframework.ai.openai.samples.helloworld;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
class AIController {
    private final ChatClient chatClient;
    private final EmbeddingModel embeddingModel;


    @GetMapping("/ai")
    Map<String, String> completion(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        return Map.of(
                "completion",
                chatClient.prompt()
                        .user(message)
                        .call()
                        .content());
    }

    @GetMapping("/q")
    Map<String, String> question(@RequestParam(value = "message") String message, @RequestParam(value = "topk", defaultValue = "4") int topk) {
        ChatResponse res = chatClient.prompt()
                .user(message)
                .call().chatResponse();

        return Map.of(
                "completion", res.getResult().getOutput().getContent(),
                "files used", res.getMetadata().get("files-used").toString()
        );
    }

    @GetMapping("/ai/embedding")
    public Map<String, Object> embed(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        EmbeddingResponse embeddingResponse = embeddingModel.embedForResponse(List.of(message));
        return Map.of("embedding", embeddingResponse);
    }
}
