package com.truthify.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.truthify.dao.AdTextMapper;
import com.truthify.domain.AdText;
import com.truthify.domain.AnalysisResult;
import com.truthify.domain.user.Member;
import com.truthify.dto.AnalysisResultDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdPersistenceService {

    private final AdTextMapper adTextMapper;

    @Transactional
    public Long saveAdText(Member member, String content, double riskScore) {
        AdText adText = AdText.builder()
                .member(member)
                .textContent(content)
                .riskScore((int) riskScore)
                .build();

        adTextMapper.saveAdText(adText);
        return adText.getId();
    }

    @Transactional
    public void saveAnalysisResult(Long adTextId, AnalysisResultDto dto, double riskScore) {
        AnalysisResult result = AnalysisResult.builder()
                .adTextId(adTextId)
                .exgCount(dto.getExgCount())
                .banWordCount(dto.getBanWordCount())
                .summary(dto.getDetailDescription())
                .riskScore(riskScore)
                .highRiskSentences(dto.getEntities())
                .build();

        adTextMapper.saveAnalysisResult(result);
    }
}