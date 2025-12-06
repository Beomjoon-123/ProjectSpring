package com.truthify.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdTextResponse {

	private String originalText;
	private Boolean isTrue;
	private Double confidenceScore;
	private String message;
}
