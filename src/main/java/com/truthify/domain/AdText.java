package com.truthify.domain;

import java.time.LocalDateTime;
import com.truthify.domain.user.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdText {
	private Long id;
	private Member member;
	private String textContent;
	private Integer riskScore;
	private LocalDateTime regDate;
	private boolean isPublic;
	private LocalDateTime publicAt;
}