package rag.configuration;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class ChatClientConfiguration {

    @Autowired
    private RAGAdvisor ragAdvisor;

    @Bean
    ChatClient chatClient(ChatClient.Builder builder) {
        return builder
                .defaultAdvisors(
//                        new PromptChatMemoryAdvisor(chatMemory),
                        ragAdvisor,
                        new SimpleLoggerAdvisor())
                .build();
    }

}
