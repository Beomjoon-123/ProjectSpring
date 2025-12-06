package com.truthify.controller;

import com.truthify.dto.AdTextRequest;
import com.truthify.dto.AdTextResponse;
import com.truthify.service.AdTextService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/adtext")
public class AdTextController {

	private final AdTextService adTextService;

	/**
	 * 광고 문장 분석 POST /api/adtext/analyze
	 */
	@PostMapping("/analyze")
	public AdTextResponse analyzeAdText(@RequestBody AdTextRequest request) {

		return adTextService.analyzeAdText(request);
	}
}
