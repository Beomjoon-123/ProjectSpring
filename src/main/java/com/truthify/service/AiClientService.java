package com.truthify.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.truthify.dto.AnalysisResultDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiClientService {

    @Qualifier("openAiWebClient")
    private final WebClient openAiWebClient;
    private final ObjectMapper objectMapper;

    @Value("${truthify.openai.model:gpt-4o-mini}")
    private String model;

    public AnalysisResultDto analyze(String adContent) {
        try {
            Map<String, Object> schema = buildTruthifySchema();

            String developerMsg =
            		"당신은 한국어 광고 심의/컴플라이언스 분석기입니다. " +
            			   "반드시 제공된 JSON 스키마에 맞는 JSON만 반환하세요. " +
            			    "detailDescription와 highRiskSentences[].reason은 **반드시 한국어로만** 작성하세요. " +
            			    "영어는 원문 광고 문구(sentence)가 영어일 때만 그대로 유지하세요. " +
            			    "불확실해도 스키마를 지키고, 이유를 한국어로 간단히 설명하세요.";
            Map<String, Object> body = Map.of(
                    "model", model,
                    "input", List.of(
                            Map.of("role", "developer", "content", developerMsg),
                            Map.of("role", "user", "content", adContent)
                    ),
                    "text", Map.of("format", Map.of(
                            "type", "json_schema",
                            "name", "truthify_ad_analysis",
                            "strict", true,
                            "schema", schema
                    ))
            );

            JsonNode res = openAiWebClient.post()
                    .uri("/responses")
                    .bodyValue(body)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, r ->
                            r.bodyToMono(String.class)
                             .defaultIfEmpty("")
                             .flatMap(errBody -> {
                                 // ✅ 여기서 원인이 다 나옴 (401, 429, etc)
                                 log.error("[OpenAI] status={} body={}", r.statusCode(), errBody);
                                 return Mono.error(new RuntimeException(
                                         "OpenAI HTTP " + r.statusCode() + " / " + errBody
                                 ));
                             })
                    )
                    .bodyToMono(JsonNode.class)
                    .block();

            String jsonText = (res != null && res.hasNonNull("output_text"))
                    ? res.get("output_text").asText()
                    : extractTextFromOutput(res);

            if (jsonText == null || jsonText.isBlank()) {
                log.error("[OpenAI] Empty output_text. raw={}", res);
                throw new RuntimeException("OpenAI 응답에 output_text가 비어있음");
            }

            TruthifyAiOutput parsed = objectMapper.readValue(jsonText, TruthifyAiOutput.class);

            AnalysisResultDto dto = new AnalysisResultDto();
            dto.setSuccess(parsed.success());
            dto.setDetailDescription(parsed.detailDescription());
            dto.setExgCount(parsed.exgCount());
            dto.setBanWordCount(0);
            dto.setEntities(objectMapper.writeValueAsString(parsed.highRiskSentences()));
            return dto;

        } catch (WebClientResponseException e) {
            log.error("[OpenAI] WebClientResponseException status={} body={}",
                    e.getStatusCode(), e.getResponseBodyAsString(), e);

            return fallback("OpenAI 분석 실패 (HTTP " + e.getStatusCode() + ")");

        } catch (Exception e) {
            log.error("[OpenAI] Exception", e);
            return fallback("OpenAI 분석 실패 (서버 로그 확인)");
        }
    }

    private AnalysisResultDto fallback(String msg) {
        AnalysisResultDto fallback = new AnalysisResultDto();
        fallback.setSuccess(false);
        fallback.setDetailDescription(msg);
        fallback.setExgCount(0);
        fallback.setBanWordCount(0);
        fallback.setEntities("[]");
        return fallback;
    }

    private Map<String, Object> buildTruthifySchema() {
        return Map.of(
                "type", "object",
                "additionalProperties", false,
                "properties", Map.of(
                        "success", Map.of("type", "boolean"),
                        "exgCount", Map.of("type", "integer", "minimum", 0),
                        "detailDescription", Map.of("type", "string"),
                        "highRiskSentences", Map.of(
                                "type", "array",
                                "items", Map.of(
                                        "type", "object",
                                        "additionalProperties", false,
                                        "properties", Map.of(
                                                "sentence", Map.of("type", "string"),
                                                "reason", Map.of("type", "string")
                                        ),
                                        "required", List.of("sentence", "reason")
                                )
                        )
                ),
                "required", List.of("success", "exgCount", "detailDescription", "highRiskSentences")
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

    public record TruthifyAiOutput(
            boolean success,
            int exgCount,
            String detailDescription,
            List<HighRisk> highRiskSentences
    ) {
        public record HighRisk(String sentence, String reason) {}
    }
}
