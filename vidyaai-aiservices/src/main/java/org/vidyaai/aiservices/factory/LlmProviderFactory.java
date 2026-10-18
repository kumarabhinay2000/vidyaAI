package org.vidyaai.aiservices.factory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Component;
import org.vidyaai.aiservices.enums.LlmProvider;
import org.vidyaai.aiservices.exception.LlmProviderException;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class LlmProviderFactory {

    private final Map<LlmProvider, ChatClient> chatClients= new EnumMap<>(LlmProvider.class);

    public LlmProviderFactory(ChatModel chatModel, List<ChatModel> chatModels) {

        try {
            this.chatClients.put(LlmProvider.GROQ, ChatClient.builder(chatModel).build());

            log.info("Registered {} LLM Provider client(s)", chatClients.size());
        } catch(Exception e) {
            log.error("Failed to initialize LLM Provider factory: {}",e.getMessage(), e);
            throw new LlmProviderException("GROQ","Failed to initalize LLM Providers ", e);
        }
    }

    public ChatClient getClient(LlmProvider provider) {
        ChatClient client = chatClients.get(provider);
        if (client == null) {
            throw new LlmProviderException(provider.getCode(),
                    "No ChatClient registered for provider: " + provider);
        }
        return client;
    }

    public ChatClient getDefaultClient() {
        return getClient(LlmProvider.GROQ);
    }

    public boolean isAvailable(LlmProvider provider) {

        return chatClients.containsKey(provider);
    }
}