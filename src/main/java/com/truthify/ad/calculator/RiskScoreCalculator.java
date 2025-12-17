package com.truthify.ad.calculator;

import org.springframework.stereotype.Component;
import com.truthify.dto.AnalysisResultDto;

@Component
public class RiskScoreCalculator {

    public double calculate(AnalysisResultDto dto) {
        int exg = dto.getExgCount() != null ? dto.getExgCount() : 0;
        int ban = dto.getBanWordCount() != null ? dto.getBanWordCount() : 0;

        // 가중치 예시 (너가 원하는 대로 조절 가능)
        double score = exg * 15 + ban * 25;

        return Math.min(score, 100.0);
    }
}
