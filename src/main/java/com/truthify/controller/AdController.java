package com.truthify.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.truthify.config.auth.dto.SessionUser;
import com.truthify.ad.dto.AdAnalyzeRequest; // 💡 개별 파일 임포트
import com.truthify.ad.dto.AdAnalyzeResponse; // 💡 개별 파일 임포트
import com.truthify.ad.dto.AdFeedbackRequest; // 💡 개별 파일 임포트
import com.truthify.service.AdService;
import com.truthify.user.dto.ResultData;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ad")
public class AdController {
    
    private final AdService adService;

//    @PostMapping("/analyze")
//    public ResultData<AdAnalyzeResponse> analyzeAdText(
//            @RequestBody AdAnalyzeRequest request,
//            @AuthenticationPrincipal SessionUser principal) {
//        
//        if (principal == null) {
//            return ResultData.of("F-3", "로그인이 필요한 서비스입니다");
//        }
//
//        try {
//            // 1. AI 분석 및 DB 저장 후 Response DTO 반환
//            AdAnalyzeResponse response = adService.analyzeAndSaveAdText(request, principal);
//            
//            return ResultData.of("S-1", "광고 분석 완료", response);
//        } catch (IllegalArgumentException e) {
//            return ResultData.of("F-4", e.getMessage());
//        } catch (RuntimeException e) {
//            return ResultData.of("F-5", e.getMessage());
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResultData.of("F-2", "서버 오류");
//        }
//    }

    @PostMapping("/feedback")
    public ResultData<?> submitFeedback(
            @RequestBody AdFeedbackRequest request,
            @AuthenticationPrincipal SessionUser principal) {

        if (principal == null) {
            return ResultData.of("F-3", "로그인이 필요한 서비스입니다");
        }
        
        try {
            adService.submitFeedback(request, principal);
            return ResultData.of("S-1", "피드백이 저장되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResultData.of("F-4", e.getMessage());
        } catch (Exception e) {
            return ResultData.of("F-2", "서버 오류");
        }
    }
}