package com.truthify.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.truthify.ad.calculator.RiskScoreCalculator;
import com.truthify.ad.dto.AdAnalysisDetailResponse;
import com.truthify.ad.dto.AdAnalyzeRequest;
import com.truthify.ad.dto.AdAnalyzeResponse;
import com.truthify.ad.dto.MyAdHistoryItem;
import com.truthify.ad.dto.PublicAdItem;
import com.truthify.ad.dto.PublicAdPageResponse;
import com.truthify.dao.AdTextMapper;
import com.truthify.domain.user.Member;
import com.truthify.dto.AnalysisResultDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdAnalysisService {
	
	private final AdTextMapper adTextMapper;
    private final AiClientService aiClientService;
    private final RiskScoreCalculator riskScoreCalculator;
    private final AdPersistenceService persistenceService;
    private final BanWordMatchService banwordMatchService;
    
    @Transactional
    public AdAnalyzeResponse analyze(AdAnalyzeRequest request, Member member) {

        String content = request.getAdContent(); // ✅ 이 줄 꼭 필요!

        AnalysisResultDto aiResult = aiClientService.analyze(content);

        BanWordMatchService.MatchResult matchResult = banwordMatchService.match(content); // ✅ 타입 명시
        aiResult.setBanWordCount(matchResult.getCount()); // ✅ count() 아님!

        double riskScore = riskScoreCalculator.calculate(aiResult);

        Long adTextId = persistenceService.saveAdText(member, content, riskScore);
        persistenceService.saveAnalysisResult(adTextId, aiResult, riskScore);

        return new AdAnalyzeResponse(
                adTextId,
                riskScore,
                aiResult.getDetailDescription(),
                aiResult.getDetailDescription()
        );
    }

    
    @Transactional(readOnly = true)
    public AdAnalysisDetailResponse getAnalysisDetail(Long adTextId, Member member) {

        var row = adTextMapper.findAnalysisDetail(adTextId);

        if (row == null) {
            throw new IllegalArgumentException("분석 결과 없음");
        }
        
        boolean isOwner =
                member != null && row.getUserId().equals(member.getId());

            boolean isPublic = Boolean.TRUE.equals(row.getIsPublic());

            if (!isPublic && !isOwner) {
                throw new IllegalStateException("접근 권한이 없습니다");
            }
         BanWordMatchService.MatchResult matchResult = banwordMatchService.match(row.getContent());
        return AdAnalysisDetailResponse.builder()
                .adTextId(row.getAdTextId())
                .adText(row.getContent())
                .riskScore(row.getRiskScore())
                .exgCount(row.getExgCount())
                .banWordCount(row.getBanWordCount())
                .summary(row.getSummary())
                .highRiskSentences(parseHighRiskSentences(row.getHighRiskSentences()))
                .isPublic(isPublic)
                .matchedBanWords(matchResult.getMatchedKeywords())
                .isOwner(isOwner)
                .build();
    }

    @Transactional(readOnly = true)
    public List<MyAdHistoryItem> getMyHistory(Member member) {
        return adTextMapper.findMyHistoryByUserId(member.getId());
    }

    private List<AdAnalysisDetailResponse.HighRiskSentence>
    parseHighRiskSentences(String json) {

        if (json == null || json.isBlank()) {
            return List.of();
        }

        try {
            ObjectMapper mapper = new ObjectMapper();

            return mapper.readValue(
                json,
                new TypeReference<List<AdAnalysisDetailResponse.HighRiskSentence>>() {}
            );

        } catch (Exception e) {
            return List.of();
        }
    }
    
    @Transactional
    public void makePublic(Member member, Long adTextId) {
    	int updated = adTextMapper.makePublic(adTextId, member.getId());
    	
    	if(updated == 0) {
    		throw new IllegalStateException("공개 권한이 없습니다");
    	}
    }
    
    @Transactional(readOnly = true)
    public List<PublicAdItem> getPublicAds() {
    	return adTextMapper.findPublicAds();
    }
    
    @Transactional
    public void setVisibility(Member member, Long adTextId, boolean isPublic) {
    	int updated = adTextMapper.updateVisibility(adTextId, member.getId(), isPublic);
    	
    	if (updated == 0) {
    		throw new IllegalStateException("권한이 없습니다");
    	}
    }
    
    @Transactional(readOnly = true)
    public PublicAdPageResponse getPublicAdsPaged(int page, int size) {
    	int safeSize = Math.max(1,  Math.min(size, 30));
    	int safePage = Math.max(0, page);
    	
    	long totalCount = adTextMapper.countPublicAds();
    	int totalPages = (int) Math.ceil((double) totalCount /safeSize);
    	int offset = safePage * safeSize;
    	var items = adTextMapper.findPublicAdsPage(safeSize, offset);
    	
    	return new PublicAdPageResponse(items, safePage, safeSize, totalCount, totalPages);
    }
}