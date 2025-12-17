package com.truthify.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class OpenAiClientConfig {

    @Bean(name = "openAiWebClient")
    public WebClient openAiWebClient(
            @Value("${truthify.openai.base-url}") String baseUrl,
            @Value("${truthify.openai.api-key}") String apiKey
    ) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
