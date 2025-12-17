package com.truthify.controller;

import com.truthify.ad.dto.AdAnalysisDetailResponse;
import com.truthify.ad.dto.AdAnalyzeRequest;
import com.truthify.ad.dto.AdAnalyzeResponse;
import com.truthify.ad.dto.AdVisibilityRequest;
import com.truthify.service.AdAnalysisService;
import com.truthify.user.dto.CustomUserDetails;
import com.truthify.user.dto.ResultData;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ad")
public class AdController {

    private final AdAnalysisService adAnalysisService;

    @PostMapping("/analyze")
    public ResultData<AdAnalyzeResponse> analyzeAdText(
            @RequestBody AdAnalyzeRequest request,
            @AuthenticationPrincipal CustomUserDetails user) {

        if (user == null) {
            return ResultData.of("F-3", "로그인이 필요한 서비스입니다");
        }

        return ResultData.of(
            "S-1",
            "광고 분석 완료",
            adAnalysisService.analyze(request, user.getMember())
        );
    }

    @GetMapping("/{adTextId}/detail")
    public ResultData<AdAnalysisDetailResponse> getAnalysisDetail(
            @PathVariable Long adTextId,
            @AuthenticationPrincipal CustomUserDetails user) {

        return ResultData.of(
            "S-1",
            "분석 상세 조회 성공",
            adAnalysisService.getAnalysisDetail(
                adTextId,
                user != null ? user.getMember() : null
            )
        );
    }

    @GetMapping("/my")
    public ResultData<?> getMyAdHistory(
            @AuthenticationPrincipal CustomUserDetails user) {

        if (user == null) {
            return ResultData.of("F-3", "로그인이 필요합니다");
        }

        return ResultData.of(
            "S-1",
            "내 분석 히스토리 조회 성공",
            adAnalysisService.getMyHistory(user.getMember())
        );
    }

    @PostMapping("/{adTextId}/public")
    public ResultData<?> makePublic(
            @PathVariable Long adTextId,
            @AuthenticationPrincipal CustomUserDetails user) {

        if (user == null) {
            return ResultData.of("F-3", "로그인이 필요합니다");
        }

        adAnalysisService.makePublic(user.getMember(), adTextId);
        return ResultData.of("S-1", "분석 결과가 공개되었습니다");
    }
    
    @GetMapping("/public") 
    public ResultData<?> getPublicAds(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "6") int size) {
    	return ResultData.of("S-1", "공개된 분석 목록", adAnalysisService.getPublicAdsPaged(page, size));
    }
    
    @PatchMapping("/{adTextId}/visibility")
    public ResultData<?> setVisibility(@PathVariable Long adTextId, @RequestBody AdVisibilityRequest req, @AuthenticationPrincipal CustomUserDetails user) {
    	
    	if (user == null) return ResultData.of("F-3", "로그인이 필요한 서비스입니다");
    	if (req.getIsPublic() == null) return ResultData.of("F-1", "isPublic 값 필요");
    	
    	adAnalysisService.setVisibility(user.getMember(), adTextId, req.getIsPublic());
    	return ResultData.of("S-1", req.getIsPublic() ? "공개로 전환되었습니다" : "비공개로 전환되었습니다");
    }
}
