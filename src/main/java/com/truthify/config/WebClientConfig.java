package com.truthify.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

	@Value("${truthify.ai.url:http://localhost:5000}")
	private String pythonAiBaseUrl;

	@Bean
	public WebClient webClient(@Value("${truthify.openai.base-url}") String baseUrl) {
		return WebClient.builder().baseUrl(baseUrl).build();
	}
}