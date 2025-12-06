package com.truthify.domain;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BannedWord {
	private Long id;
	private String keyword;
	private LawType lawType;
	private String description;
	private LocalDateTime regDate;
	private Boolean isActive = true;

	@Builder
	public BannedWord(String keyword, LawType lawType, String description) {
		this.keyword = keyword;
		this.lawType = lawType;
		this.description = description;
		this.regDate = LocalDateTime.now();
		this.isActive = true;
	}

	public enum LawType {
		FOOD_SAFETY, FAIR_TRADE, FINANCIAL, MEDICAL, ETC
	}
}