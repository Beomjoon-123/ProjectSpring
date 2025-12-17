package com.truthify.domain.ad;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.truthify.ad.dto.AdFeedbackRequest;
import com.truthify.user.dto.CustomUserDetails;
import com.truthify.user.dto.ResultData;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ad/{adTextId}/feedback")
public class FeedbackController {

	private final FeedbackService feedbackService;

	@PostMapping
	public ResultData<?> vote(@PathVariable Long adTextId, @RequestBody AdFeedbackRequest req,
			@AuthenticationPrincipal CustomUserDetails user) {

		if (user == null) {
			return ResultData.of("F-3", "로그인이 필요합니다");
		}

		// ✅ trustScore 값 검증 (0/1만 허용)
		int trustScore = req.getTrustScore();
		if (trustScore != 0 && trustScore != 1) {
			return ResultData.of("F-1", "trustScore는 0 또는 1만 가능합니다");
		}

		FeedbackAction action = feedbackService.vote(user.getMember(), adTextId, trustScore);

		return switch (action) {
		case CREATED -> ResultData.of("S-1", "투표 완료");
		case UPDATED -> ResultData.of("S-2", "투표 변경");
		case DUPLICATE -> ResultData.of("F-1", "이미 투표하셨습니다");
		};
	}

	@GetMapping("/stats")
	public ResultData<?> stats(@PathVariable Long adTextId) {
		return ResultData.of("S-1", "통계 조회 성공", feedbackService.getStats(adTextId));
	}

	@GetMapping("/me")
	public ResultData<?> myVote(@PathVariable Long adTextId, @AuthenticationPrincipal CustomUserDetails user) {

		if (user == null) {
			return ResultData.of("S-1", "비로그인", null);
		}

		return ResultData.of("S-1", "조회 성공", feedbackService.getMyVote(user.getMember(), adTextId));
	}
}
