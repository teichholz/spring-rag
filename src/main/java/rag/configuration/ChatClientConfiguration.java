package rag.configuration;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
class ChatClientConfiguration {

    @Autowired
    private RAGAdvisor ragAdvisor;

    @Primary
    @Bean
    ChatClient ragClient(ChatClient.Builder builder) {
        return builder
                .defaultAdvisors(
//                        new PromptChatMemoryAdvisor(chatMemory),
                        ragAdvisor,
                        new SimpleLoggerAdvisor())
                .build();
    }

    @Bean
    ChatClient defaultClient(ChatClient.Builder builder) {
        return builder.build();
    }

}
