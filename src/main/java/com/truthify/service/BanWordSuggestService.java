package com.truthify.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.truthify.dao.AdTextMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BanWordSuggestService {

    private final AdTextMapper adTextMapper;

    @Qualifier("openAiWebClient")
    private final WebClient openAiWebClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${truthify.openai.model:gpt-4o-mini}")
    private String model;

    public List<Suggestion> suggestFromHighRiskAds(double minRisk, int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 20)); // 비용/길이 제한
        List<String> samples = adTextMapper.findHighRiskSamples(minRisk, safeLimit);

        if (samples == null || samples.isEmpty()) return List.of();

        String input = buildPromptInput(samples);

        Map<String, Object> schema = buildSchema();

        Map<String, Object> body = Map.of(
                "model", model,
                "input", List.of(
                        Map.of("role", "developer", "content",
                                "You extract Korean ad-risk keywords for compliance screening. " +
                                "Return ONLY JSON matching schema. Avoid overly generic words. " +
                                "Prefer short phrases used in ads (2~15 chars)."),
                        Map.of("role", "user", "content", input)
                ),
                "text", Map.of("format", Map.of(
                        "type", "json_schema",
                        "name", "truthify_banword_suggestions",
                        "strict", true,
                        "schema", schema
                ))
        );

        JsonNode res = openAiWebClient.post()
                .uri("/responses")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        String jsonText = res != null && res.hasNonNull("output_text")
                ? res.get("output_text").asText()
                : extractTextFromOutput(res);

        try {
            SuggestionResponse parsed = objectMapper.readValue(jsonText, SuggestionResponse.class);

            // keyword 중복 제거(대소/공백 정리)
            Map<String, Suggestion> dedup = new LinkedHashMap<>();
            for (Suggestion s : parsed.suggestions) {
                if (s == null || s.keyword == null) continue;
                String k = s.keyword.trim();
                if (k.isEmpty()) continue;
                dedup.putIfAbsent(k, new Suggestion(k, s.lawType, s.description, s.confidence));
            }
            return new ArrayList<>(dedup.values());

        } catch (Exception e) {
            return List.of();
        }
    }

    private String buildPromptInput(List<String> samples) {
        // 너무 길면 비용/품질 떨어져서 각 샘플을 잘라서 넣기
        List<String> trimmed = samples.stream()
                .map(s -> s == null ? "" : s.strip())
                .filter(s -> !s.isBlank())
                .map(s -> s.length() > 600 ? s.substring(0, 600) : s)
                .collect(Collectors.toList());

        StringBuilder sb = new StringBuilder();
        sb.append("Below are high-risk Korean ad texts. ")
          .append("Extract candidate banned/suspicious phrases used in ads.\n\n");

        for (int i = 0; i < trimmed.size(); i++) {
            sb.append("AD#").append(i + 1).append(":\n")
              .append(trimmed.get(i)).append("\n\n");
        }

        sb.append("Rules:\n")
          .append("- Suggest 5~20 phrases.\n")
          .append("- Each keyword must be a phrase that appears in ads (not abstract).\n")
          .append("- Provide lawType among: FOOD_SAFETY, FAIR_TRADE, FINANCIAL, MEDICAL, ETC.\n")
          .append("- Provide short description.\n")
          .append("- confidence is 0.0~1.0.\n");

        return sb.toString();
    }

    private Map<String, Object> buildSchema() {
        return Map.of(
                "type", "object",
                "additionalProperties", false,
                "properties", Map.of(
                        "suggestions", Map.of(
                                "type", "array",
                                "items", Map.of(
                                        "type", "object",
                                        "additionalProperties", false,
                                        "properties", Map.of(
                                                "keyword", Map.of("type", "string"),
                                                "lawType", Map.of("type", "string"),
                                                "description", Map.of("type", "string"),
                                                "confidence", Map.of("type", "number")
                                        ),
                                        "required", List.of("keyword", "lawType", "description", "confidence")
                                )
                        )
                ),
                "required", List.of("suggestions")
        );
    }

    private String extractTextFromOutput(JsonNode res) {
        if (res == null || !res.has("output") || !res.get("output").isArray()) return "";
        StringBuilder sb = new StringBuilder();
        for (JsonNode item : res.get("output")) {
            if (!item.has("content") || !item.get("content").isArray()) continue;
            for (JsonNode c : item.get("content")) {
                if (c.has("type") && "output_text".equals(c.get("type").asText()) && c.has("text")) {
                    sb.append(c.get("text").asText());
                }
            }
        }
        return sb.toString();
    }

    public static class SuggestionResponse {
        public List<Suggestion> suggestions = List.of();
    }

    public static class Suggestion {
        public String keyword;
        public String lawType;       // enum 이름 문자열로 받기
        public String description;
        public double confidence;

        public Suggestion() {}
        public Suggestion(String keyword, String lawType, String description, double confidence) {
            this.keyword = keyword;
            this.lawType = lawType;
            this.description = description;
            this.confidence = confidence;
        }
    }
}
