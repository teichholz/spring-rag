package rag;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
class AIController {
    @Autowired
    private ChatClient chatClient;

    @Qualifier("defaultClient")
    @Autowired
    private ChatClient defaultChatClient;

    @GetMapping("/q")
    Map<String, String> question(@RequestParam(value = "message") String message) {
        ChatResponse res = chatClient.prompt()
                .user(message)
                .call().chatResponse();

        return Map.of(
                "completion", res.getResult().getOutput().getContent(),
                "files", res.getMetadata().get("files-used").toString()
        );
    }

    @GetMapping("/test")
    public Map<String, Object> embed(@RequestParam(value = "answer") String answer, @RequestParam(value = "solution") String solution) {
        String system = """
                Deine Aufgabe ist es, die generierte Antwort mit der optimalen Antwort zu vergleichen und zu bestimmen wie passend die generierte Antwort ist.
                Du Antwortest mit einer Dezimalzahl zwischen 0 und 1, wobei 0 bedeutet, dass die generierte Antwort überhaupt nicht passt und 1 bedeutet, dass die generierte Antwort perfekt passt."
                """;

        String user =
                """
                Generierte Anwort: %s
                Optimale Antwort: %s
                """;

        String res = defaultChatClient.prompt()
                .system(system)
                .user(user.formatted(answer, solution))
                .call()
                .content();

        return Map.of("score", Double.parseDouble(res));
    }
}
